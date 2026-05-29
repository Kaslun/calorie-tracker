package com.kalori.app.domain.model

/**
 * Domain enums for Kalori.
 *
 * These are the plain-Kotlin domain types referenced by repository interfaces, fakes,
 * ViewModels, and previews. The data-layer agent owns the Room @Entity representations;
 * if persistence needs differ, data-layer maps to/from these or annotates them in place.
 * Keep these values verbatim with the data model in CLAUDE_CODE_HANDOFF.md.
 */

enum class Micronutrient {
    VITAMIN_A, VITAMIN_D, VITAMIN_E, VITAMIN_K,
    VITAMIN_B1, VITAMIN_B2, VITAMIN_B3, VITAMIN_B6, VITAMIN_B9, VITAMIN_B12, VITAMIN_C,
    CALCIUM, IRON, MAGNESIUM, PHOSPHORUS, POTASSIUM,
    SODIUM, ZINC, SELENIUM, IODINE, COPPER,
}

enum class FoodSource { OFF, MATVARETABELLEN, MANUAL, RESTAURANT }

enum class NutritionBasis { PER_100G, PER_UNIT }

enum class MealSlot { FROKOST, LUNSJ, MELLOMMALTID, MIDDAG, KVELDSMAT }

enum class Confidence { HIGH, MEDIUM, LOW }

/** Metabolic input for BMR, not gender. */
enum class Sex { MALE, FEMALE }

enum class TargetMode { AUTO, MANUAL }

enum class CalibrationBasis { INITIAL_ESTIMATE, ACTUAL_PROGRESS }
