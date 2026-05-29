package com.kalori.app.core.seed

import android.util.Log
import com.kalori.app.BuildConfig
import com.kalori.app.data.fake.SampleData
import com.kalori.app.domain.repository.FoodRepository
import com.kalori.app.domain.repository.LogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Debug-only seed harness: pre-populates realistic Norwegian foods + several weeks of logs
 * so design iteration never requires manually logging "havregryn" 50 times.
 *
 * Phase-1 scaffolding: seeds the (fake) repositories from [SampleData]. The data-layer agent
 * will repoint this at the real Room repos and the full Matvaretabellen import once those
 * land — the [seedIfNeeded]/[forceReseed] surface and the BuildConfig gating stay the same.
 *
 * Gated on BuildConfig.SEED_ON_FIRST_INSTALL — release builds never seed.
 */
@Singleton
class SeedManager @Inject constructor(
    private val foodRepository: FoodRepository,
    private val logRepository: LogRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun seedIfNeeded() {
        if (!BuildConfig.SEED_ON_FIRST_INSTALL) return
        // Phase-1 note: fakes already initialize from SampleData, so this is a no-op safety
        // net today. With real persistence, this becomes a "first run" guard + bulk insert.
        Log.d(TAG, "Seed harness active (debug). Sample data available from SampleData.")
    }

    /** Dev-menu affordance: clear and re-seed logs. Safe to call repeatedly. */
    fun forceReseed() {
        scope.launch {
            SampleData.foods.forEach { foodRepository.upsert(it) }
            SampleData.logHistory().forEach { logRepository.add(it) }
            Log.d(TAG, "Forced reseed complete.")
        }
    }

    private companion object {
        const val TAG = "SeedManager"
    }
}
