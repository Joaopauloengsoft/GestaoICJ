package com.gestaoicj.app.ui.estoque

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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

/**
 * Modelo de dados que representa um produto do estoque.
 *
 * @property id Identificador único do produto.
 * @property nome Nome descritivo do produto.
 * @property codigo Código ou SKU para identificação no estoque (ex: "ICJ-001").
 * @property categoria Categoria do produto (ex: "Papelaria", "Copa").
 * @property saldo Quantidade disponível em estoque.
 * @property unidade Unidade de medida (ex: "unidades", "resmas", "pacotes").
 * @property estoqueBaixo Indica se o saldo do produto está abaixo do limite mínimo recomendado.
 */
data class Produto(
    val id: String,
    val nome: String,
    val codigo: String,
    val categoria: String,
    val saldo: Int,
    val unidade: String,
    val estoqueBaixo: Boolean = false,
)

/**
 * Tela principal de Gestão de Estoque do aplicativo ICJ.
 *
 * Exibe o cabeçalho com a marca, campo de busca de produtos, listagem dinâmica dos itens cadastrados
 * com status de saldo e botão para adição/escaneamento de novos produtos no rodapé.
 *
 * @param produtos Lista de produtos a serem exibidos (geralmente filtrada pela busca no ViewModel).
 * @param busca Texto atual do campo de pesquisa de produtos.
 * @param onBuscaChange Callback acionado quando o texto da busca é alterado pelo usuário.
 * @param onAdicionarProduto Callback acionado ao clicar no botão de adicionar produto.
 * @param modifier Modificador de layout aplicado ao contêiner principal da tela.
 * @param totalCadastrados Quantidade total de produtos cadastrados para exibição no contador.
 */
@Composable
fun EstoqueScreen(
    produtos: List<Produto>,
    busca: String,
    onBuscaChange: (String) -> Unit,
    onAdicionarProduto: () -> Unit,
    modifier: Modifier = Modifier,
    totalCadastrados: Int = produtos.size,
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
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                CampoBusca(valor = busca, onValorChange = onBuscaChange)
                ListaDeProdutos(
                    produtos = produtos,
                    totalCadastrados = totalCadastrados,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
        }

        Rodape(onAdicionarProduto)
    }
}

// ───────────────────────── Cabeçalho ─────────────────────────

/**
 * Cabeçalho da tela de estoque contendo o símbolo da ICJ, o título da seção e uma breve instrução.
 */
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
            Txt("Estoque", 24.sp, FontWeight.SemiBold, IcjColors.TextPrimary)
            Txt("Consulte e adicione produtos ao estoque.", 12.sp, FontWeight.Medium, IcjColors.TextSecondary)
        }
    }
}

// ───────────────────────── Busca ─────────────────────────

/**
 * Campo de entrada de texto estilizado para filtragem de produtos por nome ou código.
 *
 * @param valor Texto atualmente digitado no campo de busca.
 * @param onValorChange Callback acionado sempre que o valor do campo for modificado.
 */
@Composable
private fun CampoBusca(valor: String, onValorChange: (String) -> Unit) {
    val forma = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(forma)
            .background(IcjColors.Field)
            .border(1.dp, IcjColors.Border, forma)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = IcjColors.TextSecondary,
            modifier = Modifier.size(20.dp),
        )
        BasicTextField(
            value = valor,
            onValueChange = onValorChange,
            singleLine = true,
            textStyle = TextStyle(
                color = IcjColors.TextPrimary,
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            ),
            cursorBrush = SolidColor(IcjColors.Yellow),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier.weight(1f),
            decorationBox = { campo ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (valor.isEmpty()) {
                        Txt("Buscar produto ou código", 14.sp, FontWeight.Normal, IcjColors.TextMuted, maxLines = 1)
                    }
                    campo()
                }
            },
        )
    }
}

// ───────────────────────── Lista ─────────────────────────

/**
 * Seção de listagem dos produtos cadastrados no estoque. Exibe o contador total e a lista rolável.
 *
 * @param produtos Lista dos produtos a serem renderizados.
 * @param totalCadastrados Número total de produtos cadastrados no sistema.
 * @param modifier Modificador opcional para customização de layout.
 */
