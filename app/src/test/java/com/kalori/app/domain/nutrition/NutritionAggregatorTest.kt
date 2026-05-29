package com.kalori.app.domain.nutrition

import com.kalori.app.domain.model.Micronutrient
import com.kalori.app.domain.model.Nutrition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NutritionAggregatorTest {

    @Test
    fun `sums only known nutrients and reports contributing-item counts`() {
        // A day mixing known and unknown (null) fiber values.
        val day = listOf(
            Nutrition(kcal = 200.0, protein = 10.0, fiber = 3.0),       // fiber known
            Nutrition(kcal = 150.0, protein = 5.0, fiber = null),       // fiber UNKNOWN
            Nutrition(kcal = 100.0, protein = null, fiber = 2.0),       // protein UNKNOWN
            Nutrition(kcal = 50.0, protein = 4.0, fiber = null),        // fiber UNKNOWN
        )

        val totals = NutritionAggregator.aggregate(day)

        assertEquals(4, totals.entryCount)
        assertEquals(500.0, totals.kcal, 1e-9)

        // protein: 10 + 5 + 4 = 19 from 3 of 4 items (one was null, excluded).
        assertEquals(19.0, totals.protein.sum!!, 1e-9)
        assertEquals(3, totals.protein.contributing)
        assertEquals(4, totals.protein.total)

        // fiber: 3 + 2 = 5 from 2 of 4 items; nulls excluded, NOT treated as 0.0.
        assertEquals(5.0, totals.fiber.sum!!, 1e-9)
        assertEquals(2, totals.fiber.contributing)
        assertEquals(4, totals.fiber.total)
    }

    @Test
    fun `a nutrient with no contributing items has a null sum, not zero`() {
        val day = listOf(
            Nutrition(kcal = 200.0, salt = null),
            Nutrition(kcal = 100.0, salt = null),
        )
        val totals = NutritionAggregator.aggregate(day)
        assertNull("all-unknown salt must sum to null (UNKNOWN), never 0.0", totals.salt.sum)
        assertEquals(0, totals.salt.contributing)
        assertEquals(2, totals.salt.total)
    }

    @Test
    fun `micros aggregate per-nutrient with their own contributing counts`() {
        val day = listOf(
            Nutrition(kcal = 100.0, micros = mapOf(Micronutrient.VITAMIN_D to 6.0, Micronutrient.IRON to 2.0)),
            Nutrition(kcal = 100.0, micros = mapOf(Micronutrient.VITAMIN_D to 6.0)),
            Nutrition(kcal = 100.0, micros = null), // no micros recorded
        )
        val totals = NutritionAggregator.aggregate(day)

        val vitD = totals.micros[Micronutrient.VITAMIN_D]!!
        assertEquals(12.0, vitD.sum!!, 1e-9)
        assertEquals(2, vitD.contributing)
        assertEquals(3, vitD.total)

        val iron = totals.micros[Micronutrient.IRON]!!
        assertEquals(2.0, iron.sum!!, 1e-9)
        assertEquals(1, iron.contributing)

        // A micro nobody contributed is absent from the map (UNKNOWN), not present as 0.0.
        assertNull(totals.micros[Micronutrient.CALCIUM])
    }
}
