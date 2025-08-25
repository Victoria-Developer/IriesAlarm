package com.iries.alarm.di

import android.content.Context
import com.iries.alarm.AlarmApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideBaseApplication(@ApplicationContext context: Context?): AlarmApp? {
        return context as AlarmApp?
    }
}