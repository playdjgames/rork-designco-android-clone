package com.DesignCo.djgames.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DesignCoColorScheme = darkColorScheme(
    primary = DC.Gold,
    onPrimary = DC.OnGold,
    primaryContainer = DC.GoldDeep,
    onPrimaryContainer = DC.Ivory,
    secondary = DC.GoldLight,
    onSecondary = DC.OnGold,
    background = DC.Canvas,
    onBackground = DC.Ivory,
    surface = DC.Surface,
    onSurface = DC.Ivory,
    surfaceVariant = DC.SurfaceHigh,
    onSurfaceVariant = DC.Muted,
    surfaceContainerLowest = DC.Canvas,
    surfaceContainerLow = DC.Surface,
    surfaceContainer = DC.Surface,
    surfaceContainerHigh = DC.SurfaceHigh,
    surfaceContainerHighest = DC.SurfaceHigh,
    outline = DC.Border,
    outlineVariant = DC.Divider,
    inverseSurface = DC.SurfaceHigh,
    inverseOnSurface = DC.Ivory,
    inversePrimary = DC.Gold,
    error = DC.Danger
)

/** Always-dark luxury theme; DesignCo's identity does not switch with the system theme. */
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DesignCoColorScheme,
        typography = AppTypography,
        content = content
    )
}
