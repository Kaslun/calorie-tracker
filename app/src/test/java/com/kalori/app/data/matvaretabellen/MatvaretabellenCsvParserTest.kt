package com.kalori.app.data.matvaretabellen

import com.kalori.app.domain.model.FoodSource
import com.kalori.app.domain.model.Micronutrient
import com.kalori.app.domain.model.NutritionBasis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MatvaretabellenCsvParserTest {

    // Minimal real-shaped header (semicolon-delimited, Norwegian names) + one data row.
    private val header =
        "Matvare;Energi (kcal);Protein (g);Fett (g);Mettet fett (g);Karbohydrat (g);Sukker (g);" +
            "Kostfiber (g);Salt (g);Vitamin D (µg);Jern (mg)"

    @Test
    fun `parses norwegian decimal commas and resolves columns by header name`() {
        val rows = listOf(
            header,
            "Havregryn lettkokte;370;13,2;7,0;1,3;58,7;1,0;10,0;0,02;0;4,7",
        )
        val foods = MatvaretabellenCsvParser.parseLines(rows)
        assertEquals(1, foods.size)
        val f = foods.first()

        assertEquals("Havregryn lettkokte", f.name)
        assertEquals(FoodSource.MATVARETABELLEN, f.source)
        assertEquals(NutritionBasis.PER_100G, f.basis)
        assertEquals(370.0, f.nutrition.kcal, 1e-9)
        assertEquals(13.2, f.nutrition.protein!!, 1e-9)
        assertEquals(7.0, f.nutrition.fat!!, 1e-9)        // "Fett", not "Mettet fett"
        assertEquals(1.3, f.nutrition.saturatedFat!!, 1e-9)
        assertEquals(10.0, f.nutrition.fiber!!, 1e-9)
        assertEquals(4.7, f.nutrition.micros!![Micronutrient.IRON]!!, 1e-9)
    }

    @Test
    fun `blank energy row is skipped`() {
        val rows = listOf(header, "Tom rad;;;;;;;;;;")
        assertTrue(MatvaretabellenCsvParser.parseLines(rows).isEmpty())
    }

    @Test
    fun `column reordering does not break resolution`() {
        val reordered = "Jern (mg);Matvare;Energi (kcal);Protein (g)"
        val rows = listOf(reordered, "4,7;Egg;145;12,6")
        val f = MatvaretabellenCsvParser.parseLines(rows).first()
        assertEquals("Egg", f.name)
        assertEquals(145.0, f.nutrition.kcal, 1e-9)
        assertEquals(12.6, f.nutrition.protein!!, 1e-9)
        assertEquals(4.7, f.nutrition.micros!![Micronutrient.IRON]!!, 1e-9)
        // Columns absent from the header stay null (UNKNOWN), not 0.0.
        assertNull(f.nutrition.fiber)
    }
}
