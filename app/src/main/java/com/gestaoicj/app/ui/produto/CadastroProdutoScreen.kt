package com.gestaoicj.app.ui.produto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.QrCode
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import com.gestaoicj.app.ui.components.CampoFormulario
import com.gestaoicj.app.ui.components.RodapeCopyright
import com.gestaoicj.app.ui.components.SimboloIcj
import com.gestaoicj.app.ui.theme.GestaoIcjTheme

@Composable
fun CadastroProdutoScreen(
    carregando: Boolean,
    erro: String?,
    onSalvar: (codigo: String, nome: String, qtdInicial: Int, minimo: Int, unidade: String) -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var codigo by rememberSaveable { mutableStateOf("") }
    var nome by rememberSaveable { mutableStateOf("") }
    var qtdInicial by rememberSaveable { mutableStateOf("") }
    var minimo by rememberSaveable { mutableStateOf("") }
    var unidade by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val alturaTela = maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = alturaTela)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(36.dp),
                ) {
                    Cabecalho()

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        CampoFormulario(
                            rotulo = "Código de barras",
                            valor = codigo,
                            onValorChange = { codigo = it },
                            placeholder = "EAN-13 ou código interno",
                            leadingIcon = Icons.Outlined.QrCode,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            ),
                        )

                        CampoFormulario(
                            rotulo = "Nome do produto",
                            valor = nome,
                            onValorChange = { nome = it },
                            placeholder = "Ex: Papel A4 500 folhas",
                            leadingIcon = Icons.Outlined.Inventory2,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Next,
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            ),
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CampoFormulario(
                                rotulo = "Qtd. inicial",
                                valor = qtdInicial,
                                onValorChange = { qtdInicial = it.filter { c -> c.isDigit() } },
                                placeholder = "0",
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next,
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                ),
                                modifier = Modifier.weight(1f),
                            )
                            CampoFormulario(
                                rotulo = "Estoque mínimo",
                                valor = minimo,
                                onValorChange = { minimo = it.filter { c -> c.isDigit() } },
                                placeholder = "0",
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next,
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                ),
                                modifier = Modifier.weight(1f),
                            )
                        }

                        CampoFormulario(
                            rotulo = "Unidade",
                            valor = unidade,
                            onValorChange = { unidade = it },
                            placeholder = "Ex: un, cx, kg, L",
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() },
                            ),
                        )

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
                    onSalvar = {
                        focusManager.clearFocus()
                        onSalvar(
                            codigo.trim(),
                            nome.trim(),
                            qtdInicial.toIntOrNull() ?: 0,
                            minimo.toIntOrNull() ?: 0,
                            unidade.trim(),
                        )
                    },
                    onVoltar = onVoltar,
                )
            }
        }
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
                text = "Novo produto",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Preencha os dados para cadastrar.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Rodape(carregando: Boolean, onSalvar: () -> Unit, onVoltar: () -> Unit) {
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
                .clickable(enabled = !carregando, role = Role.Button, onClick = onSalvar),
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
                    text = "Salvar produto",
                    style = MaterialTheme.typography.titleSmall,
                    color = cores.onPrimary,
                )
            }
        }

        Text(
            text = "Cancelar",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(role = Role.Button, onClick = onVoltar),
        )

        RodapeCopyright()
    }
}

@Composable
private fun CadastroProdutoPreview(darkTheme: Boolean) {
    GestaoIcjTheme(darkTheme = darkTheme) {
        CadastroProdutoScreen(
            carregando = false,
            erro = null,
            onSalvar = { _, _, _, _, _ -> },
            onVoltar = {},
        )
    }
}

@Preview(name = "Escuro", widthDp = 390, heightDp = 844)
@Composable
private fun CadastroProdutoEscuroPreview() = CadastroProdutoPreview(darkTheme = true)

@Preview(name = "Claro", widthDp = 390, heightDp = 844)
@Composable
private fun CadastroProdutoClaroPreview() = CadastroProdutoPreview(darkTheme = false)
