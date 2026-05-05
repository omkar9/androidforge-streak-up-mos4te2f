package com.androidforge.streakup.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androidforge.streakup.core.common.Constants
import com.androidforge.streakup.data.local.dao.HabitCompletionDao
import com.androidforge.streakup.data.local.dao.HabitDao
import com.androidforge.streakup.data.local.entity.HabitCompletionEntity
import com.androidforge.streakup.data.local.entity.HabitEntity

@Database(
    entities = [HabitEntity::class, HabitCompletionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
}