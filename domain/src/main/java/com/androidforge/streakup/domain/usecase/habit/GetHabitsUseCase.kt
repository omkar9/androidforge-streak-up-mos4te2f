package com.androidforge.streakup.domain.usecase.habit

import com.androidforge.streakup.core.common.Resource
import com.androidforge.streakup.domain.model.Habit
import com.androidforge.streakup.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<Resource<List<Habit>>> {
        return repository.getAllHabits()
    }
}