package com.gestaoicj.app.ui.login // TROQUE pelo package do seu projeto

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gestaoicj.app.R // TROQUE pelo R do seu projeto
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import com.gestaoicj.app.ui.theme.IcjColors
import com.gestaoicj.app.ui.theme.Inter
import androidx.compose.material3.CircularProgressIndicator

// Largura aproximada do título "Gestão ICJ" a 48sp, medida no frame de 390dp.
private const val TitleWidthAt48Sp = 251f

@Composable
fun LoginScreen(
    onLogin: (email: String, password: String, remember: Boolean) -> Unit,
    onForgotPassword: () -> Unit,
    onHelp: () -> Unit,
    onPrivacy: () -> Unit,
    onTerms: () -> Unit,
    modifier: Modifier = Modifier,
    erro: String? = null,
    carregando: Boolean = false,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var remember by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(IcjColors.Background, IcjColors.BackgroundEnd))),
    ) {
        val screenHeight = maxHeight
        // O título ocupa a largura restante ao lado do símbolo (só margem esquerda de 24dp,
        // como no design, onde ele avança sobre a margem direita). Em telas estreitas a fonte encolhe.
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
                                                tint = IcjColors.TextSecondary,
                                                modifier = Modifier.size(20.dp),
                                            )
                                        }
                                    },
                                )

                                AccessPreferences(
                                    remember = remember,
                                    onRememberChange = { remember = it },
                                    onForgotPassword = onForgotPassword,
                                )

                                if (erro != null) {
                                    Text(
                                        text = erro,
                                        color = IcjColors.Error,
                                        fontFamily = Inter,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 13.sp,
                                    )
                                }

                                EnterButton(
                                    carregando = carregando,
                                    onClick = { onLogin(email, password, remember) },
                                )
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

private val SymbolWidth = 92.dp

// ───────────────────────── Cabeçalho ─────────────────────────

@Composable
private fun InstitutionalHeader(titleSize: androidx.compose.ui.unit.TextUnit) {
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
                .clip(RoundedCornerShape(6.dp))
                .background(IcjColors.Yellow),
        ) {
            // Adicione o PNG exportado do nó "image 1" (37:65) como res/drawable/logo_icj.png

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
                color = IcjColors.TextPrimary,
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = titleSize,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Visible,
            )
            Text(
                text = "PLATAFORMA DE GESTÃO",
                color = IcjColors.TextSecondary,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

// ───────────────────────── Acesso ─────────────────────────

@Composable
private fun AccessContext() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Lock,
            contentDescription = null,
            tint = IcjColors.TextSecondary,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = "ACESSO CORPORATIVO",
            color = IcjColors.TextSecondary,
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun Welcome() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Bem-vindo de volta.",
            color = IcjColors.TextPrimary,
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 33.6.sp, // 1.2 × 28
        )
        Text(
            text = "Entre com sua conta para acessar o Gestão ICJ.",
            color = IcjColors.TextSecondary,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 22.4.sp, // 1.6 × 14
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
    val shape = RoundedCornerShape(8.dp)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            color = IcjColors.TextPrimary,
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(shape)
                .background(IcjColors.Field)
                .border(1.dp, IcjColors.Border, shape)
                // Com o botão de olho, o padding direito vai para dentro da área de toque (48dp):
                // 2dp + 24dp (metade da área) = 26dp do centro do ícone até a borda, igual ao design.
                .padding(start = 16.dp, end = if (trailing != null) 2.dp else 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = IcjColors.TextSecondary,
                modifier = Modifier.size(20.dp),
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = IcjColors.TextPrimary,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                ),
                cursorBrush = SolidColor(IcjColors.Yellow),
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = IcjColors.TextMuted,
                                fontFamily = Inter,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
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
            val boxShape = RoundedCornerShape(4.dp)
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(boxShape)
                    .background(if (remember) IcjColors.Yellow else IcjColors.Field)
                    .border(1.dp, if (remember) IcjColors.Yellow else IcjColors.Border, boxShape),
                contentAlignment = Alignment.Center,
            ) {
                if (remember) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = IcjColors.OnYellow,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            Text(
                text = "Lembrar de mim",
                color = IcjColors.TextSecondary,
                fontFamily = Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
            )
        }

        Text(
            text = "Esqueci minha senha",
            color = IcjColors.Yellow,
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            modifier = Modifier.clickable(role = Role.Button, onClick = onForgotPassword),
        )
    }
}

@Composable
private fun EnterButton(carregando: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(IcjColors.Yellow)
            .clickable(enabled = !carregando, role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (carregando) {
            CircularProgressIndicator(
                color = IcjColors.OnYellow,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp),
            )
            return@Row
        }
        Text(
            text = "Entrar",
            color = IcjColors.OnYellow,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal, // no design o botão usa Inter Regular
            fontSize = 16.sp,
        )
        Spacer(Modifier.width(12.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = IcjColors.OnYellow,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun AccessHelp(onHelp: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Ainda não tem acesso? Fale com o administrador da sua organização.",
            color = IcjColors.TextSecondary,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 20.8.sp, // 1.6 × 13
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
                tint = IcjColors.TextPrimary,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "Precisa de ajuda?",
                color = IcjColors.TextPrimary,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
            )
        }
    }
}

// ───────────────────────── Rodapé ─────────────────────────

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
                    .background(IcjColors.TextMuted),
            )
            FooterLink("Termos de uso", onTerms)
        }
        Text(
            text = "© 2026 Gestão ICJ. Todos os direitos reservados.",
            color = IcjColors.TextMuted,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FooterLink(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = IcjColors.TextSecondary,
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
    )
}

@Preview(widthDp = 390, heightDp = 844, showBackground = true, backgroundColor = 0xFF101112)
@Composable
private fun LoginScreenPreview() {
    GestaoIcjTheme {
        LoginScreen(
            onLogin = { _, _, _ -> },
            onForgotPassword = {},
            onHelp = {},
            onPrivacy = {},
            onTerms = {},
        )
    }
}
