package com.gestaoicj.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movimentos_estoque",
    foreignKeys = [
        ForeignKey(
            entity = ProdutoEntity::class,
            parentColumns = ["id"],
            childColumns = ["produto_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["produto_id"]),
        Index(value = ["usuario_id", "data_hora"])
    ]
)
data class MovimentoEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "produto_id")
    val produtoId: String,

    // QUEM FEZ: id e nome de quem realizou a operação (o nome fica gravado na hora)
    @ColumnInfo(name = "usuario_id")
    val usuarioId: String,

    @ColumnInfo(name = "usuario_nome")
    val usuarioNome: String,

    val tipo: String,                     // "ENTRADA" ou "SAIDA"

    val motivo: String? = null,           // "VENDA" ou "RETIRADA" (só quando tipo = SAIDA)

    val quantidade: Int,

    @ColumnInfo(name = "data_hora")
    val dataHora: Long,

    val observacao: String? = null,

    val sincronizado: Boolean = false
)