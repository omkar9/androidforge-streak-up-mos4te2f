package com.androidforge.streakup.data.local.mapper

import com.androidforge.streakup.data.local.entity.HabitEntity
import com.androidforge.streakup.data.local.entity.HabitWithCompletions
import com.androidforge.streakup.domain.model.Habit
import com.androidforge.streakup.presentation.ui.add_edit_habit.AddEditHabitEvent
import java.time.LocalDate

fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        name = name,
        description = description,
        frequencyType = AddEditHabitEvent.FrequencyType.valueOf(frequencyType),
        frequencyValue = frequencyValue,
        reminderTime = reminderTime,
        color = color,
        createdAt = createdAt,
        isActive = isActive
    )
}

fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        name = name,
        description = description,
        frequencyType = frequencyType.name,
        frequencyValue = frequencyValue,
        reminderTime = reminderTime,
        color = color,
        createdAt = createdAt,
        isActive = isActive
    )
}

fun HabitWithCompletions.toDomain(): Habit {
    val habit = this.habit.toDomain()
    val completions = this.completions.map { it.toDomain() }

    // Calculate isCompletedToday
    val today = LocalDate.now()
    val isCompletedToday = completions.any { it.date == today && it.isCompleted }

    return habit.copy(
        completionRecords = completions,
        isCompletedToday = isCompletedToday
        // currentStreak and longestStreak will be calculated by CalculateStreaksUseCase
    )
}