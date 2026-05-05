package com.androidforge.streakup.domain.usecase.settings

import com.androidforge.streakup.core.common.Resource
import com.androidforge.streakup.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateDailyRemindersSettingUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(enable: Boolean): Flow<Resource<Unit>> {
        return repository.updateDailyRemindersSetting(enable)
    }
}