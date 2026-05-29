package com.kalori.app.data.local

import androidx.room.TypeConverter
import com.kalori.app.domain.model.CalibrationBasis
import com.kalori.app.domain.model.Confidence
import com.kalori.app.domain.model.FoodSource
import com.kalori.app.domain.model.MealSlot
import com.kalori.app.domain.model.Micronutrient
import com.kalori.app.domain.model.NutritionBasis
import com.kalori.app.domain.model.Sex
import com.kalori.app.domain.model.TargetMode
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate

/**
 * Room TypeConverters.
 *
 * Holds the temporal converters (Instant, LocalDate), the `micros` map <-> JSON string
 * converter, and converters for the domain enums embedded in the entities.
 *
 * Null-handling is honest: a null nutrient/micros value means UNKNOWN, never 0.0.
 *  - A null `micros` map column means "no micros recorded" and round-trips as null.
 *  - An empty map and a null map are kept distinct only at the domain layer; the converter
 *    serializes an empty map to "{}" and null to null, so the distinction survives.
 *
 * Micros are stored keyed by the enum NAME (stable string), not ordinal, so reordering the
 * [Micronutrient] enum can never silently corrupt persisted data.
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

    // --- micros map <-> JSON ------------------------------------------------------------

    @TypeConverter
    fun microsToJson(micros: Map<Micronutrient, Double>?): String? {
        if (micros == null) return null
        // Key by enum name for forward-compatibility against enum reordering.
        val byName = micros.entries.associate { (k, v) -> k.name to v }
        return json.encodeToString(mapSerializer, byName)
    }

    @TypeConverter
    fun jsonToMicros(stored: String?): Map<Micronutrient, Double>? {
        if (stored == null) return null
        val byName: Map<String, Double> = json.decodeFromString(mapSerializer, stored)
        return byName.entries.mapNotNull { (name, v) ->
            // Drop unknown keys defensively (e.g. a key removed from the enum in a later build).
            runCatching { Micronutrient.valueOf(name) }.getOrNull()?.let { it to v }
        }.toMap()
    }

    // --- enums --------------------------------------------------------------------------

    @TypeConverter fun foodSourceToName(v: FoodSource): String = v.name
    @TypeConverter fun nameToFoodSource(v: String): FoodSource = FoodSource.valueOf(v)

    @TypeConverter fun basisToName(v: NutritionBasis): String = v.name
    @TypeConverter fun nameToBasis(v: String): NutritionBasis = NutritionBasis.valueOf(v)

    @TypeConverter fun mealSlotToName(v: MealSlot): String = v.name
    @TypeConverter fun nameToMealSlot(v: String): MealSlot = MealSlot.valueOf(v)

    @TypeConverter fun confidenceToName(v: Confidence): String = v.name
    @TypeConverter fun nameToConfidence(v: String): Confidence = Confidence.valueOf(v)

    @TypeConverter fun sexToName(v: Sex): String = v.name
    @TypeConverter fun nameToSex(v: String): Sex = Sex.valueOf(v)

    @TypeConverter fun targetModeToName(v: TargetMode): String = v.name
    @TypeConverter fun nameToTargetMode(v: String): TargetMode = TargetMode.valueOf(v)

    @TypeConverter fun calibrationBasisToName(v: CalibrationBasis): String = v.name
    @TypeConverter fun nameToCalibrationBasis(v: String): CalibrationBasis =
        CalibrationBasis.valueOf(v)

    private companion object {
        val json = Json { ignoreUnknownKeys = true }
        val mapSerializer = MapSerializer(String.serializer(), Double.serializer())
    }
}
