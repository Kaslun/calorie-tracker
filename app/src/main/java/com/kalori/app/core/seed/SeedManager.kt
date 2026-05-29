package com.kalori.app.core.seed

import android.util.Log
import com.kalori.app.BuildConfig
import com.kalori.app.data.fake.SampleData
import com.kalori.app.data.matvaretabellen.MatvaretabellenImporter
import com.kalori.app.data.seed.RestaurantSeed
import com.kalori.app.domain.repository.FoodRepository
import com.kalori.app.domain.repository.LogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds the Room database.
 *
 * Two distinct concerns:
 *  - FOOD BACKBONE (both flavors): the Matvaretabellen micronutrient table and the Norwegian
 *    restaurant-chain foods are real reference data the app needs to function. These import
 *    idempotently on first run regardless of build type.
 *  - DEV SAMPLE LOGS (debug only): weeks of fake logs so design iteration never requires
 *    manually logging "havregryn" 50 times. Gated on BuildConfig.SEED_ON_FIRST_INSTALL;
 *    release never seeds logs.
 *
 * All operations are idempotent (stable ids + IGNORE-on-conflict), so repeated calls are safe.
 */
@Singleton
class SeedManager @Inject constructor(
    private val foodRepository: FoodRepository,
    private val logRepository: LogRepository,
    private val matvaretabellenImporter: MatvaretabellenImporter,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun seedIfNeeded() {
        scope.launch {
            // Food backbone — both flavors.
            val imported = matvaretabellenImporter.importIfNeeded()
            RestaurantSeed.foods().forEach { foodRepository.upsert(it) }
            Log.d(TAG, "Food backbone ready (matvaretabellen inserted=$imported, restaurants seeded).")

            // Dev sample logs — debug only.
            if (BuildConfig.SEED_ON_FIRST_INSTALL) {
                SampleData.logHistory().forEach { logRepository.add(it) }
                Log.d(TAG, "Debug sample logs seeded.")
            }
        }
    }

    /** Dev-menu affordance: re-run the food import and re-seed sample logs. Safe to repeat. */
    fun forceReseed() {
        scope.launch {
            matvaretabellenImporter.importNow()
            RestaurantSeed.foods().forEach { foodRepository.upsert(it) }
            SampleData.foods.forEach { foodRepository.upsert(it) }
            SampleData.logHistory().forEach { logRepository.add(it) }
            Log.d(TAG, "Forced reseed complete.")
        }
    }

    private companion object {
        const val TAG = "SeedManager"
    }
}
