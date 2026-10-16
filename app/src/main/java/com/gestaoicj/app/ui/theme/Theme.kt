package com.gestaoicj.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Mapeamento dos tokens do design para papéis do Material:
//   amarelo → primary · texto sobre amarelo → onPrimary
//   fundo → background/surface · texto → onBackground/onSurface
//   texto secundário → onSurfaceVariant · campos/cartões → surfaceVariant
//   barra inferior → surfaceContainer · bordas → outline · erro → error
// Papéis não listados mantêm o padrão do Material (não são usados hoje).

private val DarkColorScheme = darkColorScheme(
    primary = IcjYellow,
    onPrimary = IcjOnYellow,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkBackground,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkField,
    onSurfaceVariant = DarkTextSecondary,
    surfaceContainer = DarkBackgroundEnd,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
    error = DarkError,
)

private val LightColorScheme = lightColorScheme(
    primary = IcjYellow,
    onPrimary = IcjOnYellow,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightBackground,
    onSurface = LightTextPrimary,
    surfaceVariant = LightField,
    onSurfaceVariant = LightTextSecondary,
    surfaceContainer = LightBackgroundEnd,
    outline = LightBorder,
    outlineVariant = LightBorder,
    error = LightError,
)

private val LocalIcjExtraColors = staticCompositionLocalOf { DarkExtraColors }

/** Cores que não existem no ColorScheme: `MaterialTheme.extraColors.textMuted`, etc. */
val MaterialTheme.extraColors: IcjExtraColors
    @Composable
    @ReadOnlyComposable
    get() = LocalIcjExtraColors.current

/**
 * Tema do Gestão ICJ. Segue o modo do sistema por padrão.
 *
 * Não usa cor dinâmica (Material You) de propósito: ela trocaria o amarelo da
 * marca pela cor do papel de parede do usuário.
 */
@Composable
fun GestaoIcjTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extraColors = if (darkTheme) DarkExtraColors else LightExtraColors

    // Ícones da barra de status/navegação: escuros no tema claro, claros no escuro.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalIcjExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = IcjTypography,
            shapes = IcjShapes,
            content = content,
        )
    }
}
