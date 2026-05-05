package com.androidforge.streakup.domain.repository

import com.androidforge.streakup.core.common.Resource
import com.androidforge.streakup.domain.model.Habit
import com.androidforge.streakup.domain.model.HabitCompletion
import com.androidforge.streakup.presentation.ui.settings.AppSettings
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

interface HabitRepository {

    fun addHabit(habit: Habit): Flow<Resource<Long>> // Returns ID of the new habit
    fun updateHabit(habit: Habit): Flow<Resource<Unit>>
    fun deleteHabit(habitId: Long): Flow<Resource<Unit>>
    fun getHabitById(habitId: Long): Flow<Resource<Habit?>> // Includes completions
    fun getAllHabits(): Flow<Resource<List<Habit>>> // Includes completions and derived streaks
    fun markHabitCompleted(habitId: Long, date: LocalDate, isCompleted: Boolean): Flow<Resource<Unit>>

    // Settings related
    fun getAppSettings(): Flow<Resource<AppSettings>>
    fun updateDailyRemindersSetting(enable: Boolean): Flow<Resource<Unit>>

    // Onboarding related
    fun getOnboardingStatus(): Flow<Resource<Boolean>>
    fun setOnboardingComplete(isComplete: Boolean): Flow<Resource<Unit>>
}