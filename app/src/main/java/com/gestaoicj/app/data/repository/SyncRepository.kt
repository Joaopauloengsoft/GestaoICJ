package com.gestaoicj.app.data.repository

import com.gestaoicj.app.data.local.dao.MovimentoDao
import com.gestaoicj.app.data.local.dao.ProdutoDao
import com.gestaoicj.app.data.remote.FirestoreDataSource
import javax.inject.Inject

class SyncRepository @Inject constructor(
    private val remoto: FirestoreDataSource,
    private val produtoDao: ProdutoDao,
    private val movimentoDao: MovimentoDao
) {

    // Room -> Firestore: envia tudo que está com sincronizado = 0
    suspend fun enviarPendentes(uid: String) {
        for (produto in produtoDao.pendentes(uid)) {
            remoto.enviarProduto(uid, produto)
            produtoDao.marcarSincronizado(produto.id, produto.updatedAt)
        }
        for (movimento in movimentoDao.pendentes(uid)) {
            remoto.enviarMovimento(uid, movimento)
            movimentoDao.marcarSincronizado(movimento.id)
        }
    }

    // Firestore -> Room: usado no login e em aparelho novo
    suspend fun baixarTudo(uid: String) {
        // Produtos primeiro, porque os movimentos dependem deles (chave estrangeira)
        for (daNuvem in remoto.baixarProdutos(uid)) {
            val local = produtoDao.buscarPorId(daNuvem.id)
            // Regra: a alteração mais recente vence, e nunca sobrescreve
            // uma alteração local que ainda não foi enviada
            val deveSobrescrever = local == null ||
                    (local.sincronizado && daNuvem.updatedAt >= local.updatedAt)
            if (deveSobrescrever) produtoDao.salvar(daNuvem)
        }
        movimentoDao.salvarTodos(remoto.baixarMovimentos(uid))
    }
}