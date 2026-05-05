package com.androidforge.streakup.domain.usecase.settings

import com.androidforge.streakup.core.common.Resource
import com.androidforge.streakup.domain.repository.HabitRepository
import com.androidforge.streakup.presentation.ui.settings.AppSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppSettingsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<Resource<AppSettings>> {
        return repository.getAppSettings()
    }
}