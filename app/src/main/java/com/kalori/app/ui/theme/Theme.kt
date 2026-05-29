package com.kalori.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * App theme. The single entry point that wires color/type/spacing tokens into the tree.
 *
 * SEAM FOR designer: this is the propagation point for token changes. Note: NO dynamic color
 * (Material You) — Kalori uses a deliberate restrained palette per DESIGN_HANDOFF, so we do
 * not pull from the device wallpaper.
 */
private val LightColors = lightColorScheme(
    primary = Primary,
    background = NeutralLightBackground,
    surface = NeutralLightSurface,
    onBackground = NeutralLightOnSurface,
    onSurface = NeutralLightOnSurface,
)

private val DarkColors = darkColorScheme(
    primary = PrimaryDark,
    background = NeutralDarkBackground,
    surface = NeutralDarkSurface,
    onBackground = NeutralDarkOnSurface,
    onSurface = NeutralDarkOnSurface,
)

@Composable
fun KaloriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val kaloriColors = KaloriColors(
        overTarget = if (darkTheme) OverTargetDark else OverTargetLight,
    )

    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalRadii provides Radii(),
        LocalKaloriColors provides kaloriColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
