package com.gestaoicj.app.ui.theme // TROQUE pelo package do seu projeto

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.gestaoicj.app.R // TROQUE pelo R do seu projeto

/** Cores lidas diretamente do frame "Gestão ICJ — Login Mobile". */
object IcjColors {
    val Background = Color(0xFF101112)      // topo do degradê
    val BackgroundEnd = Color(0xFF161718)   // base do degradê
    val Field = Color(0xFF1C1D1F)           // fundo de campos e checkbox
    val Border = Color(0xFF363739)
    val Yellow = Color(0xFFF4D54B)          // marca, botão, link
    val OnYellow = Color(0xFF161710)        // texto/ícone sobre o amarelo
    val TextPrimary = Color(0xFFF5F5F2)
    val TextSecondary = Color(0xFFA8AAAC)
    val TextMuted = Color(0xFF74777A)       // placeholder, copyright, separador
    val Error = Color(0xFFFF8A80)           // mensagens de erro (não existe no design original)
}

/**
 * Inter. Adicione os arquivos em res/font/ (minúsculas, sem hífen):
 * inter_regular.ttf, inter_medium.ttf, inter_semibold.ttf
 */
val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

private val IcjDarkColorScheme = darkColorScheme(
    primary = IcjColors.Yellow,
    onPrimary = IcjColors.OnYellow,
    background = IcjColors.Background,
    onBackground = IcjColors.TextPrimary,
    surface = IcjColors.Field,
    onSurface = IcjColors.TextPrimary,
    outline = IcjColors.Border,
)

/** O design só existe em modo escuro, então o tema é sempre escuro. */
@Composable
fun GestaoIcjTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = IcjDarkColorScheme, content = content)
}
