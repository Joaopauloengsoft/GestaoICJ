package com.gestaoicj.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// ════════════════════════════════════════════════════════════════════════════
//  PALETA BRUTA — só valores. As telas NÃO usam estes vals direto: elas leem
//  MaterialTheme.colorScheme / MaterialTheme.extraColors (ver Theme.kt),
//  que trocam sozinhos entre claro e escuro.
// ════════════════════════════════════════════════════════════════════════════

// ── Marca (igual nos dois temas) ────────────────────────────────────────────
internal val IcjYellow = Color(0xFFF4D54B)    // marca, botões, indicador de aba
internal val IcjOnYellow = Color(0xFF161710)  // texto/ícone sobre o amarelo (12,4:1)

// ── Escuro: valores lidos do frame do Figma. NÃO ALTERAR sem mexer no design ─
internal val DarkBackground = Color(0xFF101112)
internal val DarkBackgroundEnd = Color(0xFF161718)  // barra inferior / fim do degradê
internal val DarkField = Color(0xFF1C1D1F)          // campos, cartões, checkbox
internal val DarkBorder = Color(0xFF363739)
internal val DarkBorderStrong = Color(0xFF3F4144)   // contorno do botão "Sair"
internal val DarkTextPrimary = Color(0xFFF5F5F2)
internal val DarkTextSecondary = Color(0xFFA8AAAC)
internal val DarkTextMuted = Color(0xFF74777A)      // placeholder, copyright
internal val DarkError = Color(0xFFFF8A80)

// ── Claro: PROPOSTA (o Figma só tem o escuro). Validar com o design. ─────────
// Contrastes WCAG calculados contra Background/Field/barra:
//   TextPrimary ≥ 15,9:1 · TextSecondary ≥ 5,7:1 · TextMuted ≥ 4,3:1
//   Accent ≥ 5,3:1 · Error ≥ 5,7:1
internal val LightBackground = Color(0xFFFAFAF7)
internal val LightBackgroundEnd = Color(0xFFF1F1EC)
internal val LightField = Color(0xFFFFFFFF)
internal val LightBorder = Color(0xFFD6D6D0)
internal val LightBorderStrong = Color(0xFFC9C9C3)
internal val LightTextPrimary = Color(0xFF161710)
internal val LightTextSecondary = Color(0xFF5B5E61)
internal val LightTextMuted = Color(0xFF6E7175)
internal val LightError = Color(0xFFB3261E)

// O amarelo #F4D54B vale como PREENCHIMENTO nos dois temas, mas como TEXTO sobre
// fundo claro dá só 1,4:1 (ilegível). Por isso, no claro, texto/cursor/link
// amarelos usam este âmbar escuro (mesma família de cor).
internal val LightAccent = Color(0xFF7A5F00)

// ════════════════════════════════════════════════════════════════════════════
//  CORES EXTRAS — o que não tem papel equivalente no ColorScheme do Material
// ════════════════════════════════════════════════════════════════════════════

/**
 * @property textMuted texto de baixa ênfase (placeholder, copyright).
 * @property borderStrong contorno mais forte que `outline` (botão "Sair").
 * @property accent o amarelo usado como TEXTO/cursor/link (no escuro é o
 *   próprio amarelo da marca; no claro é um âmbar escuro legível).
 */
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

// ════════════════════════════════════════════════════════════════════════════
//  TRANSITÓRIO — apagar quando Login, Reposição, Histórico e Perfil migrarem
// ════════════════════════════════════════════════════════════════════════════

/**
 * Valores escuros fixos, mantidos só para as telas que ainda não foram migradas
 * continuarem compilando e idênticas. Cada uso aparece como aviso de depreciação
 * no Android Studio: é a sua lista do que falta migrar.
 */
@Deprecated(
    message = "Fixo no escuro. Use MaterialTheme.colorScheme / MaterialTheme.extraColors.",
)
object IcjColors {
    val Background = DarkBackground
    val BackgroundEnd = DarkBackgroundEnd
    val Field = DarkField
    val Border = DarkBorder
    val Yellow = IcjYellow
    val OnYellow = IcjOnYellow
    val TextPrimary = DarkTextPrimary
    val TextSecondary = DarkTextSecondary
    val TextMuted = DarkTextMuted
    val Error = DarkError
}
