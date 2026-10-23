package com.gestaoicj.app.ui.produto

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gestaoicj.app.ui.components.CampoFormulario
import com.gestaoicj.app.ui.components.RodapeCopyright
import com.gestaoicj.app.ui.components.SimboloIcj
import com.gestaoicj.app.ui.estoque.Produto
import com.gestaoicj.app.ui.theme.GestaoIcjTheme

@Composable
fun EditarProdutoScreen(
    produto: Produto?,
    carregando: Boolean,
    erro: String?,
    onSalvar: (nome: String, minimo: Int, unidade: String) -> Unit,
    onExcluir: () -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var nome by rememberSaveable(produto?.id) { mutableStateOf(produto?.nome ?: "") }
    var minimo by rememberSaveable(produto?.id) { mutableStateOf((produto?.estoqueMinimo ?: 0).toString()) }
    var unidade by rememberSaveable(produto?.id) { mutableStateOf(produto?.unidade ?: "") }
    var mostrarConfirmacao by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    if (mostrarConfirmacao) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacao = false },
            title = { Text("Excluir produto?") },
            text = { Text("O produto será removido do estoque.") },
            confirmButton = {
                TextButton(onClick = { mostrarConfirmacao = false; onExcluir() }) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacao = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val alturaTela = maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
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
                                text = "Editar produto",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Text(
                                text = produto?.nome ?: "",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        CampoFormulario(
                            rotulo = "Nome do produto",
                            valor = nome,
                            onValorChange = { nome = it },
                            placeholder = "Ex: Papel A4 500 folhas",
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
                            CampoFormulario(
                                rotulo = "Unidade",
                                valor = unidade,
                                onValorChange = { unidade = it },
                                placeholder = "Ex: un, cx, kg",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Done,
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() },
                                ),
                                modifier = Modifier.weight(1f),
                            )
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

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(enabled = !carregando, role = Role.Button, onClick = {
                                focusManager.clearFocus()
                                onSalvar(nome.trim(), minimo.toIntOrNull() ?: 0, unidade.trim())
                            }),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (carregando) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp),
                            )
                        } else {
                            Text(
                                text = "Salvar alterações",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }

                    Text(
                        text = "Excluir produto",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable(role = Role.Button, onClick = { mostrarConfirmacao = true }),
                    )

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
        }
    }
}

@Composable
private fun EditarProdutoPreview(darkTheme: Boolean) {
    GestaoIcjTheme(darkTheme = darkTheme) {
        EditarProdutoScreen(
            produto = Produto("1", "Papel A4 500 folhas", "ICJ-001", "Papelaria", 24, "resmas", estoqueMinimo = 5),
            carregando = false,
            erro = null,
            onSalvar = { _, _, _ -> },
            onExcluir = {},
            onVoltar = {},
        )
    }
}

@Preview(name = "Escuro", widthDp = 390, heightDp = 844)
@Composable
private fun EditarProdutoEscuroPreview() = EditarProdutoPreview(darkTheme = true)

@Preview(name = "Claro", widthDp = 390, heightDp = 844)
@Composable
private fun EditarProdutoClaroPreview() = EditarProdutoPreview(darkTheme = false)
