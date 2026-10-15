package com.gestaoicj.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.gestaoicj.app.data.local.entity.MovimentoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimentoDao {

    // Movimentos nunca são alterados depois de criados; IGNORE evita duplicar
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserir(movimento: MovimentoEntity)

    // Usado ao baixar o histórico do Firestore
    @Upsert
    suspend fun salvarTodos(movimentos: List<MovimentoEntity>)

    // Tela Histórico (UC08). Filtros opcionais:
    //  tipo = null, motivo = null  -> sem filtro
    //  inicio = 0, fim = Long.MAX_VALUE -> sem filtro de período
    @Query(
        """
        SELECT m.id, m.produto_id, p.nome AS produto_nome, m.tipo, m.motivo,
               m.quantidade, m.data_hora, m.observacao, m.usuario_nome
        FROM movimentos_estoque m
        INNER JOIN produtos p ON p.id = m.produto_id
        WHERE p.usuario_id = :uid
          AND (:tipo IS NULL OR m.tipo = :tipo)
          AND (:motivo IS NULL OR m.motivo = :motivo)
          AND m.data_hora BETWEEN :inicio AND :fim
        ORDER BY m.data_hora DESC
        """
    )
    fun observarHistorico(
        uid: String,
        tipo: String?,
        motivo: String?,
        inicio: Long,
        fim: Long
    ): Flow<List<MovimentoComProduto>>

    // Sincronização (Etapa 9)
    @Query("SELECT * FROM movimentos_estoque WHERE usuario_id = :uid AND sincronizado = 0")
    suspend fun pendentes(uid: String): List<MovimentoEntity>

    @Query("UPDATE movimentos_estoque SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizado(id: String)
}