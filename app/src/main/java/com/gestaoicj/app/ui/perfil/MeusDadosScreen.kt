package com.gestaoicj.app.ui.perfil

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gestaoicj.app.ui.components.RodapeCopyright
import com.gestaoicj.app.ui.components.SimboloIcj
import com.gestaoicj.app.ui.theme.GestaoIcjTheme
import com.gestaoicj.app.ui.theme.extraColors

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
                    DadosPessoais(perfil)
                }
                Rodape(onSair)
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
                text = "Gestão ICJ",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "PLATAFORMA DE GESTÃO",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DadosPessoais(perfil: Perfil) {
    val tipografia = MaterialTheme.typography
    val cores = MaterialTheme.colorScheme

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Meus Dados",
                style = tipografia.headlineMedium.copy(lineHeight = 33.6.sp),
                color = cores.onBackground,
            )
            Text(
                text = "Confira seu perfil e gerencie seu acesso.",
                style = tipografia.bodyMedium.copy(lineHeight = 22.4.sp),
                color = cores.onSurfaceVariant,
            )
        }

        CartaoPerfil(perfil)

        Text(
            text = "Para atualizar seus dados, fale com o administrador da sua organização.",
            style = tipografia.bodySmall.copy(lineHeight = 19.5.sp),
            color = cores.onSurfaceVariant,
        )
    }
}

@Composable
private fun CartaoPerfil(perfil: Perfil) {
    val forma = MaterialTheme.shapes.medium
    val cores = MaterialTheme.colorScheme
    val tipografia = MaterialTheme.typography

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(cores.surfaceVariant)
            .border(1.dp, cores.outline, forma)
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
                    .background(cores.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = iniciais(perfil.nome),
                    style = tipografia.titleLarge,
                    color = cores.onPrimary,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = perfil.nome,
                    style = tipografia.titleLarge.copy(lineHeight = 23.4.sp),
                    color = cores.onBackground,
                )
                Text(
                    text = perfil.cargo,
                    style = tipografia.bodySmall.copy(lineHeight = 18.2.sp),
                    color = cores.onSurfaceVariant,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(cores.outline),
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
        Text(
            text = rotulo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 19.6.sp),
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun Rodape(onSair: () -> Unit) {
    val forma = MaterialTheme.shapes.large
    val corTexto = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(forma)
                .border(1.dp, MaterialTheme.extraColors.borderStrong, forma)
                .clickable(role = Role.Button, onClick = onSair)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = null,
                tint = corTexto,
                modifier = Modifier.size(18.dp),
            )
            // O texto ocupa o espaço restante e fica centralizado nele.
            Text(
                text = "Sair",
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp),
                color = corTexto,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
        }

        RodapeCopyright()
    }
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

@Composable
private fun PerfilPreview(darkTheme: Boolean) {
    GestaoIcjTheme(darkTheme = darkTheme) {
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

@Preview(name = "Escuro", widthDp = 390, heightDp = 844)
@Composable
private fun PerfilEscuroPreview() = PerfilPreview(darkTheme = true)

@Preview(name = "Claro", widthDp = 390, heightDp = 844)
@Composable
private fun PerfilClaroPreview() = PerfilPreview(darkTheme = false)