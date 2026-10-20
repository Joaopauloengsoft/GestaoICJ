package com.gestaoicj.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gestaoicj.app.ui.theme.extraColors

@Composable
fun CabecalhoTela(titulo: String, subtitulo: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SimboloIcj()
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelLarge.copy(lineHeight = 19.2.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun RodapeCopyright(modifier: Modifier = Modifier) {
    Text(
        text = "© 2026 Gestão ICJ. Todos os direitos reservados.",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.extraColors.textMuted,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth(),
    )
}