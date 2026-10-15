package com.gestaoicj.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val id: String,                                   // uid do Firebase Auth

    val nome: String,

    val email: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long                               // milissegundos desde 1970
)