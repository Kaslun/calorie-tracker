package com.kalori.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.kalori.app.data.local.entity.FoodEntity
import com.kalori.app.data.local.entity.FoodPortionEntity
import com.kalori.app.data.local.entity.LogEntryEntity
import com.kalori.app.data.local.entity.MealComponentEntity
import com.kalori.app.data.local.entity.MealEntity
import com.kalori.app.data.local.entity.TdeeCalibrationEntity
import com.kalori.app.data.local.entity.UserGoalEntity
import com.kalori.app.data.local.entity.WeightPointEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface FoodDao {

    @Query("SELECT * FROM foods WHERE isArchived = 0 ORDER BY lastUsedAt DESC LIMIT :limit")
    fun observeRecents(limit: Int): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE isFavorite = 1 AND isArchived = 0 ORDER BY lastUsedAt DESC")
    fun observeFavorites(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE id = :id")
    suspend fun getById(id: String): FoodEntity?

    @Query("SELECT * FROM foods WHERE barcode = :barcode LIMIT 1")
    suspend fun findByBarcode(barcode: String): FoodEntity?

    @Query(
        """
        SELECT * FROM foods
        WHERE isArchived = 0 AND name LIKE '%' || :query || '%'
        ORDER BY useCount DESC, lastUsedAt DESC
        LIMIT :limit
        """,
    )
    suspend fun search(query: String, limit: Int = 50): List<FoodEntity>

    @Upsert
    suspend fun upsert(food: FoodEntity)

    @Upsert
    suspend fun upsertAll(foods: List<FoodEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(foods: List<FoodEntity>): List<Long>

    @Query("UPDATE foods SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: String, favorite: Boolean)

    @Query("SELECT COUNT(*) FROM foods WHERE source = :source")
    suspend fun countBySource(source: String): Int
}

@Dao
interface FoodPortionDao {

    @Query("SELECT * FROM food_portions WHERE foodId = :foodId")
    suspend fun forFood(foodId: String): List<FoodPortionEntity>

    @Upsert
    suspend fun upsert(portion: FoodPortionEntity)
}

@Dao
interface MealDao {

    @Query("SELECT * FROM meals WHERE isFavorite = 1 ORDER BY lastUsedAt DESC")
    fun observeFavorites(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getById(id: String): MealEntity?

    @Upsert
    suspend fun upsert(meal: MealEntity)

    @Query("SELECT * FROM meal_components WHERE mealId = :mealId")
    suspend fun componentsFor(mealId: String): List<MealComponentEntity>

    @Upsert
    suspend fun upsertComponent(component: MealComponentEntity)
}

@Dao
interface LogEntryDao {

    @Query("SELECT * FROM log_entries WHERE date = :date ORDER BY timestamp ASC")
    fun observeForDate(date: LocalDate): Flow<List<LogEntryEntity>>

    @Query(
        "SELECT * FROM log_entries WHERE date BETWEEN :start AND :end ORDER BY date ASC, timestamp ASC",
    )
    suspend fun forRange(start: LocalDate, end: LocalDate): List<LogEntryEntity>

    @Query("SELECT * FROM log_entries WHERE id = :id")
    suspend fun getById(id: String): LogEntryEntity?

    @Upsert
    suspend fun upsert(entry: LogEntryEntity)

    @Query("DELETE FROM log_entries WHERE id = :id")
    suspend fun delete(id: String)

    /**
     * Daily per-nutrient totals for a [date], honoring the null-exclusion rule.
     *
     * SQL `SUM(x)` ignores NULL natively — we deliberately do NOT `COALESCE(x, 0)` inside the
     * sum, so unknown nutrients are excluded rather than treated as 0.0. Alongside each sum we
     * keep `COUNT(x)` (counts only non-null rows) as the contributing-item count, plus the
     * overall `COUNT(*)` so the UI can say "Vitamin D: 12 µg from 6 of 14 logged items".
     *
     * A nutrient with no contributing rows comes back with a NULL sum (UNKNOWN), not 0.0.
     * Micros live in the JSON column and are aggregated in Kotlin (see LogRepository).
     */
    @Query(
        """
        SELECT
            COUNT(*)              AS entryCount,
            SUM(kcal)             AS kcalSum,
            SUM(protein)          AS proteinSum,       COUNT(protein)      AS proteinCount,
            SUM(carbs)            AS carbsSum,          COUNT(carbs)        AS carbsCount,
            SUM(sugars)           AS sugarsSum,         COUNT(sugars)       AS sugarsCount,
            SUM(fat)              AS fatSum,            COUNT(fat)          AS fatCount,
            SUM(saturatedFat)     AS saturatedFatSum,   COUNT(saturatedFat) AS saturatedFatCount,
            SUM(fiber)            AS fiberSum,          COUNT(fiber)        AS fiberCount,
            SUM(salt)             AS saltSum,           COUNT(salt)         AS saltCount
        FROM log_entries
        WHERE date = :date
        """,
    )
    suspend fun dailyMacroTotals(date: LocalDate): DailyMacroTotalsRow?

    /** The micros JSON column for every entry on a [date]; micro aggregation happens in Kotlin. */
    @Query("SELECT micros FROM log_entries WHERE date = :date")
    suspend fun microsJsonForDate(date: LocalDate): List<String?>
}

/**
 * Raw aggregate row from [LogEntryDao.dailyMacroTotals]. Sums are nullable: a NULL sum means
 * no entry on the day carried that nutrient (UNKNOWN), never 0.0. Counts are the
 * contributing-item counts for the honesty display.
 */
data class DailyMacroTotalsRow(
    val entryCount: Int,
    val kcalSum: Double?,
    val proteinSum: Double?, val proteinCount: Int,
    val carbsSum: Double?, val carbsCount: Int,
    val sugarsSum: Double?, val sugarsCount: Int,
    val fatSum: Double?, val fatCount: Int,
    val saturatedFatSum: Double?, val saturatedFatCount: Int,
    val fiberSum: Double?, val fiberCount: Int,
    val saltSum: Double?, val saltCount: Int,
)

@Dao
interface WeightPointDao {

    @Query("SELECT * FROM weight_points ORDER BY date DESC LIMIT :days")
    fun observeRecent(days: Int): Flow<List<WeightPointEntity>>

    @Query("SELECT * FROM weight_points ORDER BY date DESC LIMIT 1")
    suspend fun latest(): WeightPointEntity?

    @Upsert
    suspend fun upsert(point: WeightPointEntity)
}

@Dao
interface GoalDao {

    @Query("SELECT * FROM user_goal WHERE id = 1")
    fun observeGoal(): Flow<UserGoalEntity?>

    @Query("SELECT * FROM user_goal WHERE id = 1")
    suspend fun getGoal(): UserGoalEntity?

    @Upsert
    suspend fun setGoal(goal: UserGoalEntity)

    @Query("SELECT * FROM tdee_calibrations ORDER BY effectiveDate DESC")
    fun observeCalibrations(): Flow<List<TdeeCalibrationEntity>>

    @Query(
        "SELECT * FROM tdee_calibrations WHERE effectiveDate <= :date ORDER BY effectiveDate DESC LIMIT 1",
    )
    suspend fun effectiveCalibration(date: LocalDate): TdeeCalibrationEntity?

    @Upsert
    suspend fun addCalibration(calibration: TdeeCalibrationEntity)
}
