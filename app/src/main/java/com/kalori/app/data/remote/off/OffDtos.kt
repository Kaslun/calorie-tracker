package com.kalori.app.data.remote.off

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Open Food Facts API v2 product response DTOs.
 * Endpoint: GET https://world.openfoodfacts.org/api/v2/product/{barcode}.json
 *
 * OFF returns `status = 1` on hit, `0` on miss. Nutriment fields are sparse and arrive as
 * `*_100g` keys; a missing nutriment means UNKNOWN (mapped to null, never 0.0).
 */
@Serializable
data class OffProductResponse(
    val status: Int = 0,
    val code: String? = null,
    val product: OffProduct? = null,
)

@Serializable
data class OffProduct(
    @SerialName("product_name") val productName: String? = null,
    @SerialName("product_name_no") val productNameNo: String? = null,
    @SerialName("generic_name") val genericName: String? = null,
    val brands: String? = null,
    @SerialName("serving_quantity") val servingQuantity: String? = null,
    @SerialName("serving_size") val servingSize: String? = null,
    val nutriments: OffNutriments? = null,
)

/**
 * Per-100g nutriments. All nullable: absent = unknown. OFF reports energy in kJ and kcal; we
 * prefer the explicit kcal field. Salt is reported directly (g/100g); sodium also available.
 */
@Serializable
data class OffNutriments(
    @SerialName("energy-kcal_100g") val energyKcal100g: Double? = null,
    @SerialName("energy-kj_100g") val energyKj100g: Double? = null,
    @SerialName("proteins_100g") val proteins100g: Double? = null,
    @SerialName("carbohydrates_100g") val carbohydrates100g: Double? = null,
    @SerialName("sugars_100g") val sugars100g: Double? = null,
    @SerialName("fat_100g") val fat100g: Double? = null,
    @SerialName("saturated-fat_100g") val saturatedFat100g: Double? = null,
    @SerialName("fiber_100g") val fiber100g: Double? = null,
    @SerialName("salt_100g") val salt100g: Double? = null,
    @SerialName("sodium_100g") val sodium100g: Double? = null,
)
