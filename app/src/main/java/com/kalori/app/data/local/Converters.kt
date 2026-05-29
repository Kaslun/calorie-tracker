package com.kalori.app.data.local

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate

/**
 * Room TypeConverters.
 *
 * SEAM FOR data-layer: this holds the load-bearing temporal converters (Instant, LocalDate)
 * so the DB compiles now. The data-layer agent OWNS this file going forward and must add:
 *  - the `micros` Map<Micronutrient, Double> <-> JSON string converter (handoff: JSON column)
 *  - converters for any enum/value types embedded in the real @Entity definitions.
 * Keep null-handling honest: a null nutrient/micros value means UNKNOWN, never 0.0.
 */
class Converters {

    @TypeConverter
    fun instantToEpochMilli(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun epochMilliToInstant(millis: Long?): Instant? = millis?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun localDateToEpochDay(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun epochDayToLocalDate(epochDay: Long?): LocalDate? = epochDay?.let(LocalDate::ofEpochDay)
}
