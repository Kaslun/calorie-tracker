package com.kalori.app.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing scale + extended (non-Material) tokens.
 *
 * SEAM FOR designer: spacing/radii live here, not scattered as magic numbers. Components in
 * [com.kalori.app.ui.components] read from these via [LocalSpacing] / [LocalKaloriColors].
 */
data class Spacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
)

data class Radii(
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 20.dp,
)

/** Extended color tokens not expressible in a Material [androidx.compose.material3.ColorScheme]. */
data class KaloriColors(
    /** Subtle warmer shift for "significantly over target". NOT a warning/error color. */
    val overTarget: Color,
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
val LocalRadii = staticCompositionLocalOf { Radii() }
val LocalKaloriColors = staticCompositionLocalOf {
    KaloriColors(overTarget = OverTargetLight)
}
