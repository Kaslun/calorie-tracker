package com.kalori.app.ui.navigation

/**
 * Navigation destinations. Route stubs for the core surfaces.
 *
 * SEAM FOR feature-dev: the three primary screens per DESIGN_HANDOFF are Today / Stats /
 * Trend; AddFood and Settings are supporting. feature-dev fleshes out the composables and may
 * add argument-typed routes (e.g. food detail) — keep route constants centralized here.
 */
object Routes {
    const val TODAY = "today"
    const val ADD_FOOD = "add_food"
    const val STATS = "stats"
    const val TREND = "trend"
    const val SETTINGS = "settings"
}

/** Bottom-bar destinations (Today, Stats, Trend = three screens, three jobs). */
enum class TopLevelDestination(val route: String, val label: String) {
    TODAY(Routes.TODAY, "I dag"),
    STATS(Routes.STATS, "Statistikk"),
    TREND(Routes.TREND, "Trend"),
}
