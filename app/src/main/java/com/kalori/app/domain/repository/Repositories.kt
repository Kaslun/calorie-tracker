package com.kalori.app.domain.repository

import com.kalori.app.domain.model.Food
import com.kalori.app.domain.model.LogEntry
import com.kalori.app.domain.model.Meal
import com.kalori.app.domain.model.TdeeCalibration
import com.kalori.app.domain.model.UserGoal
import com.kalori.app.domain.model.WeightPoint
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interfaces. The data-layer agent provides the Room-backed implementations;
 * [com.kalori.app.data.fake] provides in-memory fakes so every screen and @Preview renders
 * with no DB or network.
 *
 * These signatures are an initial, reasonable surface for Phase 1 scaffolding. data-layer
 * and feature-dev may extend them as real features land — treat them as a starting seam,
 * not a frozen contract.
 */

interface FoodRepository {
    fun observeRecents(limit: Int = 20): Flow<List<Food>>
    fun observeFavorites(): Flow<List<Food>>
    suspend fun getById(id: String): Food?
    suspend fun findByBarcode(barcode: String): Food?
    suspend fun search(query: String): List<Food>
    suspend fun upsert(food: Food)
    suspend fun setFavorite(id: String, favorite: Boolean)
}

interface LogRepository {
    fun observeEntriesForDate(date: LocalDate): Flow<List<LogEntry>>
    suspend fun add(entry: LogEntry)
    suspend fun update(entry: LogEntry)
    suspend fun delete(id: String)
    suspend fun entriesForRange(start: LocalDate, end: LocalDate): List<LogEntry>
}

interface WeightRepository {
    fun observeRecent(days: Int = 30): Flow<List<WeightPoint>>
    suspend fun latest(): WeightPoint?
    suspend fun upsert(point: WeightPoint)
}

interface GoalRepository {
    fun observeGoal(): Flow<UserGoal?>
    suspend fun getGoal(): UserGoal?
    suspend fun setGoal(goal: UserGoal)

    fun observeCalibrations(): Flow<List<TdeeCalibration>>
    /** Most recent calibration with effectiveDate <= [date]; basis for effective TDEE. */
    suspend fun effectiveCalibration(date: LocalDate): TdeeCalibration?
    suspend fun addCalibration(calibration: TdeeCalibration)
}

interface MealRepository {
    fun observeFavorites(): Flow<List<Meal>>
    suspend fun getById(id: String): Meal?
    suspend fun upsert(meal: Meal)
}
