package com.iries.youtubealarm.data.database

import androidx.room.RoomDatabase
import com.iries.youtubealarm.data.dao.AlarmsDao
import com.iries.youtubealarm.data.dao.ChannelsDao
import com.iries.youtubealarm.data.entity.AlarmInfo
import com.iries.youtubealarm.data.entity.YTChannel

@androidx.room.Database(entities = [AlarmInfo::class, YTChannel::class], version = 1)
abstract class UserDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmsDao

    abstract fun channelsDao(): ChannelsDao
}