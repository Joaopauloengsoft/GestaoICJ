package com.gestaoicj.app.data.repository

import android.util.Log
import com.gestaoicj.app.data.local.dao.UsuarioDao
import com.gestaoicj.app.data.local.entity.UsuarioEntity
import com.gestaoicj.app.data.remote.FirestoreDataSource
import com.gestaoicj.app.data.remote.toUsuarioEntity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val remoto: FirestoreDataSource,
    private val usuarioDao: UsuarioDao,
    private val sync: SyncRepository,
    private val scheduler: SyncScheduler
) {

    // uid de quem está logado (null se ninguém)
    val uidAtual: String? get() = auth.currentUser?.uid

    // O Firebase guarda a sessão: ao reabrir o app, continua logado.
    // A tela inicial usa isto para decidir entre Login e Estoque.
    val logado: Boolean get() = auth.currentUser != null

    // UC01 - Cadastrar conta (exige internet)
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
        remoto.salvarUsuario(usuario)      // cria usuarios/{uid} no Firestore
        usuarioDao.salvar(usuario)         // e no Room
    }

    // UC02 - Entrar (exige internet)
    suspend fun entrar(email: String, senha: String): Result<Unit> = resultado {
        require(email.isNotBlank()) { "Informe o e-mail" }
        require(senha.isNotBlank()) { "Informe a senha" }

        val user = auth.signInWithEmailAndPassword(email.trim(), senha).await().user
            ?: error("Não foi possível entrar")

        val doc = remoto.buscarUsuario(user.uid)
        val usuario = if (doc.exists()) {
            doc.toUsuarioEntity()
        } else {
            // Conta existe no Auth, mas o documento não foi criado (falha no cadastro): cria agora
            UsuarioEntity(
                id = user.uid,
                nome = email.substringBefore("@"),
                email = email.trim(),
                createdAt = System.currentTimeMillis(),
            ).also { remoto.salvarUsuario(it) }
        }
        usuarioDao.salvar(usuario)         // precisa existir no Room antes dos produtos (chave estrangeira)

        try {
            sync.baixarTudo(user.uid)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Se o download falhar, o usuário entra mesmo assim com os dados locais
            Log.w("AuthRepository", "Falha ao baixar dados após o login", e)
        }
        scheduler.agendar()      // envia o que ficou pendente
    }

    // UC11 - Recuperar senha
    suspend fun recuperarSenha(email: String): Result<Unit> = resultado {
        require(email.isNotBlank()) { "Informe o e-mail" }
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    // UC10 - Sair da conta
    fun sair() = auth.signOut()

    // UC10 - Editar o nome (exige internet)
    suspend fun atualizarNome(nome: String): Result<Unit> = resultado {
        val uid = uidAtual ?: error("Sessão expirada. Entre novamente.")
        require(nome.isNotBlank()) { "Informe seu nome" }

        val atual = usuarioDao.buscarPorId(uid) ?: error("Usuário não encontrado")
        val novo = atual.copy(nome = nome.trim())
        remoto.salvarUsuario(novo)
    }

    // UC10 - Trocar senha (exige internet)
    suspend fun alterarSenha(senhaAtual: String, novaSenha: String): Result<Unit> = resultado {
        val user = auth.currentUser ?: error("Sessão expirada. Entre novamente.")
        val email = user.email ?: error("Conta sem e-mail")
        require(novaSenha.length >= 6) { "A nova senha deve ter pelo menos 6 caracteres" }

        // O Firebase exige login recente para operações sensíveis: reautentica primeiro
        user.reauthenticate(EmailAuthProvider.getCredential(email, senhaAtual)).await()
        user.updatePassword(novaSenha).await()
    }
}