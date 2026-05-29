package com.kalori.app.di

import com.kalori.app.data.fake.FakeFoodRepository
import com.kalori.app.data.fake.FakeGoalRepository
import com.kalori.app.data.fake.FakeLogRepository
import com.kalori.app.data.fake.FakeMealRepository
import com.kalori.app.data.fake.FakeWeightRepository
import com.kalori.app.domain.repository.FoodRepository
import com.kalori.app.domain.repository.GoalRepository
import com.kalori.app.domain.repository.LogRepository
import com.kalori.app.domain.repository.MealRepository
import com.kalori.app.domain.repository.WeightRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds repository interfaces to implementations.
 *
 * SEAM FOR data-layer: Phase 1 binds the in-memory fakes so the whole app runs with no DB
 * or network. When the Room-backed repositories land, swap these @Provides to the real
 * implementations (or @Binds the concrete classes). The interface surface in
 * [com.kalori.app.domain.repository] stays stable so feature-dev is unaffected.
 *
 * Kept as singletons so the fakes' in-memory state is shared across the app (writes from one
 * screen are observable in another) — matters for the dev menu seed/reset affordances.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFoodRepository(): FoodRepository = FakeFoodRepository()

    @Provides
    @Singleton
    fun provideLogRepository(): LogRepository = FakeLogRepository()

    @Provides
    @Singleton
    fun provideWeightRepository(): WeightRepository = FakeWeightRepository()

    @Provides
    @Singleton
    fun provideGoalRepository(): GoalRepository = FakeGoalRepository()

    @Provides
    @Singleton
    fun provideMealRepository(): MealRepository = FakeMealRepository()
}
