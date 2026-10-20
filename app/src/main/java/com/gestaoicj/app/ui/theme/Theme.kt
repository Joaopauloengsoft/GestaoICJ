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

/** Cores fora do ColorScheme: `MaterialTheme.extraColors.textMuted`, etc. */
val MaterialTheme.extraColors: IcjExtraColors
    @Composable
    @ReadOnlyComposable
    get() = LocalIcjExtraColors.current

/** Tema do Gestão ICJ. Segue o modo do sistema e não usa cor dinâmica, para manter o amarelo da marca. */
@Composable
fun GestaoIcjTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extraColors = if (darkTheme) DarkExtraColors else LightExtraColors

    // Ícones das barras do sistema: escuros no tema claro, claros no escuro.
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