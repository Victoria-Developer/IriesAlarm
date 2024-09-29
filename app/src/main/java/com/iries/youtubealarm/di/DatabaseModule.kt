package com.iries.youtubealarm.di

import android.content.Context
import androidx.room.Room
import com.iries.youtubealarm.data.database.UserDatabase
import com.iries.youtubealarm.data.dao.AlarmsDao
import com.iries.youtubealarm.data.dao.ChannelsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAlarmDao(menuDatabase: UserDatabase): AlarmsDao {
        return menuDatabase.alarmDao()
    }

    @Provides
    @Singleton
    fun provideChannelsDao(menuDatabase: UserDatabase): ChannelsDao {
        return menuDatabase.channelsDao()
    }

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context?): UserDatabase {
        return Room.databaseBuilder(
            context!!,
            UserDatabase::class.java,
            "USER_DATA.DB"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}