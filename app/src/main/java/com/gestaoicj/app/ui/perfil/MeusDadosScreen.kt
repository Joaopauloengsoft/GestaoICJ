package com.gestaoicj.app.ui.perfil // TROQUE pelo package do seu projeto

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import com.gestaoicj.app.ui.theme.IcjColors
import com.gestaoicj.app.ui.theme.Inter

// Borda do botão "Sair" (#3F4144): é mais clara que IcjColors.Border e só aparece nesta tela.
private val BordaSair = Color(0xFF3F4144)

data class Perfil(
    val nome: String,
    val cargo: String,
    val email: String,
    val telefone: String,
    val organizacao: String,
)

@Composable
fun MeusDadosScreen(
    perfil: Perfil,
    onSair: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(IcjColors.Background),
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
                    DadosPessoais(perfil)
                }
                Rodape(onSair)
            }
        }
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
            Txt("Gestão ICJ", 24.sp, FontWeight.SemiBold, IcjColors.TextPrimary)
            Txt("PLATAFORMA DE GESTÃO", 10.sp, FontWeight.Medium, IcjColors.TextSecondary)
        }
    }
}

/**
 * Símbolo 48×48. Por enquanto mostra "ICJ" como provisório.
 * Quando adicionar res/drawable/logo_icj, troque o Text pelo Image comentado abaixo.
 */
@Composable
private fun SimboloIcj() {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(IcjColors.Yellow),
        contentAlignment = Alignment.Center,
    ) {
        Txt("ICJ", 16.sp, FontWeight.SemiBold, IcjColors.OnYellow)

        // Image(
        //     painter = painterResource(R.drawable.logo_icj),
        //     contentDescription = "Símbolo do Gestão ICJ",
        //     contentScale = ContentScale.Crop,
        //     modifier = Modifier
        //         .align(Alignment.BottomCenter)
        //         .offset(y = (-4).dp)
        //         .requiredSize(width = 46.dp, height = 42.dp),
        // )
    }
}

// ───────────────────────── Dados pessoais ─────────────────────────

@Composable
private fun DadosPessoais(perfil: Perfil) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Txt(
                text = "Meus Dados",
                size = 28.sp,
                weight = FontWeight.SemiBold,
                color = IcjColors.TextPrimary,
                lineHeight = 33.6.sp, // 1.2 × 28
            )
            Txt(
                text = "Confira seu perfil e gerencie seu acesso.",
                size = 14.sp,
                weight = FontWeight.Normal,
                color = IcjColors.TextSecondary,
                lineHeight = 22.4.sp, // 1.6 × 14
            )
        }

        CartaoPerfil(perfil)

        Txt(
            text = "Para atualizar seus dados, fale com o administrador da sua organização.",
            size = 13.sp,
            weight = FontWeight.Normal,
            color = IcjColors.TextSecondary,
            lineHeight = 19.5.sp, // 1.5 × 13
        )
    }
}

@Composable
private fun CartaoPerfil(perfil: Perfil) {
    val forma = RoundedCornerShape(8.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(IcjColors.Field)
            .border(1.dp, IcjColors.Border, forma)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(IcjColors.Yellow),
                contentAlignment = Alignment.Center,
            ) {
                Txt(iniciais(perfil.nome), 18.sp, FontWeight.SemiBold, IcjColors.OnYellow)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Txt(
                    text = perfil.nome,
                    size = 18.sp,
                    weight = FontWeight.SemiBold,
                    color = IcjColors.TextPrimary,
                    lineHeight = 23.4.sp, // 1.3 × 18
                )
                Txt(
                    text = perfil.cargo,
                    size = 13.sp,
                    weight = FontWeight.Normal,
                    color = IcjColors.TextSecondary,
                    lineHeight = 18.2.sp, // 1.4 × 13
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(IcjColors.Border),
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DadoDoPerfil("E-mail corporativo", perfil.email)
            DadoDoPerfil("Telefone", perfil.telefone)
            DadoDoPerfil("Organização", perfil.organizacao)
        }
    }
}

@Composable
private fun DadoDoPerfil(rotulo: String, valor: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Txt(rotulo, 13.sp, FontWeight.Normal, IcjColors.TextSecondary)
        Txt(
            text = valor,
            size = 14.sp,
            weight = FontWeight.Normal,
            color = IcjColors.TextPrimary,
            lineHeight = 19.6.sp, // 1.4 × 14
        )
    }
}

// ───────────────────────── Rodapé ─────────────────────────

@Composable
private fun Rodape(onSair: () -> Unit) {
    val forma = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(forma)
                .border(1.dp, BordaSair, forma)
                .clickable(role = Role.Button, onClick = onSair)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
            // No design o texto ocupa o espaço restante e fica centralizado nele.
            Txt(
                text = "Sair",
                size = 15.sp,
                weight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.weight(1f),
                align = TextAlign.Center,
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

// ───────────────────────── Utilitários ─────────────────────────

@Composable
private fun Txt(
    text: String,
    size: TextUnit,
    weight: FontWeight,
    color: Color,
    modifier: Modifier = Modifier,
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
        textAlign = align,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

/** "Maria Oliveira" → "MO" (primeira letra do primeiro e do último nome). */
private fun iniciais(nome: String): String {
    val partes = nome.trim().split(" ").filter { it.isNotEmpty() }
    return when {
        partes.isEmpty() -> ""
        partes.size == 1 -> partes.first().take(1)
        else -> "${partes.first().first()}${partes.last().first()}"
    }.uppercase()
}

@Preview(widthDp = 390, heightDp = 844, showBackground = true, backgroundColor = 0xFF101112)
@Composable
private fun MeusDadosScreenPreview() {
    GestaoIcjTheme {
        MeusDadosScreen(
            perfil = Perfil(
                nome = "Maria Oliveira",
                cargo = "Analista administrativa",
                email = "maria.oliveira@icj.org.br",
                telefone = "(11) 98765-4321",
                organizacao = "Instituto de Cidadania e Justiça",
            ),
            onSair = {},
        )
    }
}
