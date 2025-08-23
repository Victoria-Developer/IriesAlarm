package com.iries.youtubealarm.data.database

import androidx.room.RoomDatabase
import com.iries.youtubealarm.data.dao.AlarmsDao
import com.iries.youtubealarm.data.dao.ArtistsDao
import com.iries.youtubealarm.data.entity.AlarmEntity
import com.iries.youtubealarm.data.entity.ArtistEntity

@androidx.room.Database(entities = [AlarmEntity::class, ArtistEntity::class], version = 2)
abstract class UserDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmsDao

    abstract fun artistsDao(): ArtistsDao
}