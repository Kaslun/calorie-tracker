package com.kalori.app.data.repository

import com.kalori.app.data.local.dao.LogEntryDao
import com.kalori.app.data.local.entity.toDomain
import com.kalori.app.data.local.entity.toEntity
import com.kalori.app.domain.model.LogEntry
import com.kalori.app.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed [LogRepository]. Stores each entry's FROZEN nutrition snapshot verbatim; this
 * repo never recomputes nutrition from the referenced Food, so editing a Food cannot mutate
 * historical entries. Daily totals are queried by `date` (not `timestamp`).
 *
 * [add] and [update] both upsert by primary key — the entry id is the stable key.
 */
@Singleton
class LogRepositoryImpl @Inject constructor(
    private val logEntryDao: LogEntryDao,
) : LogRepository {

    override fun observeEntriesForDate(date: LocalDate): Flow<List<LogEntry>> =
        logEntryDao.observeForDate(date).map { list -> list.map { it.toDomain() } }

    override suspend fun add(entry: LogEntry) = logEntryDao.upsert(entry.toEntity())

    override suspend fun update(entry: LogEntry) = logEntryDao.upsert(entry.toEntity())

    override suspend fun delete(id: String) = logEntryDao.delete(id)

    override suspend fun entriesForRange(start: LocalDate, end: LocalDate): List<LogEntry> =
        logEntryDao.forRange(start, end).map { it.toDomain() }
}
