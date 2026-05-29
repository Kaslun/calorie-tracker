package com.kalori.app.data.repository

import com.kalori.app.data.local.dao.MealDao
import com.kalori.app.data.local.entity.toDomain
import com.kalori.app.data.local.entity.toEntity
import com.kalori.app.domain.model.Meal
import com.kalori.app.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed [MealRepository]. Meal components live in their own table; the interface
 * currently exposes only meal-level reads/writes — component expansion is wired when the
 * meals feature lands (see report note on the interface surface).
 */
@Singleton
class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
) : MealRepository {

    override fun observeFavorites(): Flow<List<Meal>> =
        mealDao.observeFavorites().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Meal? = mealDao.getById(id)?.toDomain()

    override suspend fun upsert(meal: Meal) = mealDao.upsert(meal.toEntity())
}
