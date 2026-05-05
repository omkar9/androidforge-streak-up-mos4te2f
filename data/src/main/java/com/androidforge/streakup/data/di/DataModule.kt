package com.androidforge.streakup.data.di

import android.content.Context
import androidx.room.Room
import com.androidforge.streakup.core.common.Constants
import com.androidforge.streakup.data.local.dao.HabitCompletionDao
import com.androidforge.streakup.data.local.dao.HabitDao
import com.androidforge.streakup.data.local.database.AppDatabase
import com.androidforge.streakup.data.repository.HabitRepositoryImpl
import com.androidforge.streakup.domain.repository.HabitRepository
import com.androidforge.streakup.domain.usecase.streak.CalculateStreaksUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(database: AppDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    @Singleton
    fun provideHabitCompletionDao(database: AppDatabase): HabitCompletionDao {
        return database.habitCompletionDao()
    }

    @Provides
    @Singleton
    fun provideHabitRepository(
        habitDao: HabitDao,
        habitCompletionDao: HabitCompletionDao,
        calculateStreaksUseCase: CalculateStreaksUseCase,
        @ApplicationContext appContext: Context
    ): HabitRepository {
        return HabitRepositoryImpl(habitDao, habitCompletionDao, calculateStreaksUseCase, appContext)
    }

    // Since CalculateStreaksUseCase has no external dependencies, Hilt can directly inject it.
    // However, explicitly providing it here makes the graph clearer.
    @Provides
    fun provideCalculateStreaksUseCase(): CalculateStreaksUseCase {
        return CalculateStreaksUseCase()
    }
}