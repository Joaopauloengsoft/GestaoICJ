package com.gestaoicj.app.ui.historico

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CallMade
import androidx.compose.material.icons.automirrored.outlined.CallReceived
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gestaoicj.app.ui.components.CabecalhoTela
import com.gestaoicj.app.ui.components.RodapeCopyright
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import com.gestaoicj.app.ui.theme.extraColors

enum class TipoMovimentacao { Entrada, Saida }

enum class FiltroMovimentacao(val rotulo: String) {
    Todas("Todas"),
    Entradas("Entradas"),
    Saidas("Saídas"),
}

data class Movimentacao(
    val id: String,
    val tipo: TipoMovimentacao,
    val produto: String,
    val dataHora: String,
    val quantidade: Int,
    val unidade: String,
    val numeroVenda: String? = null,
)

@Composable
fun HistoricoEstoqueScreen(
    movimentacoes: List<Movimentacao>,
    filtro: FiltroMovimentacao,
    onFiltroChange: (FiltroMovimentacao) -> Unit,
    periodoRotulo: String,
    onPeriodoClick: () -> Unit,
    periodoTexto: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(36.dp),
        ) {
            CabecalhoTela(
                titulo = "Histórico do estoque",
                subtitulo = "Acompanhe as entradas e as saídas por vendas.",
            )

            Column(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Filtros(
                    selecionado = filtro,
                    onSelecionar = onFiltroChange,
                    periodoRotulo = periodoRotulo,
                    onPeriodoClick = onPeriodoClick,
                )
                ListaDeMovimentacoes(
                    movimentacoes = movimentacoes,
                    periodoTexto = periodoTexto,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
        }

        RodapeCopyright()
    }
}

@Composable
private fun Filtros(
    selecionado: FiltroMovimentacao,
    onSelecionar: (FiltroMovimentacao) -> Unit,
    periodoRotulo: String,
    onPeriodoClick: () -> Unit,
) {
    val forma = MaterialTheme.shapes.medium
    val cores = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(forma)
            .background(cores.surfaceVariant)
            .border(1.dp, cores.outline, forma)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FiltroMovimentacao.entries.forEach { filtro ->
            Segmento(
                texto = filtro.rotulo,
                ativo = filtro == selecionado,
                modifier = Modifier
                    .weight(1f)
                    .selectable(
                        selected = filtro == selecionado,
                        role = Role.Tab,
                        onClick = { onSelecionar(filtro) },
                    ),
            )
        }
        // A 4ª aba é o seletor de período: nunca fica marcada, só abre a escolha.
        Segmento(
            texto = periodoRotulo,
            ativo = false,
            modifier = Modifier
                .weight(1f)
                .clickable(role = Role.Button, onClick = onPeriodoClick),
        )
    }
}

@Composable
private fun Segmento(texto: String, ativo: Boolean, modifier: Modifier = Modifier) {
    val cores = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.small)
            .background(if (ativo) cores.primary else cores.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = if (ativo) cores.onPrimary else cores.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ListaDeMovimentacoes(
    movimentacoes: List<Movimentacao>,
    periodoTexto: String,
    modifier: Modifier = Modifier,
) {
    val forma = MaterialTheme.shapes.medium
    val cores = MaterialTheme.colorScheme

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = periodoTexto,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = cores.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (movimentacoes.size == 1) "1 movimentação" else "${movimentacoes.size} movimentações",
                style = MaterialTheme.typography.bodySmall,
                color = cores.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f, fill = false)
                .clip(forma)
                .background(cores.surfaceVariant)
                .border(1.dp, cores.outline, forma),
        ) {
            itemsIndexed(movimentacoes, key = { _, item -> item.id }) { indice, item ->
                LinhaDeMovimentacao(item, ultimo = indice == movimentacoes.lastIndex)
            }
        }

        Text(
            text = "Da movimentação mais recente para a mais antiga.",
            style = MaterialTheme.typography.labelSmall,
            color = cores.onSurfaceVariant,
        )
    }
}

@Composable
private fun LinhaDeMovimentacao(item: Movimentacao, ultimo: Boolean) {
    val cores = MaterialTheme.colorScheme
    val tipografia = MaterialTheme.typography
    val entrada = item.tipo == TipoMovimentacao.Entrada
    val corDestaque = if (entrada) MaterialTheme.extraColors.accent else cores.onBackground

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (entrada) Icons.AutoMirrored.Outlined.CallReceived
                        else Icons.AutoMirrored.Outlined.CallMade,
                        contentDescription = null,
                        tint = corDestaque,
                        modifier = Modifier.size(12.dp),
                    )
                    Text(
                        text = if (entrada) "Entrada" else "Saída · Venda",
                        style = tipografia.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = corDestaque,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!entrada && item.numeroVenda != null) {
                        Text(
                            text = "#${item.numeroVenda}",
                            style = tipografia.labelSmall,
                            color = cores.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Text(
                    text = item.produto,
                    style = tipografia.titleSmall,
                    color = cores.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.dataHora,
                    style = tipografia.labelSmall,
                    color = cores.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // 56dp no design; widthIn deixa crescer se a quantidade tiver mais dígitos.
            Column(
                modifier = Modifier.widthIn(min = 56.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = (if (entrada) "+" else "−") + item.quantidade, // "−" é o sinal de menos (U+2212)
                    style = tipografia.titleLarge,
                    color = corDestaque,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.unidade,
                    style = tipografia.labelSmall,
                    color = cores.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        if (!ultimo) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(cores.outline),
            )
        }
    }
}

@Composable
private fun HistoricoPreview(darkTheme: Boolean) {
    GestaoIcjTheme(darkTheme = darkTheme) {
        HistoricoEstoqueScreen(
            movimentacoes = listOf(
                Movimentacao("1", TipoMovimentacao.Saida, "Papel A4", "13/10/2026 · 16:42", 6, "resmas", "1048"),
                Movimentacao("2", TipoMovimentacao.Entrada, "Caneta esferográfica", "13/10/2026 · 14:10", 40, "unidades"),
                Movimentacao("3", TipoMovimentacao.Saida, "Álcool em gel 500 ml", "13/10/2026 · 11:25", 2, "unidades", "1047"),
                Movimentacao("4", TipoMovimentacao.Saida, "Café em pó 500 g", "12/10/2026 · 15:30", 4, "pacotes", "1046"),
                Movimentacao("5", TipoMovimentacao.Entrada, "Copo descartável 200 ml", "12/10/2026 · 09:15", 12, "pacotes"),
            ),
            filtro = FiltroMovimentacao.Todas,
            onFiltroChange = {},
            periodoRotulo = "7 dias",
            onPeriodoClick = {},
            periodoTexto = "12–13 out. 2026",
        )
    }
}

@Preview(name = "Escuro", widthDp = 390, heightDp = 844)
@Composable
private fun HistoricoEscuroPreview() = HistoricoPreview(darkTheme = true)

@Preview(name = "Claro", widthDp = 390, heightDp = 844)
@Composable
private fun HistoricoClaroPreview() = HistoricoPreview(darkTheme = false)