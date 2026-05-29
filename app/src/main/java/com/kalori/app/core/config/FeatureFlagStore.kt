package com.kalori.app.core.config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Runtime feature-flag overrides for the debug dev menu.
 *
 * Phase-1 seam: holds overrides in memory. The orchestrator/data-layer may later persist
 * these to DataStore so toggles survive process death — the [StateFlow] surface stays the
 * same. In release builds nothing toggles these (dev menu is gated off), so the compile-time
 * defaults in [FeatureFlags] are what ships.
 */
@Singleton
class FeatureFlagStore @Inject constructor() {

    private val overrides = MutableStateFlow(
        FeatureFlags.Flag.entries.associateWith { it.default },
    )

    val flags: StateFlow<Map<FeatureFlags.Flag, Boolean>> = overrides

    fun isEnabled(flag: FeatureFlags.Flag): Boolean = overrides.value[flag] ?: flag.default

    fun set(flag: FeatureFlags.Flag, enabled: Boolean) {
        overrides.value = overrides.value.toMutableMap().apply { put(flag, enabled) }
    }

    fun reset() {
        overrides.value = FeatureFlags.Flag.entries.associateWith { it.default }
    }
}
