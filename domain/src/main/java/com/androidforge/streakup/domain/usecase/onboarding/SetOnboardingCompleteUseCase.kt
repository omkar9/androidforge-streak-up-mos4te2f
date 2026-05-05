package com.androidforge.streakup.domain.usecase.onboarding

import com.androidforge.streakup.core.common.Resource
import com.androidforge.streakup.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetOnboardingCompleteUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<Resource<Unit>> {
        return repository.setOnboardingComplete(true)
    }
}