@Composable
private fun ListaDeProdutos(
    produtos: List<Produto>,
    totalCadastrados: Int,
    modifier: Modifier = Modifier,
) {
    val forma = RoundedCornerShape(8.dp)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Txt("Produtos", 14.sp, FontWeight.Medium, IcjColors.TextPrimary)
            Txt(
                text = if (totalCadastrados == 1) "1 cadastrado" else "$totalCadastrados cadastrados",
                size = 13.sp,
                weight = FontWeight.Normal,
                color = IcjColors.TextSecondary,
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
                LinhaDeProduto(produto, ultimo = indice == produtos.lastIndex)
            }
        }
    }
}

/**
 * Item individual da lista que apresenta as informações detalhadas de um [produto].
 *
 * @param produto Dados do produto a ser exibido nesta linha.
 * @param ultimo Indica se este item é o último da lista para controlar a exibição da linha divisória.
 */
@Composable
private fun LinhaDeProduto(produto: Produto, ultimo: Boolean) {
    val destaque = if (produto.estoqueBaixo) IcjColors.Yellow else null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Txt(produto.nome, 14.sp, FontWeight.Medium, IcjColors.TextPrimary, maxLines = 1)
                Txt(
                    text = "${produto.codigo} · ${if (produto.estoqueBaixo) "Estoque baixo" else produto.categoria}",
                    size = 11.sp,
                    weight = FontWeight.Normal,
                    color = destaque ?: IcjColors.TextSecondary,
                    maxLines = 1,
                )
            }
            // 62dp no design; widthIn deixa crescer se o saldo tiver mais dígitos.
            Column(
                modifier = Modifier.widthIn(min = 62.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Txt(
                    text = produto.saldo.toString(),
                    size = 18.sp,
                    weight = FontWeight.SemiBold,
                    color = destaque ?: IcjColors.TextPrimary,
                    maxLines = 1,
                )
                Txt(produto.unidade, 11.sp, FontWeight.Normal, IcjColors.TextSecondary, maxLines = 1)
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

// ───────────────────────── Rodapé ─────────────────────────

/**
 * Rodapé da tela contendo a ação de adicionar produto via scanner e informações de copyright.
 *
 * @param onAdicionarProduto Action executada ao clicar no botão de adicionar/escanear produto.
 */
@Composable
private fun Rodape(onAdicionarProduto: () -> Unit) {
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
                Txt("Adicionar produto", 14.sp, FontWeight.Medium, IcjColors.TextPrimary)
                Txt("Escaneie o código de barras", 13.sp, FontWeight.Normal, IcjColors.TextSecondary)
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(IcjColors.Yellow)
                    .clickable(role = Role.Button, onClick = onAdicionarProduto),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Adicionar produto escaneando o código de barras",
                    tint = IcjColors.OnYellow,
                    modifier = Modifier.size(26.dp),
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

// ───────────────────────── Utilitários ─────────────────────────

/**
 * Componente utilitário interno para renderização de textos estilizados com a fonte Inter.
 *
 * @param text Texto a ser exibido.
 * @param size Tamanho da fonte ([TextUnit]).
 * @param weight Peso da fonte ([FontWeight]).
 * @param color Cor do texto ([Color]).
 * @param modifier Modificador de layout.
 * @param maxLines Número máximo de linhas antes de cortar com reticências.
 * @param align Alinhamento do texto ([TextAlign]).
 */
@Composable
private fun Txt(
    text: String,
    size: TextUnit,
    weight: FontWeight,
    color: Color,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    align: TextAlign = TextAlign.Unspecified,
) {
    Text(
        text = text,
        color = color,
        fontFamily = Inter,
        fontWeight = weight,
        fontSize = size,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textAlign = align,
        modifier = modifier,
    )
}

/**
 * Preview da tela [EstoqueScreen] para visualização no Android Studio.
 */
@Preview(widthDp = 390, heightDp = 844, showBackground = true, backgroundColor = 0xFF101112)
@Composable
private fun EstoqueScreenPreview() {
    GestaoIcjTheme {
        EstoqueScreen(
            produtos = listOf(
                Produto("1", "Papel A4", "ICJ-001", "Papelaria", 24, "resmas"),
                Produto("2", "Caneta esferográfica", "ICJ-002", "Papelaria", 120, "unidades"),
                Produto("3", "Álcool em gel 500 ml", "ICJ-003", "Papelaria", 4, "unidades", estoqueBaixo = true),
                Produto("4", "Café em pó 500 g", "ICJ-004", "Copa", 18, "pacotes"),
                Produto("5", "Copo descartável 200 ml", "ICJ-005", "Copa", 32, "pacotes"),
            ),
            busca = "",
            onBuscaChange = {},
            onAdicionarProduto = {},
        )
    }
}
