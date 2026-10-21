package com.gestaoicj.app.ui.cadastro

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gestaoicj.app.ui.components.CampoFormulario
import com.gestaoicj.app.ui.components.RodapeCopyright
import com.gestaoicj.app.ui.components.SimboloIcj
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import com.gestaoicj.app.ui.theme.extraColors

@Composable
fun CadastroContaScreen(
    carregando: Boolean,
    erro: String?,
    onCadastrar: (nome: String, email: String, senha: String) -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
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
                            rotulo = "Nome completo",
                            valor = nome,
                            onValorChange = { nome = it },
                            placeholder = "Seu nome",
                            leadingIcon = Icons.Outlined.Person,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next,
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            ),
                        )

                        CampoFormulario(
                            rotulo = "E-mail corporativo",
                            valor = email,
                            onValorChange = { email = it },
                            placeholder = "seu.nome@empresa.com.br",
                            leadingIcon = Icons.Outlined.Email,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            ),
                        )

                        CampoFormulario(
                            rotulo = "Senha",
                            valor = senha,
                            onValorChange = { senha = it },
                            placeholder = "Mínimo 6 caracteres",
                            leadingIcon = Icons.Outlined.Lock,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
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
                    onCadastrar = {
                        focusManager.clearFocus()
                        onCadastrar(nome.trim(), email.trim(), senha)
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
                text = "Criar conta",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Acesse o Gestão ICJ com sua conta.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Rodape(carregando: Boolean, onCadastrar: () -> Unit, onVoltar: () -> Unit) {
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
                .clickable(enabled = !carregando, role = Role.Button, onClick = onCadastrar),
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
                    text = "Criar conta",
                    style = MaterialTheme.typography.titleSmall,
                    color = cores.onPrimary,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Já tenho conta. ",
                style = MaterialTheme.typography.bodySmall,
                color = cores.onSurfaceVariant,
            )
            Text(
                text = "Entrar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extraColors.accent,
                modifier = Modifier.clickable(role = Role.Button, onClick = onVoltar),
            )
        }

        RodapeCopyright()
    }
}

@Composable
private fun CadastroPreview(darkTheme: Boolean) {
    GestaoIcjTheme(darkTheme = darkTheme) {
        CadastroContaScreen(
            carregando = false,
            erro = null,
            onCadastrar = { _, _, _ -> },
            onVoltar = {},
        )
    }
}

@Preview(name = "Escuro", widthDp = 390, heightDp = 844)
@Composable
private fun CadastroEscuroPreview() = CadastroPreview(darkTheme = true)

@Preview(name = "Claro", widthDp = 390, heightDp = 844)
@Composable
private fun CadastroClaroPreview() = CadastroPreview(darkTheme = false)
