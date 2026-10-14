package com.gestaoicj.app.ui.historico // TROQUE pelo package do seu projeto

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CallMade
import androidx.compose.material.icons.automirrored.outlined.CallReceived
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gestaoicj.app.ui.components.SimboloIcj
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import com.gestaoicj.app.ui.theme.IcjColors
import com.gestaoicj.app.ui.theme.Inter

enum class TipoMovimentacao { Entrada, Saida }

enum class FiltroMovimentacao(val rotulo: String) {
    Todas("Todas"),
    Entradas("Entradas"),
    Saidas("Saídas"),
}

/**
 * @param quantidade sempre positiva; o sinal (+ / −) e a cor vêm do [tipo].
 * @param dataHora já formatada, por exemplo "13/10/2026 · 16:42".
 * @param numeroVenda só para saídas por venda; aparece como "#1048".
 */
data class Movimentacao(
    val id: String,
    val tipo: TipoMovimentacao,
    val produto: String,
    val dataHora: String,
    val quantidade: Int,
    val unidade: String,
    val numeroVenda: String? = null,
)

/**
 * @param movimentacoes lista já filtrada pelo [filtro] e pelo período (o filtro fica por sua conta).
 * @param periodoRotulo texto da 4ª aba, por exemplo "7 dias"; ao tocar, chama [onPeriodoClick].
 * @param periodoTexto texto acima da lista, por exemplo "12–13 out. 2026".
 */
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
            .background(IcjColors.Background)
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
            Cabecalho()

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

        Txt(
            text = "© 2026 Gestão ICJ. Todos os direitos reservados.",
            size = 11.sp,
            weight = FontWeight.Normal,
            color = IcjColors.TextMuted,
            modifier = Modifier.fillMaxWidth(),
            align = TextAlign.Center,
        )
    }
}

// ───────────────────────── Cabeçalho ─────────────────────────

@Composable
private fun Cabecalho() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SimboloIcj()
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Txt("Histórico do estoque", 24.sp, FontWeight.SemiBold, IcjColors.TextPrimary)
            Txt(
                text = "Acompanhe as entradas e as saídas por vendas.",
                size = 12.sp,
                weight = FontWeight.Normal,
                color = IcjColors.TextSecondary,
                lineHeight = 19.2.sp, // 1.6 × 12
            )
        }
    }
}

// ───────────────────────── Filtros ─────────────────────────

@Composable
private fun Filtros(
    selecionado: FiltroMovimentacao,
    onSelecionar: (FiltroMovimentacao) -> Unit,
    periodoRotulo: String,
    onPeriodoClick: () -> Unit,
) {
    val forma = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(forma)
            .background(IcjColors.Field)
            .border(1.dp, IcjColors.Border, forma)
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
        // A 4ª aba é o seletor de período: não fica "marcada", só abre a escolha.
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
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(6.dp))
            .background(if (ativo) IcjColors.Yellow else IcjColors.Field),
        contentAlignment = Alignment.Center,
    ) {
        Txt(
            text = texto,
            size = 13.sp,
            weight = FontWeight.Medium,
            color = if (ativo) IcjColors.OnYellow else IcjColors.TextSecondary,
            maxLines = 1,
        )
    }
}

// ───────────────────────── Lista ─────────────────────────

@Composable
private fun ListaDeMovimentacoes(
    movimentacoes: List<Movimentacao>,
    periodoTexto: String,
    modifier: Modifier = Modifier,
) {
    val forma = RoundedCornerShape(8.dp)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Txt(periodoTexto, 13.sp, FontWeight.Medium, IcjColors.TextPrimary, maxLines = 1)
            Txt(
                text = if (movimentacoes.size == 1) "1 movimentação" else "${movimentacoes.size} movimentações",
                size = 13.sp,
                weight = FontWeight.Normal,
                color = IcjColors.TextSecondary,
                maxLines = 1,
            )
        }

        // A lista rola sozinha quando há mais movimentações do que cabe na tela.
        LazyColumn(
            modifier = Modifier
                .weight(1f, fill = false)
                .clip(forma)
                .background(IcjColors.Field)
                .border(1.dp, IcjColors.Border, forma),
        ) {
            itemsIndexed(movimentacoes, key = { _, item -> item.id }) { indice, item ->
                LinhaDeMovimentacao(item, ultimo = indice == movimentacoes.lastIndex)
            }
        }

        Txt(
            text = "Da movimentação mais recente para a mais antiga.",
            size = 11.sp,
            weight = FontWeight.Normal,
            color = IcjColors.TextSecondary,
        )
    }
}

@Composable
private fun LinhaDeMovimentacao(item: Movimentacao, ultimo: Boolean) {
    val entrada = item.tipo == TipoMovimentacao.Entrada
    val corDestaque = if (entrada) IcjColors.Yellow else IcjColors.TextPrimary

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
                    Txt(
                        text = if (entrada) "Entrada" else "Saída · Venda",
                        size = 11.sp,
                        weight = FontWeight.Medium,
                        color = corDestaque,
                        maxLines = 1,
                    )
                    if (!entrada && item.numeroVenda != null) {
                        Txt("#${item.numeroVenda}", 11.sp, FontWeight.Normal, IcjColors.TextSecondary, maxLines = 1)
                    }
                }
                Txt(item.produto, 14.sp, FontWeight.Medium, IcjColors.TextPrimary, maxLines = 2)
                Txt(item.dataHora, 11.sp, FontWeight.Normal, IcjColors.TextSecondary, maxLines = 1)
            }

            // 56dp no design; widthIn deixa crescer se a quantidade tiver mais dígitos.
            Column(
                modifier = Modifier.widthIn(min = 56.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Txt(
                    text = (if (entrada) "+" else "−") + item.quantidade, // "−" é o sinal de menos (U+2212)
                    size = 18.sp,
                    weight = FontWeight.SemiBold,
                    color = corDestaque,
                    maxLines = 1,
                )
                Txt(item.unidade, 11.sp, FontWeight.Normal, IcjColors.TextSecondary, maxLines = 1)
            }
        }

        if (!ultimo) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(IcjColors.Border),
            )
        }
    }
}

// ───────────────────────── Utilitários ─────────────────────────

@Composable
private fun Txt(
    text: String,
    size: TextUnit,
    weight: FontWeight,
    color: Color,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    lineHeight: TextUnit = TextUnit.Unspecified,
    align: TextAlign = TextAlign.Unspecified,
) {
    Text(
        text = text,
        color = color,
        fontFamily = Inter,
        fontWeight = weight,
        fontSize = size,
        lineHeight = lineHeight,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textAlign = align,
        modifier = modifier,
    )
}

@Preview(widthDp = 390, heightDp = 844, showBackground = true, backgroundColor = 0xFF101112)
@Composable
private fun HistoricoEstoqueScreenPreview() {
    GestaoIcjTheme {
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
