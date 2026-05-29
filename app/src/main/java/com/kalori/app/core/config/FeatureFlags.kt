package com.kalori.app.core.config

/**
 * Single source of feature flags. Plain Kotlin booleans — no remote config (per handoff).
 *
 * v1.1 work that may be started early MUST sit behind a flag defaulting to false so release
 * behavior stays v1. The debug dev menu can toggle these at runtime via [FeatureFlagStore];
 * these are the compile-time defaults.
 */
object FeatureFlags {

    // --- v1 (shipping) ---
    const val PROJECTION_ENABLED = true
    const val TDEE_RECALIBRATION = true
    const val BARCODE_SCANNER = true

    // --- v1.1 (behind flags, default off) ---
    const val MICRONUTRIENTS = false
    const val PATTERN_LEARNING_MEAL_SLOT = false
    const val EXTRA_WIDGET_SIZES = false
    const val GITHUB_OTA_UPDATE = false

    /** Stable keys for runtime overrides in the debug dev menu. */
    enum class Flag(val key: String, val default: Boolean, val v11: Boolean) {
        PROJECTION("projection_enabled", PROJECTION_ENABLED, false),
        RECALIBRATION("tdee_recalibration", TDEE_RECALIBRATION, false),
        SCANNER("barcode_scanner", BARCODE_SCANNER, false),
        MICRONUTRIENTS_FLAG("micronutrients", MICRONUTRIENTS, true),
        PATTERN_LEARNING("pattern_learning_meal_slot", PATTERN_LEARNING_MEAL_SLOT, true),
        WIDGET_SIZES("extra_widget_sizes", EXTRA_WIDGET_SIZES, true),
        OTA("github_ota_update", GITHUB_OTA_UPDATE, true),
    }
}
