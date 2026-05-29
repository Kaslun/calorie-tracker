package com.kalori.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Color tokens — SINGLE source of truth for palette.
 *
 * SEAM FOR designer/feature-dev: this is the one place token changes land and propagate via
 * LiveEdit. Phase-1 placeholder palette only: a restrained neutral base with one primary.
 * The full system (incl. the "over target" warmer-shift treatment from DESIGN_HANDOFF) is a
 * later design pass — see [KaloriColors.overTarget] for the reserved seam.
 *
 * DESIGN constraint: "over target" is NEVER red / a warning. It is a subtle warmer shift only
 * when significantly over. Going over is information, never a fail state.
 */

// Brand / primary (placeholder).
internal val Primary = Color(0xFF6F5B3E)
internal val PrimaryDark = Color(0xFFDCC0A0)

// Neutrals.
internal val NeutralLightBackground = Color(0xFFFBF8F4)
internal val NeutralLightSurface = Color(0xFFFFFFFF)
internal val NeutralLightOnSurface = Color(0xFF1F1B16)

internal val NeutralDarkBackground = Color(0xFF15120E)
internal val NeutralDarkSurface = Color(0xFF1F1B16)
internal val NeutralDarkOnSurface = Color(0xFFECE0D1)

// Reserved "over target" warmer-shift accents (NOT red). Tuned in a later design pass.
internal val OverTargetLight = Color(0xFFB5651D)
internal val OverTargetDark = Color(0xFFE6A86B)
