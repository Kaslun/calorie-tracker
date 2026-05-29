package com.kalori.app.domain.nutrition

import com.kalori.app.domain.model.Food
import com.kalori.app.domain.model.FoodSource
import com.kalori.app.domain.model.Micronutrient
import com.kalori.app.domain.model.Nutrition
import com.kalori.app.domain.model.NutritionBasis
import com.kalori.app.domain.model.Portion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class NutritionResolverTest {

    private val now = Instant.parse("2026-05-29T08:00:00Z")

    private fun per100g(): Food = Food(
        id = "f1",
        name = "Havregryn",
        source = FoodSource.MATVARETABELLEN,
        basis = NutritionBasis.PER_100G,
        nutrition = Nutrition(
            kcal = 370.0,
            protein = 13.0,
            carbs = 58.0,
            fat = 7.0,
            fiber = 10.0,
            sugars = null, // UNKNOWN
            saturatedFat = null,
            salt = null,
            micros = mapOf(Micronutrient.IRON to 4.7),
        ),
        createdAt = now,
    )

    private fun perUnit(): Food = Food(
        id = "f2",
        name = "Egg",
        source = FoodSource.MATVARETABELLEN,
        basis = NutritionBasis.PER_UNIT,
        nutrition = Nutrition(
            kcal = 78.0,
            protein = 6.3,
            fat = 5.3,
            carbs = 0.6,
            fiber = null,
            micros = mapOf(Micronutrient.VITAMIN_D to 1.1),
        ),
        createdAt = now,
    )

    @Test
    fun `per-100g resolves value times grams over 100`() {
        val resolved = NutritionResolver.resolve(per100g(), Portion("Porsjon", grams = 60.0))
        // 370 * 60 / 100 = 222
        assertEquals(222.0, resolved.kcal, 1e-9)
        assertEquals(13.0 * 0.6, resolved.protein!!, 1e-9)
        assertEquals(58.0 * 0.6, resolved.carbs!!, 1e-9)
        assertEquals(4.7 * 0.6, resolved.micros!![Micronutrient.IRON]!!, 1e-9)
    }

    @Test
    fun `per-unit resolves value times count`() {
        val resolved = NutritionResolver.resolve(perUnit(), Portion("Stk", units = 3.0))
        assertEquals(78.0 * 3, resolved.kcal, 1e-9)
        assertEquals(6.3 * 3, resolved.protein!!, 1e-9)
        assertEquals(1.1 * 3, resolved.micros!![Micronutrient.VITAMIN_D]!!, 1e-9)
    }

    @Test
    fun `null nutrient stays null through resolution and is never coerced to zero`() {
        val resolved = NutritionResolver.resolve(per100g(), Portion("Porsjon", grams = 60.0))
        assertNull("sugars was unknown; must remain null, not 0.0", resolved.sugars)
        assertNull(resolved.saturatedFat)
        assertNull(resolved.salt)
    }

    @Test
    fun `per-100g falls back to default portion then to 100g`() {
        val foodWithDefault = per100g().copy(defaultPortion = Portion("Porsjon", grams = 50.0))
        // null portion -> default 50g -> 370 * 0.5
        assertEquals(185.0, NutritionResolver.resolve(foodWithDefault, null).kcal, 1e-9)
        // no portion and no default -> 100g multiplier (1.0)
        assertEquals(370.0, NutritionResolver.resolve(per100g(), null).kcal, 1e-9)
    }

    @Test
    fun `multiplier for per-unit defaults to one unit when absent`() {
        assertEquals(1.0, NutritionResolver.multiplierFor(NutritionBasis.PER_UNIT, null), 1e-9)
        assertEquals(2.0, NutritionResolver.multiplierFor(NutritionBasis.PER_UNIT, Portion("x", units = 2.0)), 1e-9)
    }

    @Test
    fun `frozen snapshot is unaffected by a later food edit`() {
        val food = per100g()
        // Resolve and "freeze" a snapshot, as LogRepository would store it.
        val frozen = NutritionResolver.resolve(food, Portion("Porsjon", grams = 100.0))
        val frozenKcal = frozen.kcal
        val frozenIron = frozen.micros!![Micronutrient.IRON]

        // The food is later edited (corrected kcal, iron removed).
        val edited = food.copy(
            nutrition = food.nutrition.copy(kcal = 999.0, micros = emptyMap()),
        )
        // Re-resolving the EDITED food changes; the previously frozen snapshot does not.
        val reResolved = NutritionResolver.resolve(edited, Portion("Porsjon", grams = 100.0))

        assertEquals(370.0, frozenKcal, 1e-9)
        assertEquals(4.7, frozenIron!!, 1e-9)
        assertEquals(999.0, reResolved.kcal, 1e-9)
        assertTrue(reResolved.micros!!.isEmpty())
        // The frozen value object is immutable and still holds the original numbers.
        assertEquals(370.0, frozen.kcal, 1e-9)
    }
}
