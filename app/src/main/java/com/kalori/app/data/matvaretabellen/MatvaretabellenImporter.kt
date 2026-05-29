package com.kalori.app.data.matvaretabellen

import android.content.Context
import android.util.Log
import com.kalori.app.data.local.dao.FoodDao
import com.kalori.app.data.local.entity.toEntity
import com.kalori.app.domain.model.FoodSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Loads the bundled Matvaretabellen CSV into Room on first run. This is the micronutrient
 * backbone of the app. It is a BUILD-TIME bundle — never network-fetched.
 *
 * Idempotent: it inserts with IGNORE on conflict (stable `mvt-<slug>` ids), and short-circuits
 * if Matvaretabellen foods are already present, so calling it on every launch is cheap and safe.
 *
 * DROP-IN POINT for the full ~2000-row table: replace the asset at [ASSET_NAME] with the full
 * matvaretabellen.no CSV export (same semicolon-delimited format). No code change required —
 * the parser resolves columns by header name, tolerant of extra/reordered columns.
 */
@Singleton
class MatvaretabellenImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val foodDao: FoodDao,
) {

    /** Imports the bundled CSV if no Matvaretabellen foods exist yet. Returns rows inserted. */
    suspend fun importIfNeeded(): Int = withContext(Dispatchers.IO) {
        val existing = foodDao.countBySource(FoodSource.MATVARETABELLEN.name)
        if (existing > 0) {
            Log.d(TAG, "Matvaretabellen already imported ($existing rows); skipping.")
            return@withContext 0
        }
        importNow()
    }

    /** Forces a (re)import; used by the dev menu. Existing rows are kept (IGNORE on conflict). */
    suspend fun importNow(): Int = withContext(Dispatchers.IO) {
        val foods = try {
            context.assets.open(ASSET_NAME).use { MatvaretabellenCsvParser.parse(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read/parse $ASSET_NAME: ${e.message}")
            return@withContext 0
        }
        val entities = foods.map { it.toEntity() }
        val rowIds = foodDao.insertIgnore(entities)
        val inserted = rowIds.count { it != -1L }
        Log.d(TAG, "Matvaretabellen import: parsed ${foods.size}, inserted $inserted.")
        inserted
    }

    companion object {
        /** Replace this asset with the full Matvaretabellen CSV to ship all ~2000 foods. */
        const val ASSET_NAME = "matvaretabellen_sample.csv"
        private const val TAG = "MatvaretabellenImport"
    }
}
