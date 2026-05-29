package com.kalori.app.di

import android.content.Context
import androidx.room.Room
import com.kalori.app.BuildConfig
import com.kalori.app.data.local.KaloriDatabase
import com.kalori.app.data.local.dao.FoodDao
import com.kalori.app.data.local.dao.FoodPortionDao
import com.kalori.app.data.local.dao.GoalDao
import com.kalori.app.data.local.dao.LogEntryDao
import com.kalori.app.data.local.dao.MealDao
import com.kalori.app.data.local.dao.WeightPointDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides the Room database and DAOs.
 *
 * MIGRATION DECISION (handoff: preserve the local DB across installs):
 * The blanket `fallbackToDestructiveMigration()` is REMOVED from the normal/release path. The
 * release database opens with NO fallback, so a missing migration fails loudly in development
 * rather than silently wiping the user's logs. Schema version 1 is the first real schema;
 * every future bump MUST ship an explicit `Migration`.
 *
 * A destructive fallback is gated to DEBUG builds ONLY (so iterating on schema during
 * development doesn't require manual DB clears). Release never destroys data.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KaloriDatabase =
        Room.databaseBuilder(context, KaloriDatabase::class.java, "kalori.db")
            // .addMigrations(MIGRATION_1_2, ...) // add explicit migrations as the schema evolves
            .apply {
                if (BuildConfig.DEBUG) {
                    // Debug-only convenience; never on the release path.
                    fallbackToDestructiveMigration()
                }
            }
            .build()

    @Provides fun provideFoodDao(db: KaloriDatabase): FoodDao = db.foodDao()
    @Provides fun provideFoodPortionDao(db: KaloriDatabase): FoodPortionDao = db.foodPortionDao()
    @Provides fun provideMealDao(db: KaloriDatabase): MealDao = db.mealDao()
    @Provides fun provideLogEntryDao(db: KaloriDatabase): LogEntryDao = db.logEntryDao()
    @Provides fun provideWeightPointDao(db: KaloriDatabase): WeightPointDao = db.weightPointDao()
    @Provides fun provideGoalDao(db: KaloriDatabase): GoalDao = db.goalDao()
}
