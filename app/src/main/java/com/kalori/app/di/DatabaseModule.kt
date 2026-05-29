package com.kalori.app.di

import android.content.Context
import androidx.room.Room
import com.kalori.app.data.local.KaloriDatabase
import com.kalori.app.data.local.PlaceholderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides the Room database and DAOs.
 *
 * SEAM FOR data-layer: the DB provision is real and wired. As real DAOs are added to
 * [KaloriDatabase], add matching @Provides here. Keep DAO provisioning in this module so
 * repository implementations can constructor-inject them.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KaloriDatabase =
        Room.databaseBuilder(context, KaloriDatabase::class.java, "kalori.db")
            // data-layer: add .addMigrations(...) and remove fallback before any real data ships.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePlaceholderDao(db: KaloriDatabase): PlaceholderDao = db.placeholderDao()
}
