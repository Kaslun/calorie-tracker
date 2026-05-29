package com.kalori.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kalori.app.data.local.dao.FoodDao
import com.kalori.app.data.local.dao.FoodPortionDao
import com.kalori.app.data.local.dao.GoalDao
import com.kalori.app.data.local.dao.LogEntryDao
import com.kalori.app.data.local.dao.MealDao
import com.kalori.app.data.local.dao.WeightPointDao
import com.kalori.app.data.local.entity.FoodEntity
import com.kalori.app.data.local.entity.FoodPortionEntity
import com.kalori.app.data.local.entity.LogEntryEntity
import com.kalori.app.data.local.entity.MealComponentEntity
import com.kalori.app.data.local.entity.MealEntity
import com.kalori.app.data.local.entity.TdeeCalibrationEntity
import com.kalori.app.data.local.entity.UserGoalEntity
import com.kalori.app.data.local.entity.WeightPointEntity

/**
 * The app's Room database — schema version 1 (the first real schema; nothing has shipped, so
 * the data model lands directly as v1 and the exported schema JSON under app/schemas is
 * regenerated from these entities).
 *
 * Future schema changes MUST bump [version] and add an explicit Migration. The production DB
 * is preserved across installs (debug signing), so destructive fallback is not on the normal
 * path — see [com.kalori.app.di.DatabaseModule].
 */
@Database(
    entities = [
        FoodEntity::class,
        FoodPortionEntity::class,
        MealEntity::class,
        MealComponentEntity::class,
        LogEntryEntity::class,
        WeightPointEntity::class,
        UserGoalEntity::class,
        TdeeCalibrationEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class KaloriDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao
    abstract fun foodPortionDao(): FoodPortionDao
    abstract fun mealDao(): MealDao
    abstract fun logEntryDao(): LogEntryDao
    abstract fun weightPointDao(): WeightPointDao
    abstract fun goalDao(): GoalDao
}
