package com.gestaoicj.app.data.local.dao

import androidx.room.ColumnInfo

data class MovimentoComProduto(
    val id: String,

    @ColumnInfo(name = "produto_id")
    val produtoId: String,

    @ColumnInfo(name = "produto_nome")
    val produtoNome: String,

    val tipo: String,

    val quantidade: Int,

    @ColumnInfo(name = "data_hora")
    val dataHora: Long,

    val observacao: String?
)