package com.kalori.app.data.local

import com.kalori.app.domain.model.Micronutrient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `null micros round-trips as null (UNKNOWN), distinct from empty`() {
        assertNull(converters.microsToJson(null))
        assertNull(converters.jsonToMicros(null))
    }

    @Test
    fun `empty micros round-trips as empty map`() {
        val json = converters.microsToJson(emptyMap())
        assertTrue(converters.jsonToMicros(json)!!.isEmpty())
    }

    @Test
    fun `populated micros round-trip by enum name`() {
        val micros = mapOf(Micronutrient.VITAMIN_D to 6.0, Micronutrient.IRON to 4.7)
        val json = converters.microsToJson(micros)
        // Keyed by stable enum name, resilient to enum reordering.
        assertTrue(json!!.contains("VITAMIN_D"))
        assertEquals(micros, converters.jsonToMicros(json))
    }

    @Test
    fun `unknown micro keys are dropped defensively on decode`() {
        val stored = """{"VITAMIN_D":6.0,"UNOBTANIUM":1.0}"""
        val decoded = converters.jsonToMicros(stored)!!
        assertEquals(1, decoded.size)
        assertEquals(6.0, decoded[Micronutrient.VITAMIN_D]!!, 1e-9)
    }
}
