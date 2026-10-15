package com.gestaoicj.app.data.repository

import androidx.room.withTransaction
import com.gestaoicj.app.data.local.AppDatabase
import com.gestaoicj.app.data.local.dao.MovimentoDao
import com.gestaoicj.app.data.local.dao.ProdutoDao
import com.gestaoicj.app.data.local.dao.UsuarioDao
import com.gestaoicj.app.data.local.entity.MotivoSaida
import com.gestaoicj.app.data.local.entity.MovimentoEntity
import com.gestaoicj.app.data.local.entity.ProdutoEntity
import com.gestaoicj.app.data.local.entity.TipoMovimento
import java.util.UUID
import javax.inject.Inject

class EstoqueRepository @Inject constructor(
    private val db: AppDatabase,
    private val produtoDao: ProdutoDao,
    private val movimentoDao: MovimentoDao,
    private val usuarioDao: UsuarioDao,
    private val scheduler: SyncScheduler
) {

    private companion object {
        const val QTD_MAX = 1_000_000
    }

    // ---------- Leituras (as telas observam estas listas) ----------

    fun observarEstoque(uid: String, busca: String = "") =
        produtoDao.observarEstoque(uid, busca.trim())

    fun observarReposicao(uid: String) =
        produtoDao.observarReposicao(uid)

    fun observarHistorico(
        uid: String,
        tipo: TipoMovimento? = null,
        motivo: MotivoSaida? = null,
        inicio: Long = 0L,
        fim: Long = Long.MAX_VALUE
    ) = movimentoDao.observarHistorico(uid, tipo?.name, motivo?.name, inicio, fim)

    // UC03: devolve o produto se existir e estiver ativo.
    // null = código desconhecido (ou produto excluído) -> a tela abre o cadastro
    suspend fun buscarProdutoAtivo(uid: String, codigo: String): ProdutoEntity? =
        produtoDao.buscarPorCodigo(uid, codigo.trim())?.takeIf { !it.excluido }

    // ---------- UC06: cadastrar produto novo ----------

    suspend fun cadastrarProduto(
        uid: String,
        codigo: String,
        nome: String,
        qtdInicial: Int,
        minimo: Int,
        unidade: String = "un"
    ): Result<ProdutoEntity> = resultado {
        val cod = codigo.trim()
        require(cod.isNotEmpty()) { "Código de barras inválido" }
        require(nome.isNotBlank()) { "Informe o nome do produto" }
        require(unidade.isNotBlank()) { "Informe a unidade" }
        require(qtdInicial in 0..QTD_MAX) { "Quantidade inicial inválida" }
        require(minimo in 0..QTD_MAX) { "Estoque mínimo inválido" }

        val agora = System.currentTimeMillis()

        val produto = db.withTransaction {
            val quem = nomeDe(uid)

            val existente = produtoDao.buscarPorCodigo(uid, cod)
            if (existente != null && !existente.excluido) {
                error("Já existe um produto com este código")
            }

            val salvo = if (existente != null) {
                // Produto excluído antes: reativa. Ele foi excluído com saldo zero,
                // então a quantidade volta a ser só a inicial.
                existente.copy(
                    nome = nome.trim(),
                    estoqueMinimo = minimo,
                    unidade = unidade.trim(),
                    quantidade = existente.quantidade + qtdInicial,
                    excluido = false,
                    updatedAt = agora,
                    sincronizado = false
                )
            } else {
                ProdutoEntity(
                    id = UUID.randomUUID().toString(),
                    usuarioId = uid,
                    codigoBarras = cod,
                    nome = nome.trim(),
                    quantidade = qtdInicial,
                    estoqueMinimo = minimo,
                    unidade = unidade.trim(),
                    createdAt = agora,
                    updatedAt = agora
                )
            }

            produtoDao.salvar(salvo)
            if (qtdInicial > 0) {
                movimentoDao.inserir(
                    novoMovimento(
                        uid, quem, salvo.id, TipoMovimento.ENTRADA, null,
                        qtdInicial, agora, "Estoque inicial"
                    )
                )
            }
            salvo
        }

        scheduler.agendar()
        produto
    }

    // ---------- UC04 / UC05: entrada e saída (venda ou retirada) ----------
    // Devolve a nova quantidade em estoque

    suspend fun registrarMovimento(
        uid: String,
        produtoId: String,
        tipo: TipoMovimento,
        quantidade: Int,
        motivo: MotivoSaida? = null,          // obrigatório quando tipo = SAIDA
        observacao: String? = null
    ): Result<Int> = resultado {
        require(quantidade > 0) { "A quantidade deve ser maior que zero" }
        require(quantidade <= QTD_MAX) { "Quantidade muito grande" }

        val motivoFinal: MotivoSaida? = when (tipo) {
            TipoMovimento.ENTRADA -> null
            TipoMovimento.SAIDA ->
                requireNotNull(motivo) { "Informe o motivo da saída: venda ou retirada" }
        }

        val novaQuantidade = db.withTransaction {
            val quem = nomeDe(uid)

            val produto = produtoDao.buscarPorId(produtoId)
                ?.takeIf { it.usuarioId == uid && !it.excluido }
                ?: error("Produto não encontrado")

            val nova = when (tipo) {
                TipoMovimento.ENTRADA -> produto.quantidade + quantidade
                TipoMovimento.SAIDA -> produto.quantidade - quantidade
            }
            check(nova >= 0) { "Estoque insuficiente (disponível: ${produto.quantidade})" }

            val agora = System.currentTimeMillis()
            produtoDao.salvar(
                produto.copy(quantidade = nova, updatedAt = agora, sincronizado = false)
            )
            movimentoDao.inserir(
                novoMovimento(
                    uid, quem, produto.id, tipo, motivoFinal, quantidade, agora,
                    observacao?.trim()?.ifEmpty { null }
                )
            )
            nova
        }

        scheduler.agendar()
        novaQuantidade
    }

    // ---------- UC12: editar e excluir (manual) ----------
    // A quantidade NÃO é editada aqui: só muda por entrada ou saída.

    suspend fun editarProduto(
        uid: String,
        produtoId: String,
        nome: String,
        minimo: Int,
        unidade: String
    ): Result<Unit> = resultado {
        require(nome.isNotBlank()) { "Informe o nome do produto" }
        require(unidade.isNotBlank()) { "Informe a unidade" }
        require(minimo in 0..QTD_MAX) { "Estoque mínimo inválido" }

        db.withTransaction {
            val produto = produtoDao.buscarPorId(produtoId)
                ?.takeIf { it.usuarioId == uid && !it.excluido }
                ?: error("Produto não encontrado")

            produtoDao.salvar(
                produto.copy(
                    nome = nome.trim(),
                    estoqueMinimo = minimo,
                    unidade = unidade.trim(),
                    updatedAt = System.currentTimeMillis(),
                    sincronizado = false
                )
            )
        }
        scheduler.agendar()
    }

    // Exclusão lógica. Se ainda houver saldo, ele sai do estoque como RETIRADA,
    // para que nenhuma unidade desapareça sem registro de quem fez.
    suspend fun excluirProduto(uid: String, produtoId: String): Result<Unit> = resultado {
        db.withTransaction {
            val quem = nomeDe(uid)

            val produto = produtoDao.buscarPorId(produtoId)
                ?.takeIf { it.usuarioId == uid && !it.excluido }
                ?: error("Produto não encontrado")

            val agora = System.currentTimeMillis()
            if (produto.quantidade > 0) {
                movimentoDao.inserir(
                    novoMovimento(
                        uid, quem, produto.id, TipoMovimento.SAIDA, MotivoSaida.RETIRADA,
                        produto.quantidade, agora, "Exclusão do produto"
                    )
                )
            }
            produtoDao.salvar(
                produto.copy(
                    quantidade = 0,
                    excluido = true,
                    updatedAt = agora,
                    sincronizado = false
                )
            )
        }
        scheduler.agendar()
    }

    // ---------- Auxiliares ----------

    // Nome de quem está fazendo a operação (vai para o log)
    private suspend fun nomeDe(uid: String): String =
        usuarioDao.buscarPorId(uid)?.nome ?: error("Usuário não encontrado")

    private fun novoMovimento(
        uid: String,
        usuarioNome: String,
        produtoId: String,
        tipo: TipoMovimento,
        motivo: MotivoSaida?,
        quantidade: Int,
        agora: Long,
        observacao: String?
    ) = MovimentoEntity(
        id = UUID.randomUUID().toString(),
        produtoId = produtoId,
        usuarioId = uid,
        usuarioNome = usuarioNome,
        tipo = tipo.name,
        motivo = motivo?.name,
        quantidade = quantidade,
        dataHora = agora,
        observacao = observacao
    )
}