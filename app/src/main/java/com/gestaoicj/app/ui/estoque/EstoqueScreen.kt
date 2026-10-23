package com.gestaoicj.app.ui.estoque

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.gestaoicj.app.ui.components.SimboloIcj
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import com.gestaoicj.app.ui.theme.extraColors

data class Produto(
    val id: String,
    val nome: String,
    val codigo: String,
    val categoria: String,
    val saldo: Int,
    val unidade: String,
    val estoqueMinimo: Int = 0,
    val estoqueBaixo: Boolean = false,
)

@Composable
fun EstoqueScreen(
    produtos: List<Produto>,
    busca: String,
    onBuscaChange: (String) -> Unit,
    onEscanear: () -> Unit,
    modifier: Modifier = Modifier,
    totalCadastrados: Int = produtos.size,
    onProdutoClick: (Produto) -> Unit = {},
    onEditarProduto: (Produto) -> Unit = {},
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
            Cabecalho()

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                CampoBusca(valor = busca, onValorChange = onBuscaChange)
                ListaDeProdutos(
                    produtos = produtos,
                    totalCadastrados = totalCadastrados,
                    onProdutoClick = onProdutoClick,
                    onEditarProduto = onEditarProduto,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
        }

        Rodape(onEscanear)
    }
}

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
            Text(
                text = "Estoque",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Consulte e adicione produtos ao estoque.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CampoBusca(valor: String, onValorChange: (String) -> Unit) {
    val forma = MaterialTheme.shapes.medium
    val cores = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(forma)
            .background(cores.surfaceVariant)
            .border(1.dp, cores.outline, forma)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = cores.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        BasicTextField(
            value = valor,
            onValueChange = onValorChange,
            singleLine = true,
            // O texto digitado nunca teve altura de linha/espaçamento do bodyLarge;
            // zerar aqui mantém o campo idêntico ao original.
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = cores.onBackground,
                lineHeight = TextUnit.Unspecified,
                letterSpacing = TextUnit.Unspecified,
            ),
            cursorBrush = SolidColor(MaterialTheme.extraColors.accent),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier.weight(1f),
            decorationBox = { campo ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (valor.isEmpty()) {
                        Text(
                            text = "Buscar produto ou código",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.extraColors.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    campo()
                }
            },
        )
    }
}

@Composable
private fun ListaDeProdutos(
    produtos: List<Produto>,
    totalCadastrados: Int,
    onProdutoClick: (Produto) -> Unit,
    onEditarProduto: (Produto) -> Unit,
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
                text = "Produtos",
                style = MaterialTheme.typography.titleSmall,
                color = cores.onBackground,
            )
            Text(
                text = if (totalCadastrados == 1) "1 cadastrado" else "$totalCadastrados cadastrados",
                style = MaterialTheme.typography.bodySmall,
                color = cores.onSurfaceVariant,
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
                LinhaDeProduto(
                    produto = produto,
                    ultimo = indice == produtos.lastIndex,
                    onClick = { onProdutoClick(produto) },
                    onEditarClick = { onEditarProduto(produto) },
                )
            }
        }
    }
}

@Composable
private fun LinhaDeProduto(produto: Produto, ultimo: Boolean, onClick: () -> Unit, onEditarClick: () -> Unit) {
    val cores = MaterialTheme.colorScheme
    val destaque = if (produto.estoqueBaixo) MaterialTheme.extraColors.accent else null

    Box(modifier = Modifier.fillMaxWidth().height(72.dp)) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(role = Role.Button, onClick = onClick)
                    .padding(start = 16.dp, end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = produto.nome,
                        style = MaterialTheme.typography.titleSmall,
                        color = cores.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${produto.codigo} · ${if (produto.estoqueBaixo) "Estoque baixo" else produto.categoria}",
                        style = MaterialTheme.typography.labelSmall,
                        color = destaque ?: cores.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Column(
                    modifier = Modifier.widthIn(min = 56.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = produto.saldo.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = destaque ?: cores.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = produto.unidade,
                        style = MaterialTheme.typography.labelSmall,
                        color = cores.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(role = Role.Button, onClick = onEditarClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Editar produto",
                    tint = cores.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
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
private fun Rodape(onEscanear: () -> Unit) {
    val cores = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Escanear produto",
                    style = MaterialTheme.typography.titleSmall,
                    color = cores.onBackground,
                )
                Text(
                    text = "Entrada, saída ou novo cadastro",
                    style = MaterialTheme.typography.bodySmall,
                    color = cores.onSurfaceVariant,
                )
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(cores.primary)
                    .clickable(role = Role.Button, onClick = onEscanear),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Escanear código de barras",
                    tint = cores.onPrimary,
                    modifier = Modifier.size(26.dp),
                )
            }
        }

        Text(
            text = "© 2026 Gestão ICJ. Todos os direitos reservados.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.extraColors.textMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private val produtosDeExemplo = listOf(
    Produto("1", "Papel A4", "ICJ-001", "Papelaria", 24, "resmas"),
    Produto("2", "Caneta esferográfica", "ICJ-002", "Papelaria", 120, "unidades"),
    Produto("3", "Álcool em gel 500 ml", "ICJ-003", "Papelaria", 4, "unidades", estoqueBaixo = true),
    Produto("4", "Café em pó 500 g", "ICJ-004", "Copa", 18, "pacotes"),
    Produto("5", "Copo descartável 200 ml", "ICJ-005", "Copa", 32, "pacotes"),
)

@Composable
private fun EstoqueScreenPreview(darkTheme: Boolean) {
    GestaoIcjTheme(darkTheme = darkTheme) {
        EstoqueScreen(
            produtos = produtosDeExemplo,
            busca = "",
            onBuscaChange = {},
            onEscanear = {},
        )
    }
}

@Preview(name = "Escuro", widthDp = 390, heightDp = 844, showBackground = true)
@Composable
private fun EstoqueScreenEscuroPreview() = EstoqueScreenPreview(darkTheme = true)

@Preview(name = "Claro", widthDp = 390, heightDp = 844, showBackground = true)
@Composable
private fun EstoqueScreenClaroPreview() = EstoqueScreenPreview(darkTheme = false)
