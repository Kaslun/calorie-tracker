package com.kalori.app.data.repository

import com.kalori.app.data.local.dao.WeightPointDao
import com.kalori.app.data.local.entity.toDomain
import com.kalori.app.data.local.entity.toEntity
import com.kalori.app.domain.model.WeightPoint
import com.kalori.app.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed [WeightRepository]. This is a CACHE only — Health Connect is the source of truth
 * for weight (the healthconnect agent owns sync). Reads are ordered ascending by date for the
 * trend/projection consumers.
 */
@Singleton
class WeightRepositoryImpl @Inject constructor(
    private val weightPointDao: WeightPointDao,
) : WeightRepository {

    override fun observeRecent(days: Int): Flow<List<WeightPoint>> =
        weightPointDao.observeRecent(days).map { list -> list.map { it.toDomain() }.sortedBy { it.date } }

    override suspend fun latest(): WeightPoint? = weightPointDao.latest()?.toDomain()

    override suspend fun upsert(point: WeightPoint) = weightPointDao.upsert(point.toEntity())
}
