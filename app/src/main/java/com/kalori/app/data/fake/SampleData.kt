package com.kalori.app.data.fake

import com.kalori.app.domain.model.Confidence
import com.kalori.app.domain.model.Food
import com.kalori.app.domain.model.FoodSource
import com.kalori.app.domain.model.LogEntry
import com.kalori.app.domain.model.MealSlot
import com.kalori.app.domain.model.Nutrition
import com.kalori.app.domain.model.NutritionBasis
import com.kalori.app.domain.model.Portion
import com.kalori.app.domain.model.Sex
import com.kalori.app.domain.model.TargetMode
import com.kalori.app.domain.model.UserGoal
import com.kalori.app.domain.model.WeightPoint
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Realistic Norwegian sample data for fakes, previews, and the debug seed harness.
 *
 * This is a SMALL hand-built sample. The full ~2000-row Matvaretabellen import and the
 * Norwegian restaurant-chain set are the data-layer agent's job (Phase 2). Keep this list
 * small but plausible so screens render with real-looking content.
 */
object SampleData {

    private val now: Instant = Instant.parse("2026-05-29T08:00:00Z")

    val foods: List<Food> = listOf(
        Food(
            id = "food-havregryn",
            name = "Havregryn, lettkokte",
            brand = "Bjørn",
            source = FoodSource.MATVARETABELLEN,
            basis = NutritionBasis.PER_100G,
            nutrition = Nutrition(
                kcal = 370.0, protein = 13.0, carbs = 58.0, sugars = 1.0,
                fat = 7.0, saturatedFat = 1.3, fiber = 10.0, salt = 0.0,
            ),
            defaultPortion = Portion("Porsjon", grams = 60.0),
            isFavorite = true,
            createdAt = now, lastUsedAt = now, useCount = 42,
        ),
        Food(
            id = "food-skummet-melk",
            name = "Lettmelk",
            brand = "Tine",
            source = FoodSource.MATVARETABELLEN,
            basis = NutritionBasis.PER_100G,
            nutrition = Nutrition(
                kcal = 38.0, protein = 3.5, carbs = 4.5, sugars = 4.5,
                fat = 0.5, saturatedFat = 0.3, fiber = null, salt = 0.1,
            ),
            defaultPortion = Portion("Glass", grams = 200.0),
            isFavorite = true,
            createdAt = now, lastUsedAt = now, useCount = 30,
        ),
        Food(
            id = "food-egg",
            name = "Egg, kokt",
            source = FoodSource.MATVARETABELLEN,
            basis = NutritionBasis.PER_UNIT,
            nutrition = Nutrition(
                kcal = 78.0, protein = 6.3, carbs = 0.6, sugars = 0.6,
                fat = 5.3, saturatedFat = 1.6, fiber = null, salt = 0.2,
            ),
            defaultPortion = Portion("Stk", units = 1.0),
            isFavorite = true,
            createdAt = now, lastUsedAt = now, useCount = 55,
        ),
        Food(
            id = "food-kyllingfilet",
            name = "Kyllingfilet, stekt",
            brand = "Prior",
            source = FoodSource.MATVARETABELLEN,
            basis = NutritionBasis.PER_100G,
            nutrition = Nutrition(
                kcal = 165.0, protein = 31.0, carbs = 0.0, sugars = 0.0,
                fat = 3.6, saturatedFat = 1.0, fiber = null, salt = 0.4,
            ),
            defaultPortion = Portion("Filet", grams = 150.0),
            createdAt = now, lastUsedAt = now, useCount = 18,
        ),
        Food(
            id = "food-grovbrod",
            name = "Grovbrød",
            brand = "First Price",
            barcode = "7035620025013",
            source = FoodSource.OFF,
            basis = NutritionBasis.PER_100G,
            nutrition = Nutrition(
                kcal = 245.0, protein = 9.0, carbs = 41.0, sugars = 3.0,
                fat = 4.0, saturatedFat = 0.8, fiber = 7.0, salt = 1.1,
            ),
            defaultPortion = Portion("Skive", grams = 35.0),
            createdAt = now, lastUsedAt = now, useCount = 24,
        ),
        Food(
            id = "food-brunost",
            name = "Brunost (Gudbrandsdalsost)",
            brand = "Tine",
            source = FoodSource.MATVARETABELLEN,
            basis = NutritionBasis.PER_100G,
            nutrition = Nutrition(
                kcal = 466.0, protein = 9.0, carbs = 42.0, sugars = 42.0,
                fat = 29.0, saturatedFat = 19.0, fiber = null, salt = 0.6,
            ),
            defaultPortion = Portion("Skive", grams = 15.0),
            createdAt = now, lastUsedAt = now, useCount = 12,
        ),
        Food(
            id = "food-banan",
            name = "Banan",
            source = FoodSource.MATVARETABELLEN,
            basis = NutritionBasis.PER_UNIT,
            nutrition = Nutrition(
                kcal = 105.0, protein = 1.3, carbs = 27.0, sugars = 14.0,
                fat = 0.3, saturatedFat = 0.1, fiber = 3.1, salt = 0.0,
            ),
            defaultPortion = Portion("Stk", units = 1.0),
            isFavorite = true,
            createdAt = now, lastUsedAt = now, useCount = 33,
        ),
        Food(
            id = "food-bigmac",
            name = "Big Mac",
            brand = "McDonald's",
            source = FoodSource.RESTAURANT,
            basis = NutritionBasis.PER_UNIT,
            nutrition = Nutrition(
                kcal = 503.0, protein = 26.0, carbs = 41.0, sugars = 8.0,
                fat = 26.0, saturatedFat = 9.0, fiber = 3.0, salt = 2.3,
            ),
            defaultPortion = Portion("Stk", units = 1.0),
            createdAt = now, lastUsedAt = now, useCount = 3,
        ),
    )

