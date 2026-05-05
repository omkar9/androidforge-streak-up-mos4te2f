package com.androidforge.streakup.data.local.mapper

import com.androidforge.streakup.data.local.entity.HabitCompletionEntity
import com.androidforge.streakup.domain.model.HabitCompletion
import java.time.LocalDate

fun HabitCompletionEntity.toDomain(): HabitCompletion {
    return HabitCompletion(
        id = id,
        habitId = habitId,
        date = LocalDate.ofEpochDay(date), // Convert epoch day to LocalDate
        isCompleted = isCompleted
    )
}

fun HabitCompletion.toEntity(): HabitCompletionEntity {
    return HabitCompletionEntity(
        id = id,
        habitId = habitId,
        date = date.toEpochDay(), // Convert LocalDate to epoch day
        isCompleted = isCompleted
    )
}