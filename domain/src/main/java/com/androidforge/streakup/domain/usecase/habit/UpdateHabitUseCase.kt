package com.androidforge.streakup.domain.usecase.habit

import com.androidforge.streakup.core.common.Resource
import com.androidforge.streakup.domain.model.Habit
import com.androidforge.streakup.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(habit: Habit): Flow<Resource<Unit>> {
        return repository.updateHabit(habit)
    }
}