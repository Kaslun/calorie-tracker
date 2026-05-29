package com.kalori.app.data.matvaretabellen

import com.kalori.app.domain.model.Food
import com.kalori.app.domain.model.FoodSource
import com.kalori.app.domain.model.Micronutrient
import com.kalori.app.domain.model.Nutrition
import com.kalori.app.domain.model.NutritionBasis
import java.io.InputStream
import java.time.Instant

/**
 * Parses a Matvaretabellen CSV export into per-100g [Food]s (source = MATVARETABELLEN).
 *
 * CSV format targeted (matvaretabellen.no export):
 *  - semicolon (`;`) delimited
 *  - Norwegian decimal commas (`13,2`) -> parsed via comma->dot
 *  - header row with Norwegian column names; we resolve columns by header name (prefix match)
 *    so column ORDER and extra columns in the full ~2000-row file don't break the import.
 *  - all values are per-100g of edible portion.
 *
 * A blank / "0" micronutrient cell is treated as ZERO here (the source reports 0 explicitly),
 * EXCEPT macros that are genuinely unmeasured stay null. Per the null!=0 rule we only emit a
 * micro entry when the source carried a parseable number; truly absent columns map to null.
 *
 * Pure (no Android/Room deps) for unit testing. The full CSV drops in by replacing the bundled
 * asset (see [MatvaretabellenImporter.ASSET_NAME]); no code change needed.
 */
object MatvaretabellenCsvParser {

    fun parse(input: InputStream, now: Instant = Instant.now()): List<Food> =
        input.bufferedReader(Charsets.UTF_8).useLines { lines ->
            parseLines(lines.toList(), now)
        }

    fun parseLines(lines: List<String>, now: Instant = Instant.now()): List<Food> {
        if (lines.isEmpty()) return emptyList()
        val header = splitRow(lines.first())
        val idx = ColumnIndex(header)
        val nameCol = idx.require("Matvare")

        return lines.drop(1)
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                val cells = splitRow(line)
                val name = cells.getOrNull(nameCol)?.trim().orEmpty()
                if (name.isEmpty()) return@mapNotNull null
                val kcal = idx.num(cells, "Energi (kcal)") ?: return@mapNotNull null

                val micros = buildMap {
                    fun put(m: Micronutrient, header: String) {
                        idx.num(cells, header)?.let { put(m, it) }
                    }
                    put(Micronutrient.VITAMIN_A, "Vitamin A")
                    put(Micronutrient.VITAMIN_D, "Vitamin D")
                    put(Micronutrient.VITAMIN_E, "Vitamin E")
                    put(Micronutrient.VITAMIN_C, "Vitamin C")
                    put(Micronutrient.VITAMIN_B1, "Tiamin")
                    put(Micronutrient.VITAMIN_B2, "Riboflavin")
                    put(Micronutrient.VITAMIN_B3, "Niacin")
                    put(Micronutrient.VITAMIN_B6, "Vitamin B6")
                    put(Micronutrient.VITAMIN_B9, "Folat")
                    put(Micronutrient.VITAMIN_B12, "Vitamin B12")
                    put(Micronutrient.CALCIUM, "Kalsium")
                    put(Micronutrient.IRON, "Jern")
                    put(Micronutrient.MAGNESIUM, "Magnesium")
                    put(Micronutrient.PHOSPHORUS, "Fosfor")
                    put(Micronutrient.POTASSIUM, "Kalium")
                    put(Micronutrient.SODIUM, "Natrium")
                    put(Micronutrient.ZINC, "Sink")
                    put(Micronutrient.SELENIUM, "Selen")
                    put(Micronutrient.IODINE, "Jod")
                    put(Micronutrient.COPPER, "Kobber")
                }.takeIf { it.isNotEmpty() }

                val nutrition = Nutrition(
                    kcal = kcal,
                    protein = idx.num(cells, "Protein"),
                    carbs = idx.num(cells, "Karbohydrat"),
                    sugars = idx.num(cells, "Sukker"),
                    fat = idx.num(cells, "Fett", exclude = "Mettet"),
                    saturatedFat = idx.num(cells, "Mettet fett"),
                    fiber = idx.num(cells, "Kostfiber"),
                    salt = idx.num(cells, "Salt"),
                    micros = micros,
                )

                Food(
                    id = "mvt-${slug(name)}",
                    name = name,
                    brand = null,
                    barcode = null,
                    source = FoodSource.MATVARETABELLEN,
                    sourceRef = name,
                    basis = NutritionBasis.PER_100G,
                    nutrition = nutrition,
                    defaultPortion = null,
                    createdAt = now,
                    lastUsedAt = null,
                )
            }
    }

    private fun splitRow(line: String): List<String> = line.split(';')

    private fun slug(name: String): String = name.lowercase()
        .map { if (it.isLetterOrDigit()) it else '-' }
        .joinToString("")
        .replace(Regex("-+"), "-")
        .trim('-')

    /** Resolves column positions by header NAME (prefix match), tolerant of column reordering. */
    private class ColumnIndex(header: List<String>) {
        private val names = header.map { it.trim() }

        fun require(prefix: String): Int =
            find(prefix) ?: error("Matvaretabellen CSV missing required column: $prefix")

        fun find(prefix: String, exclude: String? = null): Int? =
            names.indexOfFirst {
                it.startsWith(prefix, ignoreCase = true) &&
                    (exclude == null || !it.contains(exclude, ignoreCase = true))
            }.takeIf { it >= 0 }

        fun num(cells: List<String>, prefix: String, exclude: String? = null): Double? {
            val i = find(prefix, exclude) ?: return null
            val raw = cells.getOrNull(i)?.trim().orEmpty()
            if (raw.isEmpty()) return null
            return raw.replace(',', '.').toDoubleOrNull()
        }
    }
}
