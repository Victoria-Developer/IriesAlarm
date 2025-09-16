package com.iries.alarm.data.local.database

import androidx.room.RoomDatabase
import com.iries.alarm.data.local.dao.AlarmsDao
import com.iries.alarm.data.local.dao.ArtistsDao
import com.iries.alarm.data.local.entity.AlarmEntity
import com.iries.alarm.data.local.entity.ArtistEntity

@androidx.room.Database(entities = [AlarmEntity::class, ArtistEntity::class], version = 3)
abstract class UserDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmsDao

    abstract fun artistsDao(): ArtistsDao
}