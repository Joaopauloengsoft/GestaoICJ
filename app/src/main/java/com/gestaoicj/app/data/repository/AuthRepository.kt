package com.gestaoicj.app.data.repository

import android.util.Log
import com.gestaoicj.app.data.local.dao.UsuarioDao
import com.gestaoicj.app.data.local.entity.UsuarioEntity
import com.gestaoicj.app.data.remote.toUsuarioEntity
import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val usuarioDao: UsuarioDao,
    private val sync: SyncRepository,
    private val scheduler: SyncScheduler
) {

    val uidAtual: String? get() = auth.currentUser?.uid

    val logado: Boolean get() = auth.currentUser != null

    val uidFlow: Flow<String?> = callbackFlow {
        val ouvinte = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        auth.addAuthStateListener(ouvinte)
        awaitClose { auth.removeAuthStateListener(ouvinte) }
    }.distinctUntilChanged()

    fun observarUsuario(uid: String): Flow<UsuarioEntity?> = usuarioDao.observar(uid)

    suspend fun cadastrar(nome: String, email: String, senha: String): Result<Unit> = resultado {
        require(nome.isNotBlank()) { "Informe seu nome" }
        require(email.isNotBlank()) { "Informe o e-mail" }
        require(senha.length >= 6) { "A senha deve ter pelo menos 6 caracteres" }

        val user = auth.createUserWithEmailAndPassword(email.trim(), senha).await().user
            ?: error("Não foi possível criar a conta")

        val usuario = UsuarioEntity(
            id = user.uid,
            nome = nome.trim(),
            email = email.trim(),
            createdAt = System.currentTimeMillis(),
        )

        db.collection("usuarios").document(usuario.id).set(
            mapOf(
                "nome" to usuario.nome,
                "email" to usuario.email,
                "createdAt" to Timestamp(Date(usuario.createdAt))
            ),
            SetOptions.merge()
        ).await()

        usuarioDao.salvar(usuario)
    }

    suspend fun entrar(email: String, senha: String): Result<Unit> = resultado {
        require(email.isNotBlank()) { "Informe o e-mail" }
        require(senha.isNotBlank()) { "Informe a senha" }

        val user = auth.signInWithEmailAndPassword(email.trim(), senha).await().user
            ?: error("Não foi possível entrar")

        val doc = db.collection("usuarios").document(user.uid).get().await()
        val usuario = if (doc.exists()) {
            doc.toUsuarioEntity()
        } else {
            UsuarioEntity(
                id = user.uid,
                nome = email.substringBefore("@"),
                email = email.trim(),
                createdAt = System.currentTimeMillis(),
            ).also { novo ->
                db.collection("usuarios").document(novo.id).set(
                    mapOf(
                        "nome" to novo.nome,
                        "email" to novo.email,
                        "createdAt" to Timestamp(Date(novo.createdAt))
                    ),
                    SetOptions.merge()
                ).await()
            }
        }
        usuarioDao.salvar(usuario)

        try {
            sync.baixarTudo(user.uid)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w("AuthRepository", "Falha ao baixar dados após o login", e)
        }
        scheduler.agendar()
        scheduler.iniciarPeriodico()
    }

    suspend fun recuperarSenha(email: String): Result<Unit> = resultado {
        require(email.isNotBlank()) { "Informe o e-mail" }
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    fun sair() = auth.signOut()

    suspend fun atualizarNome(nome: String): Result<Unit> = resultado {
        val uid = uidAtual ?: error("Sessão expirada. Entre novamente.")
        require(nome.isNotBlank()) { "Informe seu nome" }

        val atual = usuarioDao.buscarPorId(uid) ?: error("Usuário não encontrado")
        val novo = atual.copy(nome = nome.trim())

        db.collection("usuarios").document(novo.id).set(
            mapOf(
                "nome" to novo.nome,
                "email" to novo.email,
                "createdAt" to Timestamp(Date(novo.createdAt))
            ),
            SetOptions.merge()
        ).await()
    }

    suspend fun alterarSenha(senhaAtual: String, novaSenha: String): Result<Unit> = resultado {
        val user = auth.currentUser ?: error("Sessão expirada. Entre novamente.")
        val email = user.email ?: error("Conta sem e-mail")
        require(novaSenha.length >= 6) { "A nova senha deve ter pelo menos 6 caracteres" }

        // Firebase exige login recente para operações sensíveis
        user.reauthenticate(EmailAuthProvider.getCredential(email, senhaAtual)).await()
        user.updatePassword(novaSenha).await()
    }
}
