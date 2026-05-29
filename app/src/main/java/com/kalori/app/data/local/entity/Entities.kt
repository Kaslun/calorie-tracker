package com.kalori.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kalori.app.domain.model.CalibrationBasis
import com.kalori.app.domain.model.Confidence
import com.kalori.app.domain.model.FoodSource
import com.kalori.app.domain.model.MealSlot
import com.kalori.app.domain.model.NutritionBasis
import com.kalori.app.domain.model.Sex
import com.kalori.app.domain.model.TargetMode
import java.time.Instant
import java.time.LocalDate

/**
 * Room @Entity definitions — schema version 1.
 *
 * Persistence is deliberately kept separate from the domain models in
 * [com.kalori.app.domain.model]; the mappers in EntityMappers.kt bridge the two so no screen
 * or repository surface depends on Room.
 *
 * Conventions:
 *  - Nutrition is @Embedded with NO prefix where it appears once (its macro columns are the
 *    aggregate targets), and `@Embedded(prefix = "...")` where a second Portion/Nutrition would
 *    collide on the same row.
 *  - Portion uses prefixes to avoid colliding with Nutrition's own `grams`-free columns and to
 *    disambiguate multiple portions on one entity.
 */

@Entity(
    tableName = "foods",
    indices = [
        Index("barcode"),
        Index("lastUsedAt"),
        Index("isFavorite"),
        Index("name"),
    ],
)
data class FoodEntity(
    @PrimaryKey val id: String,
    val name: String,
    val brand: String?,
    val barcode: String?,
    val source: FoodSource,
    val sourceRef: String?,
    val basis: NutritionBasis,
    // Food's own per-basis nutrition. No prefix: these macro columns are the canonical names.
    @Embedded val nutrition: NutritionEmbedded,
    // Default portion. Prefixed to avoid colliding with any other portion and with nutrition.
    @Embedded(prefix = "def_") val defaultPortion: PortionEmbedded?,
    val isFavorite: Boolean = false,
    val createdAt: Instant,
    val lastUsedAt: Instant?,
    val useCount: Int = 0,
    val isArchived: Boolean = false,
)

@Entity(
    tableName = "food_portions",
    indices = [Index("foodId")],
)
data class FoodPortionEntity(
    @PrimaryKey val id: String,
    val foodId: String,
    @Embedded(prefix = "p_") val portion: PortionEmbedded,
    val isPackageDefault: Boolean = false,
)

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isFavorite: Boolean = false,
    val lastUsedAt: Instant?,
    val useCount: Int = 0,
    val createdAt: Instant,
)

@Entity(
    tableName = "meal_components",
    indices = [Index("mealId"), Index("foodId")],
)
data class MealComponentEntity(
    @PrimaryKey val id: String,
    val mealId: String,
    val foodId: String,
    @Embedded(prefix = "p_") val portion: PortionEmbedded,
)

@Entity(
    tableName = "log_entries",
    indices = [Index("date"), Index("foodId"), Index("sourceMealId")],
)
data class LogEntryEntity(
    @PrimaryKey val id: String,
    /** Day this entry counts toward. Queried for daily totals; distinct from [timestamp]. */
    val date: LocalDate,
    /** When the item was consumed/logged. Distinct from [date]. */
    val timestamp: Instant,
    val mealSlot: MealSlot,
    val foodId: String?,
    val sourceMealId: String?,
    // The portion actually logged. Prefixed; nullable for quick-add.
    @Embedded(prefix = "p_") val portion: PortionEmbedded?,
    // RESOLVED, FROZEN snapshot. No prefix: macro columns aggregate via SUM(...) per date.
    @Embedded val nutrition: NutritionEmbedded,
    val confidence: Confidence,
    val photoPath: String?,
    val note: String?,
    val healthConnectId: String?,
)

@Entity(tableName = "weight_points")
data class WeightPointEntity(
    @PrimaryKey val date: LocalDate,
    val kg: Double,
    val source: String,
)

@Entity(tableName = "user_goal")
data class UserGoalEntity(
    @PrimaryKey val id: Int = 1,
    val sex: Sex,
    val heightCm: Double,
    val birthDate: LocalDate,
    val goalWeightKg: Double,
    val paceKgPerWeek: Double,
    val proteinTargetG: Double,
    val proteinTargetMode: TargetMode,
    val acceptableRangeKcal: Int = 100,
    val createdAt: Instant,
)

@Entity(
    tableName = "tdee_calibrations",
    indices = [Index("effectiveDate")],
)
data class TdeeCalibrationEntity(
    @PrimaryKey val id: String,
    val effectiveDate: LocalDate,
    val tdee: Double,
    val bmr: Double,
    val basis: CalibrationBasis,
    val expectedDeltaKg: Double?,
    val actualDeltaKg: Double?,
    val confirmedByUser: Boolean,
)
