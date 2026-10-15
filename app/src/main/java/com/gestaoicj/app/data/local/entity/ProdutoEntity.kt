package com.gestaoicj.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "produtos",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["usuario_id", "codigo_barras"], unique = true)
    ]
)
data class ProdutoEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "usuario_id")
    val usuarioId: String,

    @ColumnInfo(name = "codigo_barras")
    val codigoBarras: String,

    val nome: String,

    val quantidade: Int,

    @ColumnInfo(name = "estoque_minimo")
    val estoqueMinimo: Int = 0,

    val unidade: String = "un",

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    val sincronizado: Boolean = false,

    val excluido: Boolean = false
)