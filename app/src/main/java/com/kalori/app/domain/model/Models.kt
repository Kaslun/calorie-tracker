package com.kalori.app.domain.model

import java.time.Instant
import java.time.LocalDate

/**
 * Plain-Kotlin domain models. Mirror the data model in CLAUDE_CODE_HANDOFF.md.
 *
 * IMPORTANT seams / settled product decisions encoded here:
 *  - [Nutrition] uses NULLABLE nutrient fields: null means UNKNOWN and is excluded from
 *    sums; it is NOT the same as 0.0. Do not default these to 0.0 anywhere.
 *  - [LogEntry.nutrition] is a FROZEN, resolved snapshot of what was consumed. Editing a
 *    Food later must never retroactively mutate historical log entries.
 *  - [LogEntry.date] (the day it counts toward) is distinct from [LogEntry.timestamp]
 *    (when it was consumed/logged) to support the end-of-day catch-up flow.
 *
 * The data-layer agent owns the Room @Entity versions. These domain types exist so the
 * repository interfaces, fakes, and previews compile and render with no DB or network.
 */

/** Embedded value object — same shape on Food, resolved MealComponent, and LogEntry. */
data class Nutrition(
    val kcal: Double,
    val protein: Double? = null,
    val carbs: Double? = null,
    val sugars: Double? = null,
    val fat: Double? = null,
    val saturatedFat: Double? = null,
    val fiber: Double? = null,
    val salt: Double? = null,
    /** Sparse map; null/absent entry = unknown. Stored as a JSON column by data-layer. */
    val micros: Map<Micronutrient, Double>? = null,
)

data class Portion(
    val label: String,
    val grams: Double? = null,
    val units: Double? = null,
)

data class Food(
    val id: String,
    val name: String,
    val brand: String? = null,
    val barcode: String? = null,
    val source: FoodSource,
    val sourceRef: String? = null,
    val basis: NutritionBasis,
    val nutrition: Nutrition,
    val defaultPortion: Portion? = null,
    val isFavorite: Boolean = false,
    val createdAt: Instant,
    val lastUsedAt: Instant? = null,
    val useCount: Int = 0,
    val isArchived: Boolean = false,
)

/** All known portions for a food: package serving, custom, visual references. */
data class FoodPortion(
    val id: String,
    val foodId: String,
    val portion: Portion,
    val isPackageDefault: Boolean = false,
)

data class Meal(
    val id: String,
    val name: String,
    val isFavorite: Boolean = false,
    val lastUsedAt: Instant? = null,
    val useCount: Int = 0,
    val createdAt: Instant,
)

data class MealComponent(
    val id: String,
    val mealId: String,
    val foodId: String,
    val portion: Portion,
)

data class LogEntry(
    val id: String,
    /** Day this entry counts toward. Distinct from [timestamp]. */
    val date: LocalDate,
    /** When the item was consumed/logged. Distinct from [date]. */
    val timestamp: Instant,
    val mealSlot: MealSlot,
    /** null for quick-add. */
    val foodId: String? = null,
    /** Set if expanded from a Meal. */
    val sourceMealId: String? = null,
    val portion: Portion? = null,
    /** RESOLVED, frozen snapshot. Source of truth for all sums/trends/projections. */
    val nutrition: Nutrition,
    val confidence: Confidence,
    val photoPath: String? = null,
    val note: String? = null,
    /** clientRecordId written to Health Connect; enables update/delete reconciliation. */
    val healthConnectId: String? = null,
)

/** Cache only; Health Connect is the source of truth for weight. */
data class WeightPoint(
    val date: LocalDate,
    val kg: Double,
    val source: String,
)

data class UserGoal(
    val id: Int = 1,
    val sex: Sex,
    val heightCm: Double,
    val birthDate: LocalDate,
    val goalWeightKg: Double,
    /** 0.25 / 0.5 / 0.75 kg per week. */
    val paceKgPerWeek: Double,
    val proteinTargetG: Double,
    val proteinTargetMode: TargetMode,
    val acceptableRangeKcal: Int = 100,
    val createdAt: Instant,
)

/** Append-only history. */
data class TdeeCalibration(
    val id: String,
    val effectiveDate: LocalDate,
    val tdee: Double,
    val bmr: Double,
    val basis: CalibrationBasis,
    val expectedDeltaKg: Double? = null,
    val actualDeltaKg: Double? = null,
    val confirmedByUser: Boolean,
)
