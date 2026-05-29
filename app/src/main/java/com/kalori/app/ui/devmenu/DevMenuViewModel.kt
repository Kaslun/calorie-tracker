package com.kalori.app.ui.devmenu

import androidx.lifecycle.ViewModel
import com.kalori.app.core.config.FeatureFlagStore
import com.kalori.app.core.config.FeatureFlags
import com.kalori.app.core.seed.SeedManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Backs the debug dev menu. Debug-only affordances:
 *  - jump to date          (overrides the app's "today")
 *  - seed fake logs         (force reseed via SeedManager)
 *  - reset goal/calibration (seam — wired when GoalRepository writes are exercised)
 *  - force recalibration prompt
 *  - toggle every feature flag
 *
 * SEAM: the "current date" override lives here as a single source. feature-dev/data-layer
 * should read it (rather than LocalDate.now() directly) so the jump-to-date affordance works
 * across the app. Phase 1 keeps it in-memory.
 */
@HiltViewModel
class DevMenuViewModel @Inject constructor(
    private val seedManager: SeedManager,
    private val featureFlagStore: FeatureFlagStore,
) : ViewModel() {

    val flags: StateFlow<Map<FeatureFlags.Flag, Boolean>> = featureFlagStore.flags

    private val _overrideDate = MutableStateFlow<LocalDate?>(null)
    val overrideDate: StateFlow<LocalDate?> = _overrideDate.asStateFlow()

    private val _forceRecalibration = MutableStateFlow(false)
    val forceRecalibration: StateFlow<Boolean> = _forceRecalibration.asStateFlow()

    fun toggleFlag(flag: FeatureFlags.Flag, enabled: Boolean) =
        featureFlagStore.set(flag, enabled)

    fun resetFlags() = featureFlagStore.reset()

    fun jumpToDate(date: LocalDate) { _overrideDate.value = date }

    fun clearDateOverride() { _overrideDate.value = null }

    fun seedFakeLogs() = seedManager.forceReseed()

    fun forceRecalibrationPrompt() { _forceRecalibration.value = true }

    fun resetGoalAndCalibration() {
        // SEAM: clears the recalibration trigger now; data-layer wires the real goal/
        // calibration reset against GoalRepository when those flows are exercised.
        _forceRecalibration.value = false
    }
}
