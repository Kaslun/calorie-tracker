package com.kalori.app.data.repository

import com.kalori.app.data.local.dao.FoodDao
import com.kalori.app.data.local.entity.toDomain
import com.kalori.app.data.local.entity.toEntity
import com.kalori.app.data.remote.off.OffClient
import com.kalori.app.domain.model.Food
import com.kalori.app.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed [FoodRepository] with Open Food Facts fallback on barcode misses.
 *
 * [findByBarcode] is the scan path: local cache first, then OFF. An OFF hit is cached
 * permanently (upserted) so the next scan is offline-instant. An OFF miss/error returns null,
 * which lets the upstream manual-add flow take over — this never throws into the UI path.
 */
@Singleton
class FoodRepositoryImpl @Inject constructor(
    private val foodDao: FoodDao,
    private val offClient: OffClient,
) : FoodRepository {

    override fun observeRecents(limit: Int): Flow<List<Food>> =
        foodDao.observeRecents(limit).map { list -> list.map { it.toDomain() } }

    override fun observeFavorites(): Flow<List<Food>> =
        foodDao.observeFavorites().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Food? = foodDao.getById(id)?.toDomain()

    override suspend fun findByBarcode(barcode: String): Food? {
        foodDao.findByBarcode(barcode)?.let { return it.toDomain() }
        val fromOff = offClient.lookup(barcode) ?: return null
        foodDao.upsert(fromOff.toEntity()) // cache permanently
        return fromOff
    }

    override suspend fun search(query: String): List<Food> =
        foodDao.search(query).map { it.toDomain() }

    override suspend fun upsert(food: Food) = foodDao.upsert(food.toEntity())

    override suspend fun setFavorite(id: String, favorite: Boolean) =
        foodDao.setFavorite(id, favorite)
}
