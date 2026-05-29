package com.kalori.app

import android.app.Application
import com.kalori.app.core.seed.SeedManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point and Hilt graph root.
 */
@HiltAndroidApp
class KaloriApplication : Application() {

    @Inject lateinit var seedManager: SeedManager

    override fun onCreate() {
        super.onCreate()
        // Debug-only: pre-populate Norwegian foods + weeks of logs on first install.
        // No-op in release (gated on BuildConfig.SEED_ON_FIRST_INSTALL).
        seedManager.seedIfNeeded()
    }
}
