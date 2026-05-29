package com.kalori.app.di

import com.kalori.app.data.repository.FoodRepositoryImpl
import com.kalori.app.data.repository.GoalRepositoryImpl
import com.kalori.app.data.repository.LogRepositoryImpl
import com.kalori.app.data.repository.MealRepositoryImpl
import com.kalori.app.data.repository.WeightRepositoryImpl
import com.kalori.app.domain.repository.FoodRepository
import com.kalori.app.domain.repository.GoalRepository
import com.kalori.app.domain.repository.LogRepository
import com.kalori.app.domain.repository.MealRepository
import com.kalori.app.domain.repository.WeightRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds repository interfaces to their Room-backed implementations.
 *
 * The in-memory fakes in [com.kalori.app.data.fake] remain in the codebase as the preview/test
 * backbone (every @Preview and unit test constructs them directly); they are simply no longer
 * the production binding. The interface surface in [com.kalori.app.domain.repository] is
 * unchanged, so feature-dev is unaffected.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFoodRepository(impl: FoodRepositoryImpl): FoodRepository

    @Binds
    @Singleton
    abstract fun bindLogRepository(impl: LogRepositoryImpl): LogRepository

    @Binds
    @Singleton
    abstract fun bindWeightRepository(impl: WeightRepositoryImpl): WeightRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository

    @Binds
    @Singleton
    abstract fun bindMealRepository(impl: MealRepositoryImpl): MealRepository
}
