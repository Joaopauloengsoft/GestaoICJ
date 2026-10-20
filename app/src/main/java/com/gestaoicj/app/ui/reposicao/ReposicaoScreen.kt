package com.gestaoicj.app.ui.reposicao

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gestaoicj.app.ui.components.CabecalhoTela
import com.gestaoicj.app.ui.components.RodapeCopyright
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import com.gestaoicj.app.ui.theme.extraColors

/** Produto com estoque atual menor ou igual ao mínimo cadastrado. */
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

@Composable
fun ReposicaoScreen(
    produtos: List<ProdutoParaRepor>,
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
                titulo = "Reposição",
                subtitulo = "Lista automática: produtos com estoque atual menor ou igual ao mínimo definido no cadastro.",
            )
            ListaDeReposicao(
                produtos = produtos,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
        RodapeCopyright()
    }
}

@Composable
private fun ListaDeReposicao(
    produtos: List<ProdutoParaRepor>,
    modifier: Modifier = Modifier,
) {
    val forma = MaterialTheme.shapes.medium
    val cores = MaterialTheme.colorScheme
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
                Text(
                    text = "Produtos para repor",
                    style = MaterialTheme.typography.titleSmall,
                    color = cores.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (produtos.size == 1) "1 produto" else "${produtos.size} produtos",
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
                itemsIndexed(produtos, key = { _, produto -> produto.id }) { indice, produto ->
                    LinhaDeReposicao(produto, ultimo = indice == produtos.lastIndex)
                }
            }
        }

        Text(
            text = "$abaixo abaixo do mínimo · $noMinimo no mínimo",
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.5.sp),
            color = cores.onSurfaceVariant,
        )
    }
}

@Composable
private fun LinhaDeReposicao(produto: ProdutoParaRepor, ultimo: Boolean) {
    val cores = MaterialTheme.colorScheme
    val destaque = MaterialTheme.extraColors.accent

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
                Text(
                    text = produto.nome,
                    style = MaterialTheme.typography.titleSmall,
                    color = cores.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${produto.codigo} · ${if (produto.abaixoDoMinimo) "Abaixo do mínimo" else "No mínimo"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = destaque,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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
                    corNumero = destaque,
                    modifier = Modifier.weight(1f),
                )
                Comparacao(
                    rotulo = "Mínimo cadastrado",
                    numero = produto.estoqueMinimo,
                    unidade = produto.unidade,
                    corNumero = cores.onBackground,
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
                    .background(cores.outline),
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
    val tipografia = MaterialTheme.typography
    val estiloNumero = tipografia.titleLarge.toSpanStyle().copy(color = corNumero)
    val estiloUnidade = tipografia.labelSmall.toSpanStyle()
        .copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = rotulo,
            style = tipografia.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        // Número grande e unidade pequena na mesma linha de base.
        Text(
            text = buildAnnotatedString {
                withStyle(estiloNumero) { append(numero.toString()) }
                withStyle(estiloUnidade) { append(" $unidade") }
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ReposicaoPreview(darkTheme: Boolean) {
    GestaoIcjTheme(darkTheme = darkTheme) {
        ReposicaoScreen(
            produtos = listOf(
                ProdutoParaRepor("3", "Álcool em gel 500 ml", "ICJ-003", 4, 10, "unidades"),
                ProdutoParaRepor("4", "Café em pó 500 g", "ICJ-004", 18, 20, "pacotes"),
                ProdutoParaRepor("1", "Papel A4", "ICJ-001", 24, 24, "resmas"),
            ),
        )
    }
}

@Preview(name = "Escuro", widthDp = 390, heightDp = 844)
@Composable
private fun ReposicaoEscuroPreview() = ReposicaoPreview(darkTheme = true)

@Preview(name = "Claro", widthDp = 390, heightDp = 844)
@Composable
private fun ReposicaoClaroPreview() = ReposicaoPreview(darkTheme = false)