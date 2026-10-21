package com.gestaoicj.app.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gestaoicj.app.R
import com.gestaoicj.app.ui.components.RodapeCopyright
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import com.gestaoicj.app.ui.theme.extraColors

// Largura aproximada do título "Gestão ICJ" a 48sp, medida no frame de 390dp.
private const val TitleWidthAt48Sp = 251f

private val SymbolWidth = 92.dp

@Composable
fun LoginScreen(
    onLogin: (email: String, password: String, remember: Boolean) -> Unit,
    onForgotPassword: (email: String) -> Unit,
    onHelp: () -> Unit,
    onPrivacy: () -> Unit,
    onTerms: () -> Unit,
    modifier: Modifier = Modifier,
    erro: String? = null,
    carregando: Boolean = false,
    onCadastrar: () -> Unit = {},
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var remember by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val cores = MaterialTheme.colorScheme

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(cores.background, cores.surfaceContainer))),
    ) {
        val screenHeight = maxHeight
        // O título ocupa a largura que sobra ao lado do símbolo; em telas estreitas a fonte encolhe.
        val availableForTitle = maxWidth - 24.dp - SymbolWidth - 6.dp
        val titleSize = minOf(48f, 48f * availableForTitle.value / TitleWidthAt48Sp).sp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = screenHeight)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 55.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 37.dp),
                    verticalArrangement = Arrangement.spacedBy(37.dp),
                ) {
                    InstitutionalHeader(titleSize = titleSize)

                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        AccessContext()

                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Welcome()

                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                IcjTextField(
                                    label = "E-mail corporativo",
                                    value = email,
                                    onValueChange = { email = it },
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

                                IcjTextField(
                                    label = "Senha",
                                    value = password,
                                    onValueChange = { password = it },
                                    placeholder = "Digite sua senha",
                                    leadingIcon = Icons.Outlined.Lock,
                                    visualTransformation =
                                        if (showPassword) VisualTransformation.None
                                        else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done,
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus()
                                            onLogin(email, password, remember)
                                        },
                                    ),
                                    trailing = {
                                        Box(
                                            modifier = Modifier
                                                .size(width = 48.dp, height = 52.dp)
                                                .clickable(role = Role.Button) { showPassword = !showPassword },
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(
                                                imageVector =
                                                    if (showPassword) Icons.Outlined.VisibilityOff
                                                    else Icons.Outlined.Visibility,
                                                contentDescription =
                                                    if (showPassword) "Ocultar senha" else "Mostrar senha",
                                                tint = cores.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp),
                                            )
                                        }
                                    },
                                )

                                AccessPreferences(
                                    remember = remember,
                                    onRememberChange = { remember = it },
                                    onForgotPassword = { onForgotPassword(email) },
                                )

                                if (erro != null) {
                                    Text(
                                        text = erro,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = cores.error,
                                    )
                                }

                                EnterButton(
                                    carregando = carregando,
                                    onClick = { onLogin(email, password, remember) },
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        text = "Não tem conta? ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        text = "Criar conta",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = MaterialTheme.extraColors.accent,
                                        modifier = Modifier.clickable(
                                            role = Role.Button,
                                            onClick = onCadastrar,
                                        ),
                                    )
                                }
                            }

                            AccessHelp(onHelp = onHelp)
                        }
                    }
                }

                Footer(onPrivacy = onPrivacy, onTerms = onTerms)
            }
        }
    }
}

