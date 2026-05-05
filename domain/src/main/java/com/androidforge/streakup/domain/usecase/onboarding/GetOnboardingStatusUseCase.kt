package com.androidforge.streakup.domain.usecase.onboarding

import com.androidforge.streakup.core.common.Resource
import com.androidforge.streakup.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetOnboardingStatusUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.getOnboardingStatus().map {
            when (it) {
                is Resource.Success -> it.data ?: false
                is Resource.Error -> {
                    // Log error but default to false to show onboarding if status can't be retrieved
                    // This prevents getting stuck in a loading state or skipping onboarding unintentionally.
                    false
                }
                else -> false // Loading or other states, assume not complete yet
            }
        }
    }
}