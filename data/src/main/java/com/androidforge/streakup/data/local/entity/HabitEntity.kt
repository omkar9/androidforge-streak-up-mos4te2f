package com.androidforge.streakup.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val description: String,
    @ColumnInfo(name = "frequency_type") val frequencyType: String, // DAILY, WEEKLY
    @ColumnInfo(name = "frequency_value") val frequencyValue: String, // e.g., "0,1,2" for weekly, empty for daily
    @ColumnInfo(name = "reminder_time") val reminderTime: String?, // "HH:mm"
    val color: Long, // ARGB as Long
    @ColumnInfo(name = "created_at") val createdAt: Long, // Millis since epoch
    @ColumnInfo(name = "is_active") val isActive: Boolean
)

// A class to hold HabitEntity with its related HabitCompletionEntities for Room relations
data class HabitWithCompletions(
    val habit: HabitEntity,
    val completions: List<HabitCompletionEntity>
)