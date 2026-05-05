package com.androidforge.streakup.domain.model

import androidx.compose.ui.graphics.Color
import com.androidforge.streakup.presentation.ui.add_edit_habit.AddEditHabitEvent
import java.time.LocalDate

data class Habit(
    val id: Long = 0L,
    val name: String,
    val description: String,
    val frequencyType: AddEditHabitEvent.FrequencyType,
    val frequencyValue: String, // e.g., "0,1,2" for Sunday, Monday, Tuesday for weekly, empty for daily
    val reminderTime: String?, // e.g., "18:00"
    val color: Long, // ARGB color as Long
    val createdAt: Long, // Millis since epoch
    val isActive: Boolean,
    val completionRecords: List<HabitCompletion> = emptyList(), // Nested completions for detail view/streak calculation
    val currentStreak: Int = 0, // Derived property
    val longestStreak: Int = 0, // Derived property
    val isCompletedToday: Boolean = false // Derived property
) {
    fun getComposeColor(): Color {
        return Color(color)
    }
}