package com.kalori.app.data.remote.off

import com.kalori.app.domain.model.FoodSource
import com.kalori.app.domain.model.NutritionBasis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class OffMappingTest {

    private val now = Instant.parse("2026-05-29T08:00:00Z")

    @Test
    fun `maps OFF product to per-100g food with sparse nutriments left null`() {
        val product = OffProduct(
            productName = "Generic Oats",
            productNameNo = "Havregryn",
            brands = "Bjørn, Annet",
            servingQuantity = "40",
            servingSize = "40 g",
            nutriments = OffNutriments(
                energyKcal100g = 370.0,
                proteins100g = 13.0,
                carbohydrates100g = 58.0,
                fat100g = 7.0,
                // fiber and sugars absent -> must stay null
                salt100g = 0.02,
            ),
        )
        val food = product.toFood("7035620025013", now)!!

        assertEquals("off-7035620025013", food.id)
        assertEquals("Havregryn", food.name)           // prefers Norwegian name
        assertEquals("Bjørn", food.brand)              // first brand only
        assertEquals(FoodSource.OFF, food.source)
        assertEquals(NutritionBasis.PER_100G, food.basis)
        assertEquals(370.0, food.nutrition.kcal, 1e-9)
        assertEquals(40.0, food.defaultPortion!!.grams!!, 1e-9)
        assertNull(food.nutrition.fiber)
        assertNull(food.nutrition.sugars)
    }

    @Test
    fun `converts kJ to kcal when kcal absent`() {
        val product = OffProduct(
            productName = "X",
            nutriments = OffNutriments(energyKj100g = 1554.0),
        )
        val food = product.toFood("123", now)!!
        assertEquals(1554.0 / 4.184, food.nutrition.kcal, 1e-6)
    }

    @Test
    fun `returns null when no energy or no name available`() {
        assertNull(OffProduct(productName = "X", nutriments = OffNutriments()).toFood("1", now))
        assertNull(OffProduct(nutriments = OffNutriments(energyKcal100g = 100.0)).toFood("1", now))
    }

    @Test
    fun `derives salt from sodium when salt absent`() {
        val product = OffProduct(
            productName = "Y",
            nutriments = OffNutriments(energyKcal100g = 50.0, sodium100g = 0.4),
        )
        val food = product.toFood("1", now)!!
        assertEquals(1.0, food.nutrition.salt!!, 1e-9) // 0.4 * 2.5
    }
}
