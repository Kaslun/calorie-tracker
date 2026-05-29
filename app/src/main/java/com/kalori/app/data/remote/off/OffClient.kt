package com.kalori.app.data.remote.off

import android.util.Log
import com.kalori.app.domain.model.Food
import com.kalori.app.domain.model.FoodSource
import com.kalori.app.domain.model.Nutrition
import com.kalori.app.domain.model.NutritionBasis
import com.kalori.app.domain.model.Portion
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fetches a product from Open Food Facts by barcode and maps it to a [Food] (source = OFF).
 *
 * Miss handling is the contract: a product-not-found, a malformed response, or ANY network
 * error returns `null` so the upstream manual-add flow takes over. This never throws into the
 * UI path. OFF misses are EXPECTED for Norwegian store brands (First Price, Coop, X-tra,
 * Eldorado), so the null path is a primary flow, not an edge case.
 *
 * Successful lookups are cached permanently by [com.kalori.app.data.repository.FoodRepositoryImpl].
 */
@Singleton
class OffClient @Inject constructor(
    private val api: OffApi,
) {

    /** Returns a mapped [Food] on hit, or null on miss / error. Never throws. */
    suspend fun lookup(barcode: String): Food? {
        val response = try {
            api.getProduct(barcode)
        } catch (e: Exception) {
            Log.w(TAG, "OFF lookup failed for $barcode: ${e.message}")
            return null
        }
        if (response.status != 1 || response.product == null) return null
        return response.product.toFood(barcode)
    }

    private companion object {
        const val TAG = "OffClient"
    }
}

/**
 * Maps an OFF product to a per-100g [Food]. Energy prefers explicit kcal; if only kJ is
 * present it is converted (kJ / 4.184). Missing nutriments stay null (UNKNOWN), never 0.0.
 * Salt prefers the reported salt field; falls back to sodium × 2.5 if only sodium is present.
 */
internal fun OffProduct.toFood(barcode: String, now: Instant = Instant.now()): Food? {
    val n = nutriments ?: return null
    val kcal = n.energyKcal100g ?: n.energyKj100g?.let { it / 4.184 } ?: return null
    val name = productNameNo?.takeIf { it.isNotBlank() }
        ?: productName?.takeIf { it.isNotBlank() }
        ?: genericName?.takeIf { it.isNotBlank() }
        ?: return null
    val salt = n.salt100g ?: n.sodium100g?.let { it * 2.5 }

    val nutrition = Nutrition(
        kcal = kcal,
        protein = n.proteins100g,
        carbs = n.carbohydrates100g,
        sugars = n.sugars100g,
        fat = n.fat100g,
        saturatedFat = n.saturatedFat100g,
        fiber = n.fiber100g,
        salt = salt,
        micros = null, // OFF micros are unreliable/sparse; Matvaretabellen is the micro backbone.
    )

    val servingGrams = servingQuantity
        ?.replace(',', '.')
        ?.filter { it.isDigit() || it == '.' }
        ?.toDoubleOrNull()
    val defaultPortion = servingGrams?.let { Portion(label = servingSize ?: "Porsjon", grams = it) }

    return Food(
        id = "off-$barcode",
        name = name.trim(),
        brand = brands?.split(',')?.firstOrNull()?.trim()?.takeIf { it.isNotBlank() },
        barcode = barcode,
        source = FoodSource.OFF,
        sourceRef = barcode,
        basis = NutritionBasis.PER_100G,
        nutrition = nutrition,
        defaultPortion = defaultPortion,
        createdAt = now,
        lastUsedAt = null,
    )
}
