package com.kalori.app.data.local.entity

import androidx.room.ColumnInfo
import com.kalori.app.domain.model.Micronutrient
import com.kalori.app.domain.model.Nutrition
import com.kalori.app.domain.model.Portion

/**
 * Room-persistable embedded value objects, kept SEPARATE from the plain-Kotlin domain models
 * in [com.kalori.app.domain.model]. Repositories map between these and the domain types so
 * screens/repos never depend on Room.
 *
 * The common macros (kcal, protein, carbs, fat, fiber) — plus sugars, saturatedFat and salt —
 * are flattened into real columns for cheap aggregate SQL (`SUM(...)` per `date`). `micros` is
 * stored as a JSON string via the Converter; null = unknown, distinct from 0.0.
 */
data class NutritionEmbedded(
    @ColumnInfo(name = "kcal") val kcal: Double,
    @ColumnInfo(name = "protein") val protein: Double?,
    @ColumnInfo(name = "carbs") val carbs: Double?,
    @ColumnInfo(name = "sugars") val sugars: Double?,
    @ColumnInfo(name = "fat") val fat: Double?,
    @ColumnInfo(name = "saturatedFat") val saturatedFat: Double?,
    @ColumnInfo(name = "fiber") val fiber: Double?,
    @ColumnInfo(name = "salt") val salt: Double?,
    /** JSON column via Converters.microsToJson; null = no micros recorded. */
    @ColumnInfo(name = "micros") val micros: Map<Micronutrient, Double>?,
)

/**
 * Embedded portion. Both fields nullable: a PER_100G portion carries [grams]; a PER_UNIT
 * portion carries [units]. Used with `@Embedded(prefix = ...)` where it appears alongside
 * another Portion/Nutrition on the same entity (e.g. Food's default portion).
 */
data class PortionEmbedded(
    @ColumnInfo(name = "label") val label: String?,
    @ColumnInfo(name = "grams") val grams: Double?,
    @ColumnInfo(name = "units") val units: Double?,
)

fun NutritionEmbedded.toDomain(): Nutrition = Nutrition(
    kcal = kcal,
    protein = protein,
    carbs = carbs,
    sugars = sugars,
    fat = fat,
    saturatedFat = saturatedFat,
    fiber = fiber,
    salt = salt,
    micros = micros,
)

fun Nutrition.toEmbedded(): NutritionEmbedded = NutritionEmbedded(
    kcal = kcal,
    protein = protein,
    carbs = carbs,
    sugars = sugars,
    fat = fat,
    saturatedFat = saturatedFat,
    fiber = fiber,
    salt = salt,
    micros = micros,
)

fun PortionEmbedded.toDomain(): Portion =
    Portion(label = label.orEmpty(), grams = grams, units = units)

fun Portion.toEmbedded(): PortionEmbedded = PortionEmbedded(label = label, grams = grams, units = units)

/**
 * Maps a nullable embedded portion to a domain Portion, or null when absent. Room reads a
 * nullable @Embedded back as a non-null object with all-null columns when the row had no
 * portion, so we treat "all fields null" as absent.
 */
fun PortionEmbedded?.toDomainOrNull(): Portion? {
    if (this == null) return null
    return if (label == null && grams == null && units == null) null else toDomain()
}
