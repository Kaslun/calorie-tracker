package com.kalori.app.domain.nutrition

import com.kalori.app.domain.model.Food
import com.kalori.app.domain.model.Micronutrient
import com.kalori.app.domain.model.Nutrition
import com.kalori.app.domain.model.NutritionBasis
import com.kalori.app.domain.model.Portion

/**
 * Pure, dependency-free resolution of a [Food]'s per-basis nutrition + a [Portion] into a
 * FROZEN [Nutrition] snapshot.
 *
 * This is intentionally free of Room/Android imports so it is trivially unit-testable and so
 * the result can be stored verbatim on a LogEntry. The product rules it enforces:
 *
 *  - PER_100G  → value × grams / 100
 *  - PER_UNIT  → value × units
 *  - A `null` nutrient stays `null` through resolution. It is NEVER coerced to `0.0`; null
 *    means UNKNOWN and must be excluded from downstream sums.
 *  - The returned snapshot is the source of truth for the log entry. Editing the [Food] later
 *    must never recompute or mutate a snapshot already taken — callers store this value once.
 */
object NutritionResolver {

    /**
     * Resolves [food]'s nutrition for the given [portion].
     *
     * The [Portion] supplies the multiplier:
     *  - For PER_100G foods, [Portion.grams] is required (multiplier = grams / 100).
     *  - For PER_UNIT foods, [Portion.units] is required (multiplier = units).
     *
     * If the portion is missing the field its basis needs, this falls back to the food's
     * [Food.defaultPortion]; if that is also missing, it falls back to a 1:1 multiplier
     * (100g / 1 unit) so resolution never throws into the logging path.
     */
    fun resolve(food: Food, portion: Portion?): Nutrition =
        scale(food.nutrition, multiplierFor(food.basis, portion, food.defaultPortion))

    /** Resolves directly from a base [nutrition] + [basis] + [portion]; used by meal expansion. */
    fun resolve(
        nutrition: Nutrition,
        basis: NutritionBasis,
        portion: Portion?,
        defaultPortion: Portion? = null,
    ): Nutrition = scale(nutrition, multiplierFor(basis, portion, defaultPortion))

    /**
     * The portion multiplier for a basis.
     *  - PER_100G: grams / 100.0
     *  - PER_UNIT: units
     */
    fun multiplierFor(
        basis: NutritionBasis,
        portion: Portion?,
        defaultPortion: Portion? = null,
    ): Double = when (basis) {
        NutritionBasis.PER_100G -> {
            val grams = portion?.grams ?: defaultPortion?.grams ?: 100.0
            grams / 100.0
        }
        NutritionBasis.PER_UNIT -> {
            portion?.units ?: defaultPortion?.units ?: 1.0
        }
    }

    /**
     * Scales every nutrient by [factor]. Null nutrients (unknown) stay null — they are never
     * turned into 0.0. Micros are scaled value-by-value, preserving sparseness.
     */
    fun scale(n: Nutrition, factor: Double): Nutrition = Nutrition(
        kcal = n.kcal * factor,
        protein = n.protein?.times(factor),
        carbs = n.carbs?.times(factor),
        sugars = n.sugars?.times(factor),
        fat = n.fat?.times(factor),
        saturatedFat = n.saturatedFat?.times(factor),
        fiber = n.fiber?.times(factor),
        salt = n.salt?.times(factor),
        micros = n.micros?.mapValues { (_, v) -> v * factor },
    )
}

/**
 * Per-nutrient daily total honoring the null-exclusion rule.
 *
 * `sum` is the SUM of the contributing (non-null) values. `contributing` is how many log
 * entries actually carried a value for this nutrient; `total` is how many entries were
 * considered. This powers the micronutrient honesty display:
 * "Vitamin D: 12 µg from 6 of 14 logged items."
 *
 * A nutrient with zero contributing items has `sum == null` (UNKNOWN), distinct from `0.0`.
 */
data class NutrientTotal(
    val sum: Double?,
    val contributing: Int,
    val total: Int,
)

/**
 * Aggregates a list of [Nutrition] snapshots into per-nutrient totals, excluding nulls and
 * retaining contributing-item counts. Pure; mirrors the SQL aggregate the DAO performs so
 * the same honesty contract holds whether totals come from Room or in-memory.
 */
object NutritionAggregator {

    /** kcal is non-null by model contract, so its sum is always present. */
    fun aggregate(snapshots: List<Nutrition>): DailyNutritionTotals {
        val total = snapshots.size
        fun field(selector: (Nutrition) -> Double?): NutrientTotal {
            val values = snapshots.mapNotNull(selector)
            return NutrientTotal(
                sum = if (values.isEmpty()) null else values.sum(),
                contributing = values.size,
                total = total,
            )
        }

        val micros: Map<Micronutrient, NutrientTotal> =
            Micronutrient.entries.associateWith { micro ->
                val values = snapshots.mapNotNull { it.micros?.get(micro) }
                NutrientTotal(
                    sum = if (values.isEmpty()) null else values.sum(),
                    contributing = values.size,
                    total = total,
                )
            }.filterValues { it.contributing > 0 }

        return DailyNutritionTotals(
            kcal = snapshots.sumOf { it.kcal },
            entryCount = total,
            protein = field { it.protein },
            carbs = field { it.carbs },
            sugars = field { it.sugars },
            fat = field { it.fat },
            saturatedFat = field { it.saturatedFat },
            fiber = field { it.fiber },
            salt = field { it.salt },
            micros = micros,
        )
    }
}

/** Aggregated, null-honest daily totals. kcal is always known (model contract). */
data class DailyNutritionTotals(
    val kcal: Double,
    val entryCount: Int,
    val protein: NutrientTotal,
    val carbs: NutrientTotal,
    val sugars: NutrientTotal,
    val fat: NutrientTotal,
    val saturatedFat: NutrientTotal,
    val fiber: NutrientTotal,
    val salt: NutrientTotal,
    val micros: Map<Micronutrient, NutrientTotal>,
)
