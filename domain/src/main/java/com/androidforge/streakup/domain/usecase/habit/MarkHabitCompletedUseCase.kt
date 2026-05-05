package com.androidforge.streakup.domain.usecase.habit

import com.androidforge.streakup.core.common.Resource
import com.androidforge.streakup.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class MarkHabitCompletedUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    // Default to today if date is not provided
    operator fun invoke(habitId: Long, isCompleted: Boolean, date: LocalDate = LocalDate.now()): Flow<Resource<Unit>> {
        return repository.markHabitCompleted(habitId, date, isCompleted)
    }
}