@Composable
private fun InstitutionalHeader(titleSize: TextUnit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(154.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(width = SymbolWidth, height = 98.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.primary),
        ) {
            Image(
                painter = painterResource(R.drawable.logo_icj),
                contentDescription = "Símbolo do Gestão ICJ",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-4).dp)
                    .requiredSize(width = 99.dp, height = 91.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Gestão ICJ",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = titleSize),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Visible,
            )
            Text(
                text = "PLATAFORMA DE GESTÃO",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

@Composable
private fun AccessContext() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = "ACESSO CORPORATIVO",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun Welcome() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Bem-vindo de volta.",
            style = MaterialTheme.typography.headlineMedium.copy(lineHeight = 33.6.sp),
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Entre com sua conta para acessar o Gestão ICJ.",
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.4.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun IcjTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailing: (@Composable () -> Unit)? = null,
) {
    val forma = MaterialTheme.shapes.medium
    val cores = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = cores.onBackground,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(forma)
                .background(cores.surfaceVariant)
                .border(1.dp, cores.outline, forma)
                // Com o botão de olho, o padding direito entra na área de toque (48dp):
                // 2dp + 24dp = 26dp do centro do ícone até a borda, como no design.
                .padding(start = 16.dp, end = if (trailing != null) 2.dp else 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = cores.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                // O texto digitado não usa a altura de linha nem o espaçamento padrão do Text.
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = cores.onBackground,
                    lineHeight = TextUnit.Unspecified,
                    letterSpacing = TextUnit.Unspecified,
                ),
                cursorBrush = SolidColor(MaterialTheme.extraColors.accent),
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.extraColors.textMuted,
                                maxLines = 1,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            trailing?.invoke()
        }
    }
}

@Composable
private fun AccessPreferences(
    remember: Boolean,
    onRememberChange: (Boolean) -> Unit,
    onForgotPassword: () -> Unit,
) {
    val cores = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.toggleable(
                value = remember,
                role = Role.Checkbox,
                onValueChange = onRememberChange,
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val formaCaixa = MaterialTheme.shapes.extraSmall
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(formaCaixa)
                    .background(if (remember) cores.primary else cores.surfaceVariant)
                    .border(1.dp, if (remember) cores.primary else cores.outline, formaCaixa),
                contentAlignment = Alignment.Center,
            ) {
                if (remember) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = cores.onPrimary,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            Text(
                text = "Lembrar de mim",
                style = MaterialTheme.typography.bodySmall,
                color = cores.onSurfaceVariant,
            )
        }

        Text(
            text = "Esqueci minha senha",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.extraColors.accent,
            modifier = Modifier.clickable(role = Role.Button, onClick = onForgotPassword),
        )
    }
}

@Composable
private fun EnterButton(carregando: Boolean, onClick: () -> Unit) {
    val cores = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(cores.primary)
            .clickable(enabled = !carregando, role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (carregando) {
            CircularProgressIndicator(
                color = cores.onPrimary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp),
            )
            return@Row
        }
        Text(
            text = "Entrar",
            // No design o botão usa Inter Regular.
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
            color = cores.onPrimary,
        )
        Spacer(Modifier.width(12.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = cores.onPrimary,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun AccessHelp(onHelp: () -> Unit) {
    val cores = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Ainda não tem acesso? Fale com o administrador da sua organização.",
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.8.sp),
            color = cores.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier.clickable(role = Role.Button, onClick = onHelp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                contentDescription = null,
                tint = cores.onBackground,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "Precisa de ajuda?",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = cores.onBackground,
            )
        }
    }
}

@Composable
private fun Footer(onPrivacy: () -> Unit, onTerms: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FooterLink("Privacidade", onPrivacy)
            Box(
                modifier = Modifier
                    .size(3.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.extraColors.textMuted),
            )
            FooterLink("Termos de uso", onTerms)
        }
        RodapeCopyright()
    }
}

@Composable
private fun FooterLink(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
    )
}

@Composable
private fun LoginPreview(darkTheme: Boolean) {
    GestaoIcjTheme(darkTheme = darkTheme) {
        LoginScreen(
            onLogin = { _, _, _ -> },
            onForgotPassword = {},
            onHelp = {},
            onPrivacy = {},
            onTerms = {},
        )
    }
}

@Preview(name = "Escuro", widthDp = 390, heightDp = 844)
@Composable
private fun LoginEscuroPreview() = LoginPreview(darkTheme = true)

@Preview(name = "Claro", widthDp = 390, heightDp = 844)
@Composable
private fun LoginClaroPreview() = LoginPreview(darkTheme = false)