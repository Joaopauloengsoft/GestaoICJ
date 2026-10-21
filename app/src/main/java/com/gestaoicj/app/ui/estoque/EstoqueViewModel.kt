package com.gestaoicj.app.ui.estoque

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestaoicj.app.data.local.dao.MovimentoComProduto
import com.gestaoicj.app.data.local.entity.ProdutoEntity
import com.gestaoicj.app.data.local.entity.TipoMovimento
import com.gestaoicj.app.data.repository.AuthRepository
import com.gestaoicj.app.data.repository.EstoqueRepository
import com.gestaoicj.app.ui.historico.FiltroMovimentacao
import com.gestaoicj.app.ui.historico.Movimentacao
import com.gestaoicj.app.ui.mapper.formatarDia
import com.gestaoicj.app.ui.mapper.formatarDiaCompleto
import com.gestaoicj.app.ui.mapper.paraReposicao
import com.gestaoicj.app.ui.mapper.paraTela
import com.gestaoicj.app.ui.reposicao.ProdutoParaRepor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EstoqueViewModel @Inject constructor(
    authRepo: AuthRepository,
    private val repo: EstoqueRepository,
) : ViewModel() {

    private companion object {
        const val DIAS_DO_PERIODO = 7
        const val UM_DIA_MS = 24L * 60 * 60 * 1000
    }

    private val uid: Flow<String?> = authRepo.uidFlow

    private val _busca = MutableStateFlow("")
    val busca: StateFlow<String> = _busca

    fun onBuscaChange(texto: String) {
        _busca.value = texto
    }

    val produtos: StateFlow<List<Produto>> =
        combine(uid, _busca) { u, b -> u to b }
            .flatMapLatest { (u, b) ->
                if (u == null) flowOf(emptyList<ProdutoEntity>()) else repo.observarEstoque(u, b)
            }
            .map { lista -> lista.map { it.paraTela() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalCadastrados: StateFlow<Int> =
        uid.flatMapLatest { u ->
            if (u == null) flowOf(0) else repo.observarEstoque(u).map { it.size }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val reposicao: StateFlow<List<ProdutoParaRepor>> =
        uid.flatMapLatest { u ->
            if (u == null) flowOf(emptyList<ProdutoEntity>()) else repo.observarReposicao(u)
        }
            .map { lista -> lista.map { it.paraReposicao() }.sortedByDescending { it.abaixoDoMinimo } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _filtro = MutableStateFlow(FiltroMovimentacao.Todas)
    val filtro: StateFlow<FiltroMovimentacao> = _filtro

    fun onFiltroChange(filtro: FiltroMovimentacao) {
        _filtro.value = filtro
    }

    val periodoRotulo: String = "$DIAS_DO_PERIODO dias"

    fun periodoTexto(): String {
        val agora = System.currentTimeMillis()
        return "${formatarDia(agora - DIAS_DO_PERIODO * UM_DIA_MS)} – ${formatarDiaCompleto(agora)}"
    }

    val movimentacoes: StateFlow<List<Movimentacao>> =
        combine(uid, _filtro) { u, f -> u to f }
            .flatMapLatest { (u, f) ->
                if (u == null) {
                    flowOf(emptyList<MovimentoComProduto>())
                } else {
                    val tipo = when (f) {
                        FiltroMovimentacao.Todas -> null
                        FiltroMovimentacao.Entradas -> TipoMovimento.ENTRADA
                        FiltroMovimentacao.Saidas -> TipoMovimento.SAIDA
                    }
                    val inicio = System.currentTimeMillis() - DIAS_DO_PERIODO * UM_DIA_MS
                    repo.observarHistorico(u, tipo = tipo, inicio = inicio)
                }
            }
            .map { lista -> lista.map { it.paraTela() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
