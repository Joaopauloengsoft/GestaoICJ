package com.gestaoicj.app.data.remote

import com.gestaoicj.app.data.local.entity.MovimentoEntity
import com.gestaoicj.app.data.local.entity.ProdutoEntity
import com.gestaoicj.app.data.local.entity.TipoMovimento
import com.gestaoicj.app.data.local.entity.UsuarioEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

// ---------- Room (entidade) -> Firestore (mapa) ----------

fun ProdutoEntity.toMap(): Map<String, Any?> = mapOf(
    "codigoBarras" to codigoBarras,
    "nome" to nome,
    "quantidade" to quantidade,
    "estoqueMinimo" to estoqueMinimo,
    "unidade" to unidade,
    "createdAt" to Timestamp(Date(createdAt)),
    "updatedAt" to Timestamp(Date(updatedAt)),
    "excluido" to excluido
)

fun MovimentoEntity.toMap(): Map<String, Any?> = mapOf(
    "produtoId" to produtoId,
    "usuarioId" to usuarioId,
    "usuarioNome" to usuarioNome,
    "tipo" to tipo,
    "motivo" to motivo,
    "quantidade" to quantidade,
    "dataHora" to Timestamp(Date(dataHora)),
    "observacao" to observacao
)


// ---------- Firestore (documento) -> Room (entidade) ----------

fun DocumentSnapshot.toUsuarioEntity(): UsuarioEntity = UsuarioEntity(
    id = id,
    nome = getString("nome") ?: "",
    email = getString("email") ?: "",
    createdAt = getTimestamp("createdAt")?.toDate()?.time ?: System.currentTimeMillis()
)

fun DocumentSnapshot.toProdutoEntity(uid: String): ProdutoEntity = ProdutoEntity(
    id = id,
    usuarioId = uid,
    codigoBarras = getString("codigoBarras") ?: "",
    nome = getString("nome") ?: "",
    quantidade = (getLong("quantidade") ?: 0L).toInt(),
    estoqueMinimo = (getLong("estoqueMinimo") ?: 0L).toInt(),
    unidade = getString("unidade") ?: "un",
    createdAt = getTimestamp("createdAt")?.toDate()?.time ?: 0L,
    updatedAt = getTimestamp("updatedAt")?.toDate()?.time ?: 0L,
    sincronizado = true,                      // veio da nuvem, então já está sincronizado
    excluido = getBoolean("excluido") ?: false
)

fun DocumentSnapshot.toMovimentoEntity(uid: String): MovimentoEntity = MovimentoEntity(
    id = id,
    produtoId = getString("produtoId") ?: "",
    usuarioId = getString("usuarioId") ?: uid,
    usuarioNome = getString("usuarioNome") ?: "",
    tipo = getString("tipo") ?: TipoMovimento.ENTRADA.name,
    motivo = getString("motivo"),
    quantidade = (getLong("quantidade") ?: 0L).toInt(),
    dataHora = getTimestamp("dataHora")?.toDate()?.time ?: 0L,
    observacao = getString("observacao"),
    sincronizado = true
)