package com.androidforge.streakup.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.androidforge.streakup.core.common.Constants
import com.androidforge.streakup.core.common.DateUtils
import com.androidforge.streakup.core.notifications.NotificationHelper
import com.androidforge.streakup.domain.repository.HabitRepository
import com.androidforge.streakup.presentation.ui.add_edit_habit.AddEditHabitEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationHelper: NotificationHelper,
    private val habitRepository: HabitRepository // To check if reminders are enabled globally
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("ReminderWorker started.")

        // Check global reminder settings
        val appSettings = habitRepository.getAppSettings().firstOrNull()?.data
        if (appSettings?.areDailyRemindersEnabled == false) {
            Timber.d("Daily reminders are globally disabled. Skipping habit reminder.")
            return Result.success()
        }

        val habitId = inputData.getLong(Constants.KEY_HABIT_ID, -1L)
        val habitName = inputData.getString(Constants.KEY_HABIT_NAME)
        val reminderHour = inputData.getInt(Constants.KEY_REMINDER_HOUR, -1)
        val reminderMinute = inputData.getInt(Constants.KEY_REMINDER_MINUTE, -1)
        val frequencyTypeStr = inputData.getString(Constants.KEY_FREQUENCY_TYPE)
        val frequencyValue = inputData.getString(Constants.KEY_FREQUENCY_VALUE)

        if (habitId == -1L || habitName == null || reminderHour == -1 || reminderMinute == -1 || frequencyTypeStr == null) {
            Timber.e("ReminderWorker: Invalid input data for habit ID: $habitId, name: $habitName, time: $reminderHour:$reminderMinute, freq: $frequencyTypeStr")
            return Result.failure()
        }

        val frequencyType = AddEditHabitEvent.FrequencyType.valueOf(frequencyTypeStr)
        val currentTime = LocalTime.now(ZoneId.systemDefault())
        val currentLocalDate = LocalDate.now(ZoneId.systemDefault())
        val targetReminderTime = LocalTime.of(reminderHour, reminderMinute)

        // Check if it's the right time to show the notification (within a window, e.g., +/- 15 mins)
        // This worker runs daily, so it acts as a daily check for the target reminder time.
        // It's not an exact alarm, but rather a daily check if the reminder should be shown.
        // For exact alarms, SCHEDULE_EXACT_ALARM permission and AlarmManager are needed.
        // Given the prompt, WorkManager is for scheduling, NotificationScheduler handles the specific time logic.
        // ReminderWorker's job is to *trigger* the notification if conditions (date, time) are met.

        val isWithinTimeWindow = currentTime.isAfter(targetReminderTime.minusMinutes(15)) &&
                                 currentTime.isBefore(targetReminderTime.plusMinutes(15))

        if (!isWithinTimeWindow) {
            Timber.d("ReminderWorker for habit $habitId: Not within time window ($targetReminderTime). Current time: $currentTime.")
            return Result.success() // Not the right time, but not a failure.
        }

        // Check frequency
        val shouldRemindToday = when (frequencyType) {
            AddEditHabitEvent.FrequencyType.DAILY -> true
            AddEditHabitEvent.FrequencyType.WEEKLY -> {
                val selectedDays = frequencyValue?.split(",")
                    ?.mapNotNull { it.toIntOrNull() }
                    ?.map { DateUtils.intToDayOfWeek(it) } // Convert 0=Sun, 1=Mon to DayOfWeek enum
                    ?.toSet() ?: emptySet()
                selectedDays.contains(currentLocalDate.dayOfWeek)
            }
        }

        if (shouldRemindToday) {
            // Check if habit is already completed for today
            val habitDetailsResource = habitRepository.getHabitById(habitId).firstOrNull()
            val habit = habitDetailsResource?.data

            if (habit != null) {
                if (!habit.isCompletedToday) {
                    Timber.d("Showing reminder for habit: $habitName (ID: $habitId)")
                    notificationHelper.showHabitReminderNotification(habitId, habitName)
                } else {
                    Timber.d("Habit $habitName (ID: $habitId) already completed today. No reminder needed.")
                }
            } else {
                Timber.e("ReminderWorker: Habit $habitId not found in repository.")
                return Result.failure() // Habit might have been deleted
            }
        } else {
            Timber.d("ReminderWorker for habit $habitId: Today (${currentLocalDate.dayOfWeek}) is not an eligible day based on frequency.")
        }

        return Result.success()
    }
}