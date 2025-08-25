package com.iries.alarm.di

import android.content.Context
import com.iries.alarm.data.local.AuthStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthStoreModule {

    @Provides
    @Singleton
    fun provideAuthStore(@ApplicationContext appContext: Context): AuthStore {
        return AuthStore(appContext)
    }
}