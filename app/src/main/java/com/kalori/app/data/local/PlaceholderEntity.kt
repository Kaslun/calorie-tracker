package com.kalori.app.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query

/**
 * PLACEHOLDER — DELETE WHEN data-layer LANDS THE REAL ENTITIES.
 *
 * Room's @Database requires at least one entity + DAO to compile. This placeholder exists
 * solely so the database class, TypeConverter registration, and Hilt provisioning are real,
 * wired seams that compile in Phase 1. The data-layer agent owns the data model: it should
 * replace this with the real entities (Food, FoodPortion, Meal, MealComponent, LogEntry,
 * WeightPoint, UserGoal, TdeeCalibration) and DAOs from CLAUDE_CODE_HANDOFF.md, then remove
 * this file and its entry in [KaloriDatabase].
 */
@Entity(tableName = "_scaffold_placeholder")
data class PlaceholderEntity(
    @PrimaryKey val id: Long = 0,
)

@Dao
interface PlaceholderDao {
    @Query("SELECT COUNT(*) FROM _scaffold_placeholder")
    suspend fun count(): Int
}