    val goal: UserGoal = UserGoal(
        sex = Sex.MALE,
        heightCm = 182.0,
        birthDate = LocalDate.of(1990, 4, 12),
        goalWeightKg = 82.0,
        paceKgPerWeek = 0.5,
        proteinTargetG = 148.0,
        proteinTargetMode = TargetMode.AUTO,
        createdAt = now,
    )

    /** A few weeks of weigh-ins trending down from ~95kg. */
    fun weightHistory(today: LocalDate = LocalDate.of(2026, 5, 29)): List<WeightPoint> {
        val start = today.minusDays(27)
        return (0..27).map { d ->
            val date = start.plusDays(d.toLong())
            val kg = 95.0 - d * 0.12 + ((d % 3) - 1) * 0.25
            WeightPoint(date = date, kg = (kg * 10).toInt() / 10.0, source = "sample")
        }
    }

    /** Several days of plausible logs ending today. Used by fakes and the seed harness. */
    fun logHistory(today: LocalDate = LocalDate.of(2026, 5, 29)): List<LogEntry> {
        val entries = mutableListOf<LogEntry>()
        var counter = 0
        for (dayOffset in 0..13) {
            val date = today.minusDays(dayOffset.toLong())
            val ts = date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC)
            fun add(food: Food, slot: MealSlot, portionGrams: Double?, units: Double?, scale: Double) {
                entries += LogEntry(
                    id = "log-${counter++}",
                    date = date,
                    timestamp = ts.plus((counter % 24).toLong(), ChronoUnit.HOURS),
                    mealSlot = slot,
                    foodId = food.id,
                    portion = food.defaultPortion,
                    nutrition = scaleNutrition(food.nutrition, scale),
                    confidence = Confidence.HIGH,
                )
            }
            add(foods[0], MealSlot.FROKOST, 60.0, null, 0.6)   // havregryn
            add(foods[1], MealSlot.FROKOST, 200.0, null, 2.0)  // lettmelk
            add(foods[6], MealSlot.MELLOMMALTID, null, 1.0, 1.0) // banan
            add(foods[3], MealSlot.MIDDAG, 150.0, null, 1.5)   // kylling
            add(foods[4], MealSlot.LUNSJ, 70.0, null, 0.7)     // grovbrød (2 skiver)
        }
        return entries
    }

    private fun scaleNutrition(n: Nutrition, factor: Double): Nutrition = Nutrition(
        kcal = n.kcal * factor,
        protein = n.protein?.times(factor),
        carbs = n.carbs?.times(factor),
        sugars = n.sugars?.times(factor),
        fat = n.fat?.times(factor),
        saturatedFat = n.saturatedFat?.times(factor),
        fiber = n.fiber?.times(factor),
        salt = n.salt?.times(factor),
        micros = n.micros?.mapValues { it.value * factor },
    )
}
