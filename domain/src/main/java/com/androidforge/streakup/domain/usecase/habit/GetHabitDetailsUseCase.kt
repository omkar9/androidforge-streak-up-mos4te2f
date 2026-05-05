package com.androidforge.streakup.domain.usecase.habit

import com.androidforge.streakup.core.common.Resource
import com.androidforge.streakup.domain.model.Habit
import com.androidforge.streakup.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitDetailsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(habitId: Long): Flow<Resource<Habit?>> {
        return repository.getHabitById(habitId)
    }
}