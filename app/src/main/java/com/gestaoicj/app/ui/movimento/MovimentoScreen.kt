package com.gestaoicj.app.ui.movimento

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gestaoicj.app.data.local.entity.MotivoSaida
import com.gestaoicj.app.ui.components.CampoFormulario
import com.gestaoicj.app.ui.components.RodapeCopyright
import com.gestaoicj.app.ui.components.SimboloIcj
import com.gestaoicj.app.ui.estoque.Produto
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import com.gestaoicj.app.ui.theme.extraColors

private enum class TipoOperacao { ENTRADA, SAIDA }

@Composable
fun MovimentoScreen(
    produto: Produto?,
    carregando: Boolean,
    erro: String?,
    onEntrada: (quantidade: Int) -> Unit,
    onSaida: (quantidade: Int, motivo: MotivoSaida) -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var tipo by rememberSaveable { mutableStateOf(TipoOperacao.ENTRADA) }
    var quantidade by rememberSaveable { mutableStateOf("") }
    var motivo by rememberSaveable { mutableStateOf(MotivoSaida.VENDA) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
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
            Cabecalho(produto)

            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                ToggleTipo(selecionado = tipo, onSelecionar = { tipo = it })

                CampoFormulario(
                    rotulo = "Quantidade",
                    valor = quantidade,
                    onValorChange = { quantidade = it.filter { c -> c.isDigit() } },
                    placeholder = "0",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() },
                    ),
                )

                if (tipo == TipoOperacao.SAIDA) {
                    ToggleMotivo(selecionado = motivo, onSelecionar = { motivo = it })
                }

                if (erro != null) {
                    Text(
                        text = erro,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        Rodape(
            carregando = carregando,
            onConfirmar = {
                focusManager.clearFocus()
                val qtd = quantidade.toIntOrNull() ?: 0
                if (tipo == TipoOperacao.ENTRADA) onEntrada(qtd) else onSaida(qtd, motivo)
            },
            onVoltar = onVoltar,
        )
    }
}

@Composable
private fun Cabecalho(produto: Produto?) {
    val cores = MaterialTheme.colorScheme

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
                text = produto?.nome ?: "Produto",
                style = MaterialTheme.typography.headlineSmall,
                color = cores.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = produto?.codigo ?: "",
                    style = MaterialTheme.typography.labelLarge,
                    color = cores.onSurfaceVariant,
                )
                if (produto != null) {
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.labelLarge,
                        color = cores.onSurfaceVariant,
                    )
                    Text(
                        text = "Saldo: ${produto.saldo} ${produto.unidade}",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (produto.estoqueBaixo) MaterialTheme.extraColors.accent else cores.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ToggleTipo(selecionado: TipoOperacao, onSelecionar: (TipoOperacao) -> Unit) {
    val forma = MaterialTheme.shapes.medium
    val cores = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(forma)
            .background(cores.surfaceVariant)
            .border(1.dp, cores.outline, forma),
    ) {
        TipoOperacao.entries.forEach { opcao ->
            val ativo = opcao == selecionado
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(forma)
                    .background(if (ativo) cores.primary else cores.surfaceVariant)
                    .clickable(role = Role.Tab, onClick = { onSelecionar(opcao) }),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (opcao == TipoOperacao.ENTRADA) "Entrada" else "Saída",
                    style = MaterialTheme.typography.titleSmall,
                    color = if (ativo) cores.onPrimary else cores.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ToggleMotivo(selecionado: MotivoSaida, onSelecionar: (MotivoSaida) -> Unit) {
    val cores = MaterialTheme.colorScheme

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Motivo da saída",
            style = MaterialTheme.typography.titleSmall,
            color = cores.onBackground,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MotivoSaida.entries.forEach { opcao ->
                val ativo = opcao == selecionado
                val forma = MaterialTheme.shapes.medium
                Box(
                    modifier = Modifier
                        .widthIn(min = 80.dp)
                        .height(40.dp)
                        .clip(forma)
                        .background(if (ativo) cores.primary else cores.surfaceVariant)
                        .border(1.dp, if (ativo) cores.primary else cores.outline, forma)
                        .clickable(role = Role.RadioButton, onClick = { onSelecionar(opcao) })
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (opcao == MotivoSaida.VENDA) "Venda" else "Retirada",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (ativo) cores.onPrimary else cores.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun Rodape(carregando: Boolean, onConfirmar: () -> Unit, onVoltar: () -> Unit) {
    val cores = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(cores.primary)
                .clickable(enabled = !carregando, role = Role.Button, onClick = onConfirmar),
            contentAlignment = Alignment.Center,
        ) {
            if (carregando) {
                CircularProgressIndicator(
                    color = cores.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp),
                )
            } else {
                Text(
                    text = "Confirmar",
                    style = MaterialTheme.typography.titleSmall,
                    color = cores.onPrimary,
                )
            }
        }

        Text(
            text = "Cancelar",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onVoltar),
        )

        RodapeCopyright()
    }
}

@Composable
private fun MovimentoPreview(darkTheme: Boolean) {
    GestaoIcjTheme(darkTheme = darkTheme) {
        MovimentoScreen(
            produto = Produto("1", "Papel A4 500 folhas", "ICJ-001", "Papelaria", 24, "resmas"),
            carregando = false,
            erro = null,
            onEntrada = {},
            onSaida = { _, _ -> },
            onVoltar = {},
        )
    }
}

@Preview(name = "Escuro", widthDp = 390, heightDp = 844)
@Composable
private fun MovimentoEscuroPreview() = MovimentoPreview(darkTheme = true)

@Preview(name = "Claro", widthDp = 390, heightDp = 844)
@Composable
private fun MovimentoClaroPreview() = MovimentoPreview(darkTheme = false)
