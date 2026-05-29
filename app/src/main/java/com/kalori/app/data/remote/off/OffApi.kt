package com.kalori.app.data.remote.off

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Open Food Facts API v2. The ONLY network dependency in the app.
 *
 * Implementations must never throw into the UI path: [OffClient] wraps this call and returns
 * null on miss/error so the upstream manual-add flow can take over.
 */
interface OffApi {

    @GET("api/v2/product/{barcode}.json")
    suspend fun getProduct(@Path("barcode") barcode: String): OffProductResponse
}
