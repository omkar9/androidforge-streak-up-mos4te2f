package com.androidforge.streakup.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.androidforge.streakup.core.common.Constants
import com.androidforge.streakup.core.common.DateUtils
import com.androidforge.streakup.core.common.Resource
import com.androidforge.streakup.data.local.dao.HabitCompletionDao
import com.androidforge.streakup.data.local.dao.HabitDao
import com.androidforge.streakup.data.local.entity.HabitCompletionEntity
import com.androidforge.streakup.data.local.mapper.toDomain
import com.androidforge.streakup.data.local.mapper.toEntity
import com.androidforge.streakup.domain.model.Habit
import com.androidforge.streakup.domain.model.HabitCompletion
import com.androidforge.streakup.domain.repository.HabitRepository
import com.androidforge.streakup.domain.usecase.add_edit_habit.AddEditHabitEvent
import com.androidforge.streakup.domain.usecase.streak.CalculateStreaksUseCase
import com.androidforge.streakup.presentation.ui.settings.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = Constants.PREFS_NAME)

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val habitCompletionDao: HabitCompletionDao,
    private val calculateStreaksUseCase: CalculateStreaksUseCase,
    private val appContext: Context // Use appContext for DataStore
) : HabitRepository {

    private val ONBOARDING_COMPLETE_KEY = booleanPreferencesKey(Constants.PREF_ONBOARDING_COMPLETE)
    private val DAILY_REMINDERS_ENABLED_KEY = booleanPreferencesKey(Constants.PREF_DAILY_REMINDERS_ENABLED)

    override fun addHabit(habit: Habit): Flow<Resource<Long>> = flow {
        emit(Resource.Loading())
        try {
            val newHabitId = habitDao.insertHabit(habit.toEntity())
            emit(Resource.Success(newHabitId))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to add habit: ${e.localizedMessage}"))
        }
    }

    override fun updateHabit(habit: Habit): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            habitDao.updateHabit(habit.toEntity())
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to update habit: ${e.localizedMessage}"))
        }
    }

    override fun deleteHabit(habitId: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            habitDao.deleteHabit(habitId)
            habitCompletionDao.deleteCompletionsForHabit(habitId) // Also delete associated completions
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to delete habit: ${e.localizedMessage}"))
        }
    }

    override fun getHabitById(habitId: Long): Flow<Resource<Habit?>> = habitDao.getHabitWithCompletions(habitId)
        .map { habitWithCompletions ->
            if (habitWithCompletions != null) {
                val habit = habitWithCompletions.toDomain()
                val streakInfo = calculateStreaksUseCase(
                    habit.id,
                    habit.frequencyType,
                    habit.frequencyValue,
                    habit.createdAt,
                    habit.completionRecords
                )
                Resource.Success(habit.copy(currentStreak = streakInfo.currentStreak, longestStreak = streakInfo.longestStreak))
            } else {
                Resource.Success(null)
            }
        }
        .catch { e ->
            emit(Resource.Error("Failed to get habit details: ${e.localizedMessage}"))
        }

    override fun getAllHabits(): Flow<Resource<List<Habit>>> = habitDao.getAllHabitsWithCompletions()
        .map { listHabitWithCompletions ->
            val habits = listHabitWithCompletions.map { habitWithCompletions ->
                val habit = habitWithCompletions.toDomain()
                val streakInfo = calculateStreaksUseCase(
                    habit.id,
                    habit.frequencyType,
                    habit.frequencyValue,
                    habit.createdAt,
                    habit.completionRecords
                )
                habit.copy(currentStreak = streakInfo.currentStreak, longestStreak = streakInfo.longestStreak)
            }
            Resource.Success(habits)
        }
        .catch { e ->
            emit(Resource.Error("Failed to get all habits: ${e.localizedMessage}"))
        }

    override fun markHabitCompleted(habitId: Long, date: LocalDate, isCompleted: Boolean): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val existingCompletion = habitCompletionDao.getCompletionForDate(habitId, date.toEpochDay())
            if (existingCompletion != null) {
                // Update existing record
                habitCompletionDao.updateCompletion(existingCompletion.copy(isCompleted = isCompleted))
            } else {
                // Insert new record
                habitCompletionDao.insertCompletion(
                    HabitCompletionEntity(
                        habitId = habitId,
                        date = date.toEpochDay(),
                        isCompleted = isCompleted
                    )
                )
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to mark habit completion: ${e.localizedMessage}"))
        }
    }

    override fun getAppSettings(): Flow<Resource<AppSettings>> = appContext.dataStore.data
        .map { preferences ->
            val remindersEnabled = preferences[DAILY_REMINDERS_ENABLED_KEY] ?: true // Default to true
            Resource.Success(AppSettings(areDailyRemindersEnabled = remindersEnabled))
        }
        .catch { exception ->
            if (exception is IOException) {
                emit(Resource.Error("Error reading settings: ${exception.localizedMessage}"))
            } else {
                emit(Resource.Error("An unknown error occurred: ${exception.localizedMessage}"))
            }
        }

    override fun updateDailyRemindersSetting(enable: Boolean): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            appContext.dataStore.edit { preferences ->
                preferences[DAILY_REMINDERS_ENABLED_KEY] = enable
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to update reminder setting: ${e.localizedMessage}"))
        }
    }

    override fun getOnboardingStatus(): Flow<Resource<Boolean>> = appContext.dataStore.data
        .map { preferences ->
            Resource.Success(preferences[ONBOARDING_COMPLETE_KEY] ?: false)
        }
        .catch { exception ->
            if (exception is IOException) {
                emit(Resource.Error("Error reading onboarding status: ${exception.localizedMessage}"))
            }
            else {
                emit(Resource.Error("An unknown error occurred: ${exception.localizedMessage}"))
            }
        }

    override fun setOnboardingComplete(isComplete: Boolean): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            appContext.dataStore.edit { preferences ->
                preferences[ONBOARDING_COMPLETE_KEY] = isComplete
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to set onboarding status: ${e.localizedMessage}"))
        }
    }
}