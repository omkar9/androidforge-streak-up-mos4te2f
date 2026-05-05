package com.androidforge.streakup.domain.model

import java.time.LocalDate

data class HabitCompletion(
    val id: Long = 0L,
    val habitId: Long,
    val date: LocalDate,
    val isCompleted: Boolean
)