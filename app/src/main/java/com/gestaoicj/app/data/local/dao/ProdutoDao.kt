package com.gestaoicj.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gestaoicj.app.data.local.entity.ProdutoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {

    @Upsert
    suspend fun salvar(produto: ProdutoEntity)

    // Tela Estoque (UC07). Busca vazia ("") devolve todos os produtos.
    @Query(
        """
        SELECT * FROM produtos
        WHERE usuario_id = :uid
          AND excluido = 0
          AND (:busca = '' OR nome LIKE '%' || :busca || '%'
                           OR codigo_barras LIKE '%' || :busca || '%')
        ORDER BY nome
        """
    )
    fun observarEstoque(uid: String, busca: String): Flow<List<ProdutoEntity>>

    // Tela Reposição (UC09)
    @Query(
        """
        SELECT * FROM produtos
        WHERE usuario_id = :uid
          AND excluido = 0
          AND quantidade <= estoque_minimo
        ORDER BY nome
        """
    )
    fun observarReposicao(uid: String): Flow<List<ProdutoEntity>>

    // Scanner (UC03). Inclui produtos excluídos: se achar um, reative-o
    // em vez de criar outro (o código é único por usuário).
    @Query("SELECT * FROM produtos WHERE usuario_id = :uid AND codigo_barras = :codigo LIMIT 1")
    suspend fun buscarPorCodigo(uid: String, codigo: String): ProdutoEntity?

    @Query("SELECT * FROM produtos WHERE id = :id")
    suspend fun buscarPorId(id: String): ProdutoEntity?

    // Exclusão lógica (UC12): o produto some das telas, mas o histórico continua válido
    @Query("UPDATE produtos SET excluido = 1, updated_at = :agora, sincronizado = 0 WHERE id = :id")
    suspend fun marcarExcluido(id: String, agora: Long)

    // Sincronização (Etapa 9)
    @Query("SELECT * FROM produtos WHERE usuario_id = :uid AND sincronizado = 0")
    suspend fun pendentes(uid: String): List<ProdutoEntity>

    // Só marca como sincronizado se o produto não mudou durante o envio
    @Query("UPDATE produtos SET sincronizado = 1 WHERE id = :id AND updated_at = :updatedAt")
    suspend fun marcarSincronizado(id: String, updatedAt: Long)
}