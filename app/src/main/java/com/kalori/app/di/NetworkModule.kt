package com.kalori.app.di

import com.kalori.app.BuildConfig
import com.kalori.app.data.remote.off.OffApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Open Food Facts networking — the ONLY network dependency in the app.
 *
 * Lenient JSON: OFF responses are sparse and inconsistent across products, so the parser
 * ignores unknown keys and tolerates missing fields (all DTO fields are nullable/defaulted).
 * The miss/error contract lives in [com.kalori.app.data.remote.off.OffClient], which turns any
 * failure into a null Food.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val OFF_BASE_URL = "https://world.openfoodfacts.org/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC },
                )
            }
        }
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(OFF_BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideOffApi(retrofit: Retrofit): OffApi = retrofit.create(OffApi::class.java)
}
