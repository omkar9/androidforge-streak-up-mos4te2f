package com.androidforge.streakup.di

import android.content.Context
import androidx.work.WorkManager
import com.androidforge.streakup.presentation.admob.AdMobManager
import com.androidforge.streakup.presentation.notifications.NotificationScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideNotificationScheduler(@ApplicationContext context: Context): NotificationScheduler {
        return NotificationScheduler(context)
    }

    @Provides
    @Singleton
    fun provideAdMobManager(@ApplicationContext context: Context): AdMobManager {
        return AdMobManager(context)
    }
}