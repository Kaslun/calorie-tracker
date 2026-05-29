package com.kalori.app.data.repository

import com.kalori.app.data.local.dao.GoalDao
import com.kalori.app.data.local.entity.toDomain
import com.kalori.app.data.local.entity.toEntity
import com.kalori.app.domain.model.TdeeCalibration
import com.kalori.app.domain.model.UserGoal
import com.kalori.app.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed [GoalRepository]. The single [UserGoal] row uses a fixed id (1). Calibrations
 * are append-only history; [effectiveCalibration] returns the most recent row with
 * effectiveDate <= the queried date (basis for effective TDEE).
 */
@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
) : GoalRepository {

    override fun observeGoal(): Flow<UserGoal?> =
        goalDao.observeGoal().map { it?.toDomain() }

    override suspend fun getGoal(): UserGoal? = goalDao.getGoal()?.toDomain()

    override suspend fun setGoal(goal: UserGoal) = goalDao.setGoal(goal.toEntity())

    override fun observeCalibrations(): Flow<List<TdeeCalibration>> =
        goalDao.observeCalibrations().map { list -> list.map { it.toDomain() } }

    override suspend fun effectiveCalibration(date: LocalDate): TdeeCalibration? =
        goalDao.effectiveCalibration(date)?.toDomain()

    override suspend fun addCalibration(calibration: TdeeCalibration) =
        goalDao.addCalibration(calibration.toEntity())
}
