package com.kalori.app.data.fake

import com.kalori.app.domain.model.CalibrationBasis
import com.kalori.app.domain.model.Food
import com.kalori.app.domain.model.LogEntry
import com.kalori.app.domain.model.Meal
import com.kalori.app.domain.model.TdeeCalibration
import com.kalori.app.domain.model.UserGoal
import com.kalori.app.domain.model.WeightPoint
import com.kalori.app.domain.repository.FoodRepository
import com.kalori.app.domain.repository.GoalRepository
import com.kalori.app.domain.repository.LogRepository
import com.kalori.app.domain.repository.MealRepository
import com.kalori.app.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * In-memory fakes backing every screen and @Preview with no DB or network.
 *
 * These are the canonical fakes both for Compose previews and for the debug build (until
 * data-layer's Room-backed repos land, AppModule binds these). They hold state in
 * MutableStateFlow so writes are observable. The data-layer agent replaces the DI bindings,
 * not these classes — keep them as the preview/test backbone.
 */

class FakeFoodRepository(
    initial: List<Food> = SampleData.foods,
) : FoodRepository {
    private val state = MutableStateFlow(initial)

    override fun observeRecents(limit: Int): Flow<List<Food>> =
        state.map { foods -> foods.sortedByDescending { it.lastUsedAt }.take(limit) }

    override fun observeFavorites(): Flow<List<Food>> =
        state.map { foods -> foods.filter { it.isFavorite } }

    override suspend fun getById(id: String): Food? = state.value.firstOrNull { it.id == id }

    override suspend fun findByBarcode(barcode: String): Food? =
        state.value.firstOrNull { it.barcode == barcode }

    override suspend fun search(query: String): List<Food> =
        state.value.filter { it.name.contains(query, ignoreCase = true) }

    override suspend fun upsert(food: Food) {
        state.value = state.value.filterNot { it.id == food.id } + food
    }

    override suspend fun setFavorite(id: String, favorite: Boolean) {
        state.value = state.value.map { if (it.id == id) it.copy(isFavorite = favorite) else it }
    }
}

class FakeLogRepository(
    initial: List<LogEntry> = SampleData.logHistory(),
) : LogRepository {
    private val state = MutableStateFlow(initial)

    override fun observeEntriesForDate(date: LocalDate): Flow<List<LogEntry>> =
        state.map { entries -> entries.filter { it.date == date } }

    override suspend fun add(entry: LogEntry) {
        state.value = state.value + entry
    }

    override suspend fun update(entry: LogEntry) {
        state.value = state.value.map { if (it.id == entry.id) entry else it }
    }

    override suspend fun delete(id: String) {
        state.value = state.value.filterNot { it.id == id }
    }

    override suspend fun entriesForRange(start: LocalDate, end: LocalDate): List<LogEntry> =
        state.value.filter { !it.date.isBefore(start) && !it.date.isAfter(end) }
}

class FakeWeightRepository(
    initial: List<WeightPoint> = SampleData.weightHistory(),
) : WeightRepository {
    private val state = MutableStateFlow(initial)

    override fun observeRecent(days: Int): Flow<List<WeightPoint>> =
        state.map { points -> points.sortedBy { it.date }.takeLast(days) }

    override suspend fun latest(): WeightPoint? = state.value.maxByOrNull { it.date }

    override suspend fun upsert(point: WeightPoint) {
        state.value = state.value.filterNot { it.date == point.date } + point
    }
}

class FakeGoalRepository(
    initialGoal: UserGoal? = SampleData.goal,
) : GoalRepository {
    private val goalState = MutableStateFlow(initialGoal)
    private val calibrations = MutableStateFlow(
        listOf(
            TdeeCalibration(
                id = "cal-0",
                effectiveDate = LocalDate.of(2026, 5, 1),
                tdee = 2650.0,
                bmr = 1850.0,
                basis = CalibrationBasis.INITIAL_ESTIMATE,
                confirmedByUser = true,
            ),
        ),
    )

    override fun observeGoal(): Flow<UserGoal?> = goalState

    override suspend fun getGoal(): UserGoal? = goalState.value

    override suspend fun setGoal(goal: UserGoal) {
        goalState.value = goal
    }

    override fun observeCalibrations(): Flow<List<TdeeCalibration>> = calibrations

    override suspend fun effectiveCalibration(date: LocalDate): TdeeCalibration? =
        calibrations.value
            .filter { !it.effectiveDate.isAfter(date) }
            .maxByOrNull { it.effectiveDate }

    override suspend fun addCalibration(calibration: TdeeCalibration) {
        calibrations.value = calibrations.value + calibration
    }
}

class FakeMealRepository(
    initial: List<Meal> = emptyList(),
) : MealRepository {
    private val state = MutableStateFlow(initial)

    override fun observeFavorites(): Flow<List<Meal>> =
        state.map { meals -> meals.filter { it.isFavorite } }

    override suspend fun getById(id: String): Meal? = state.value.firstOrNull { it.id == id }

    override suspend fun upsert(meal: Meal) {
        state.value = state.value.filterNot { it.id == meal.id } + meal
    }
}
