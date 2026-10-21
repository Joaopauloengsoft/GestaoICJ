package com.gestaoicj.app.ui.mapper

import com.gestaoicj.app.data.local.dao.MovimentoComProduto
import com.gestaoicj.app.data.local.entity.ProdutoEntity
import com.gestaoicj.app.data.local.entity.TipoMovimento
import com.gestaoicj.app.data.local.entity.UsuarioEntity
import com.gestaoicj.app.ui.estoque.Produto
import com.gestaoicj.app.ui.historico.Movimentacao
import com.gestaoicj.app.ui.historico.TipoMovimentacao
import com.gestaoicj.app.ui.perfil.Perfil
import com.gestaoicj.app.ui.reposicao.ProdutoParaRepor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PT_BR = Locale("pt", "BR")

fun formatarDataHora(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy · HH:mm", PT_BR).format(Date(millis))

fun formatarDia(millis: Long): String =
    SimpleDateFormat("dd/MM", PT_BR).format(Date(millis))

fun formatarDiaCompleto(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", PT_BR).format(Date(millis))

fun ProdutoEntity.paraTela() = Produto(
    id = id,
    nome = nome,
    codigo = codigoBarras,
    categoria = "Em estoque",
    saldo = quantidade,
    unidade = unidade,
    estoqueBaixo = quantidade <= estoqueMinimo,
)

fun ProdutoEntity.paraReposicao() = ProdutoParaRepor(
    id = id,
    nome = nome,
    codigo = codigoBarras,
    estoqueAtual = quantidade,
    estoqueMinimo = estoqueMinimo,
    unidade = unidade,
)

fun MovimentoComProduto.paraTela() = Movimentacao(
    id = id,
    tipo = if (tipo == TipoMovimento.ENTRADA.name) TipoMovimentacao.Entrada else TipoMovimentacao.Saida,
    produto = produtoNome,
    dataHora = formatarDataHora(dataHora),
    quantidade = quantidade,
    unidade = produtoUnidade,
    numeroVenda = null,
)

fun UsuarioEntity?.paraPerfil() = Perfil(
    nome = this?.nome ?: "—",
    cargo = "—",
    email = this?.email ?: "—",
    telefone = "—",
    organizacao = "—",
)
