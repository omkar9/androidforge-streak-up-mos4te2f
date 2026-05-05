package com.androidforge.streakup.domain.usecase.streak

import com.androidforge.streakup.core.common.DateUtils
import com.androidforge.streakup.domain.model.HabitCompletion
import com.androidforge.streakup.domain.model.StreakInfo
import com.androidforge.streakup.presentation.ui.add_edit_habit.AddEditHabitEvent
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class CalculateStreaksUseCase @Inject constructor() {

    operator fun invoke(
        habitId: Long,
        frequencyType: AddEditHabitEvent.FrequencyType,
        frequencyValue: String,
        createdAt: Long, // Millis
        completionRecords: List<HabitCompletion>
    ): StreakInfo {
        val habitCreatedAt = DateUtils.millisToLocalDate(createdAt)

        val completedDates = completionRecords
            .filter { it.isCompleted }
            .map { it.date }
            .toSet()

        val selectedDaysOfWeek = if (frequencyType == AddEditHabitEvent.FrequencyType.WEEKLY) {
            frequencyValue.split(",")
                .mapNotNull { it.toIntOrNull() }
                .map { DateUtils.intToDayOfWeek(it) } // Convert 0=Sun, 1=Mon to DayOfWeek enum
                .toSet()
        } else {
            emptySet()
        }

        val today = LocalDate.now()

        var currentStreak = 0
        var longestStreak = 0
        var tempStreak = 0

        // --- Calculate Longest Streak ---
        var currentDateForLongest = habitCreatedAt
        while (!currentDateForLongest.isAfter(today)) {
            val isEligibleDay = when (frequencyType) {
                AddEditHabitEvent.FrequencyType.DAILY -> true
                AddEditHabitEvent.FrequencyType.WEEKLY -> selectedDaysOfWeek.contains(currentDateForLongest.dayOfWeek)
            }

            if (isEligibleDay) {
                if (completedDates.contains(currentDateForLongest)) {
                    tempStreak++
                } else {
                    // Streak broken on an eligible, uncompleted day
                    longestStreak = maxOf(longestStreak, tempStreak)
                    tempStreak = 0
                }
            }
            // If not an eligible day, it simply doesn't count towards the streak, it doesn't break it either.
            currentDateForLongest = currentDateForLongest.plusDays(1)
        }
        longestStreak = maxOf(longestStreak, tempStreak) // Account for the last streak


        // --- Calculate Current Streak ---
        currentStreak = 0
        var streakBroken = false
        var currentDateForCurrent = today // Start from today and go backwards

        while (!currentDateForCurrent.isBefore(habitCreatedAt) && !streakBroken) {
            val isEligibleDay = when (frequencyType) {
                AddEditHabitEvent.FrequencyType.DAILY -> true
                AddEditHabitEvent.FrequencyType.WEEKLY -> selectedDaysOfWeek.contains(currentDateForCurrent.dayOfWeek)
            }

            if (isEligibleDay) {
                if (completedDates.contains(currentDateForCurrent)) {
                    currentStreak++
                } else {
                    // This eligible day was missed, so the current streak is broken.
                    // The current streak is the count of *consecutive* completed eligible days *before* this missed day.
                    streakBroken = true
                }
            }
            // If not an eligible day, it doesn't count towards the streak, it doesn't break it either.
            currentDateForCurrent = currentDateForCurrent.minusDays(1)
        }

        return StreakInfo(currentStreak = currentStreak, longestStreak = longestStreak)
    }
}