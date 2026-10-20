package com.gestaoicj.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// Valores brutos da paleta. As telas não usam estes vals: leem
// MaterialTheme.colorScheme e MaterialTheme.extraColors (ver Theme.kt).

// Marca (igual nos dois temas)
internal val IcjYellow = Color(0xFFF4D54B)
internal val IcjOnYellow = Color(0xFF161710)

// Escuro: valores do Figma
internal val DarkBackground = Color(0xFF101112)
internal val DarkBackgroundEnd = Color(0xFF161718)
internal val DarkField = Color(0xFF1C1D1F)
internal val DarkBorder = Color(0xFF363739)
internal val DarkBorderStrong = Color(0xFF3F4144)
internal val DarkTextPrimary = Color(0xFFF5F5F2)
internal val DarkTextSecondary = Color(0xFFA8AAAC)
internal val DarkTextMuted = Color(0xFF74777A)
internal val DarkError = Color(0xFFFF8A80)

// Claro: proposta, o Figma só tem o escuro. Validar com o design.
internal val LightBackground = Color(0xFFFAFAF7)
internal val LightBackgroundEnd = Color(0xFFF1F1EC)
internal val LightField = Color(0xFFFFFFFF)
internal val LightBorder = Color(0xFFD6D6D0)
internal val LightBorderStrong = Color(0xFFC9C9C3)
internal val LightTextPrimary = Color(0xFF161710)
internal val LightTextSecondary = Color(0xFF5B5E61)
internal val LightTextMuted = Color(0xFF6E7175)
internal val LightError = Color(0xFFB3261E)

internal val LightAccent = Color(0xFF7A5F00)

@Immutable
data class IcjExtraColors(
    val textMuted: Color,
    val borderStrong: Color,
    val accent: Color,
)

internal val DarkExtraColors = IcjExtraColors(
    textMuted = DarkTextMuted,
    borderStrong = DarkBorderStrong,
    accent = IcjYellow,
)

internal val LightExtraColors = IcjExtraColors(
    textMuted = LightTextMuted,
    borderStrong = LightBorderStrong,
    accent = LightAccent,
)