package com.gestaoicj.app.data.repository

import com.gestaoicj.app.data.local.dao.MovimentoDao
import com.gestaoicj.app.data.local.dao.ProdutoDao
import com.gestaoicj.app.data.remote.toMap
import com.gestaoicj.app.data.remote.toMovimentoEntity
import com.gestaoicj.app.data.remote.toProdutoEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SyncRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val produtoDao: ProdutoDao,
    private val movimentoDao: MovimentoDao
) {

    suspend fun enviarPendentes(uid: String) {
        val ref = db.collection("usuarios").document(uid)
        for (produto in produtoDao.pendentes(uid)) {
            ref.collection("produtos").document(produto.id).set(produto.toMap()).await()
            produtoDao.marcarSincronizado(produto.id, produto.updatedAt)
        }
        for (movimento in movimentoDao.pendentes(uid)) {
            ref.collection("movimentos").document(movimento.id).set(movimento.toMap()).await()
            movimentoDao.marcarSincronizado(movimento.id)
        }
    }

    // Produtos antes de movimentos por causa da FK no Room
    suspend fun baixarTudo(uid: String) {
        val ref = db.collection("usuarios").document(uid)

        for (daNuvem in ref.collection("produtos").get().await().documents.map { it.toProdutoEntity(uid) }) {
            val local = produtoDao.buscarPorId(daNuvem.id)
            val deveSobrescrever = local == null ||
                    (local.sincronizado && daNuvem.updatedAt >= local.updatedAt)
            if (deveSobrescrever) produtoDao.salvar(daNuvem)
        }

        movimentoDao.salvarTodos(
            ref.collection("movimentos").get().await().documents.map { it.toMovimentoEntity(uid) }
        )
    }
}
