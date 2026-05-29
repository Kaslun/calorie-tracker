package com.kalori.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography tokens.
 *
 * SEAM FOR designer: Phase-1 placeholder scale on the platform default family. The designer
 * picks the real typeface (one with good TABULAR FIGURES so animated calorie/gram/weight
 * counters don't jitter — see DESIGN_HANDOFF motion spec). [TabularNumeric] reserves that
 * seam: any constantly-changing numeric display should use a style built on it.
 */

val AppTypography = Typography()

/** Use for animated counters and any constantly-changing numbers (tabular figures). */
val TabularNumeric: TextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 32.sp,
    // Request tabular (monospaced) figures so digit width is stable during count animations.
    fontFeatureSettings = "tnum",
)
