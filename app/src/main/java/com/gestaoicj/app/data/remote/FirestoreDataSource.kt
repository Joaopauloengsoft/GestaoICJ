package com.gestaoicj.app.data.remote

import com.gestaoicj.app.data.local.entity.ProdutoEntity
import com.gestaoicj.app.data.local.entity.UsuarioEntity
import com.gestaoicj.app.data.local.entity.MovimentoEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FirestoreDataSource @Inject constructor(
    private val db: FirebaseFirestore
) {

    // usuarios/{uid}
    private fun usuario(uid: String) = db.collection("usuarios").document(uid)

    // ---------- Usuário ----------

    suspend fun salvarUsuario(u: UsuarioEntity) {
        usuario(u.id).set(
            mapOf(
                "nome" to u.nome,
                "email" to u.email,
                "createdAt" to Timestamp(Date(u.createdAt))
            ),
            SetOptions.merge()          // atualiza só esses campos, sem apagar os outros
        ).await()
    }

    suspend fun buscarUsuario(uid: String): DocumentSnapshot =
        usuario(uid).get().await()

    // ---------- Enviar (Room -> Firestore) ----------

    suspend fun enviarProduto(uid: String, produto: ProdutoEntity) {
        usuario(uid).collection("produtos")
            .document(produto.id)
            .set(produto.toMap())
            .await()
    }

    suspend fun enviarMovimento(uid: String, movimento: MovimentoEntity) {
        usuario(uid).collection("movimentos")
            .document(movimento.id)
            .set(movimento.toMap())
            .await()
    }

    // ---------- Baixar (Firestore -> Room) ----------

    suspend fun baixarProdutos(uid: String): List<ProdutoEntity> =
        usuario(uid).collection("produtos")
            .get().await()
            .documents
            .map { it.toProdutoEntity(uid) }

    suspend fun baixarMovimentos(uid: String): List<MovimentoEntity> =
        usuario(uid).collection("movimentos")
            .get().await()
            .documents
            .map { it.toMovimentoEntity(uid) }
}