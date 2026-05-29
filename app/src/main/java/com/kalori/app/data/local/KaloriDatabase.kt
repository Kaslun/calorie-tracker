package com.kalori.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * The app's Room database.
 *
 * SEAM FOR data-layer: this is the wired database class with [Converters] registered and a
 * Hilt provision (see [com.kalori.app.di.DatabaseModule]). It currently holds only the
 * scaffold placeholder so the project compiles. data-layer must:
 *  - add the real entities to the `entities = [...]` array,
 *  - declare DAO accessors here,
 *  - remove [PlaceholderEntity]/[PlaceholderDao],
 *  - bump `version` and add migrations as the schema evolves.
 */
@Database(
    entities = [
        PlaceholderEntity::class,
        // data-layer: add Food, FoodPortion, Meal, MealComponent, LogEntry,
        // WeightPoint, UserGoal, TdeeCalibration here.
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class KaloriDatabase : RoomDatabase() {

    abstract fun placeholderDao(): PlaceholderDao
    // data-layer: declare real DAO accessors here, e.g. fun foodDao(): FoodDao
}
