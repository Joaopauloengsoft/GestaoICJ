package com.gestaoicj.app.ui.reposicao // TROQUE pelo package do seu projeto

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gestaoicj.app.ui.components.SimboloIcj
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import com.gestaoicj.app.ui.theme.IcjColors
import com.gestaoicj.app.ui.theme.Inter

/**
 * Produto que precisa de reposição: estoque atual menor ou igual ao mínimo cadastrado.
 * O status ("Abaixo do mínimo" / "No mínimo") é calculado a partir dos dois números.
 */
data class ProdutoParaRepor(
    val id: String,
    val nome: String,
    val codigo: String,
    val estoqueAtual: Int,
    val estoqueMinimo: Int,
    val unidade: String,
) {
    val abaixoDoMinimo: Boolean get() = estoqueAtual < estoqueMinimo
}

/**
 * @param produtos já filtrados (atual ≤ mínimo) e na ordem em que devem aparecer;
 * no design, os "abaixo do mínimo" vêm antes dos "no mínimo".
 */
@Composable
fun ReposicaoScreen(
    produtos: List<ProdutoParaRepor>,
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
            ListaDeReposicao(
                produtos = produtos,
                modifier = Modifier.weight(1f, fill = false),
            )
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
            Txt("Reposição", 24.sp, FontWeight.SemiBold, IcjColors.TextPrimary)
            Txt(
                text = "Lista automática: produtos com estoque atual menor ou igual ao mínimo definido no cadastro.",
                size = 12.sp,
                weight = FontWeight.Normal,
                color = IcjColors.TextSecondary,
                lineHeight = 19.2.sp, // 1.6 × 12
            )
        }
    }
}

// ───────────────────────── Lista ─────────────────────────

@Composable
private fun ListaDeReposicao(
    produtos: List<ProdutoParaRepor>,
    modifier: Modifier = Modifier,
) {
    val forma = RoundedCornerShape(8.dp)
    val abaixo = produtos.count { it.abaixoDoMinimo }
    val noMinimo = produtos.size - abaixo

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Txt("Produtos para repor", 14.sp, FontWeight.Medium, IcjColors.TextPrimary, maxLines = 1)
                Txt(
                    text = if (produtos.size == 1) "1 produto" else "${produtos.size} produtos",
                    size = 13.sp,
                    weight = FontWeight.Normal,
                    color = IcjColors.TextSecondary,
                    maxLines = 1,
                )
            }

            // A lista rola sozinha quando há mais produtos do que cabe na tela.
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .clip(forma)
                    .background(IcjColors.Field)
                    .border(1.dp, IcjColors.Border, forma),
            ) {
                itemsIndexed(produtos, key = { _, produto -> produto.id }) { indice, produto ->
                    LinhaDeReposicao(produto, ultimo = indice == produtos.lastIndex)
                }
            }
        }

        Txt(
            text = "$abaixo abaixo do mínimo · $noMinimo no mínimo",
            size = 13.sp,
            weight = FontWeight.Normal,
            color = IcjColors.TextSecondary,
            lineHeight = 19.5.sp, // 1.5 × 13
        )
    }
}

@Composable
private fun LinhaDeReposicao(produto: ProdutoParaRepor, ultimo: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 124.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Txt(produto.nome, 14.sp, FontWeight.Medium, IcjColors.TextPrimary, maxLines = 2)
                Txt(
                    text = "${produto.codigo} · ${if (produto.abaixoDoMinimo) "Abaixo do mínimo" else "No mínimo"}",
                    size = 11.sp,
                    weight = FontWeight.Normal,
                    color = IcjColors.Yellow,
                    maxLines = 1,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Comparacao(
                    rotulo = "Estoque atual",
                    numero = produto.estoqueAtual,
                    unidade = produto.unidade,
                    corNumero = IcjColors.Yellow,
                    modifier = Modifier.weight(1f),
                )
                Comparacao(
                    rotulo = "Mínimo cadastrado",
                    numero = produto.estoqueMinimo,
                    unidade = produto.unidade,
                    corNumero = IcjColors.TextPrimary,
                    modifier = Modifier.weight(1f),
                )
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

@Composable
private fun Comparacao(
    rotulo: String,
    numero: Int,
    unidade: String,
    corNumero: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Txt(rotulo, 11.sp, FontWeight.Normal, IcjColors.TextSecondary, maxLines = 1)
        // Número grande e unidade pequena na mesma linha de base, como no design.
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = corNumero, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)) {
                    append(numero.toString())
                }
                withStyle(SpanStyle(color = IcjColors.TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Normal)) {
                    append(" $unidade")
                }
            },
            fontFamily = Inter,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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
private fun ReposicaoScreenPreview() {
    GestaoIcjTheme {
        ReposicaoScreen(
            produtos = listOf(
                ProdutoParaRepor("3", "Álcool em gel 500 ml", "ICJ-003", 4, 10, "unidades"),
                ProdutoParaRepor("4", "Café em pó 500 g", "ICJ-004", 18, 20, "pacotes"),
                ProdutoParaRepor("1", "Papel A4", "ICJ-001", 24, 24, "resmas"),
            ),
        )
    }
}
