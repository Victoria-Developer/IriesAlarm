package com.iries.youtubealarm.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.iries.youtubealarm.data.entity.AlarmInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmsDao {
    @Insert
    fun insert(alarmInfo: AlarmInfo) : Long

    @Delete
    fun delete(alarmInfo: AlarmInfo)

    @Update
    fun update(alarmInfo: AlarmInfo)

    @Query("DELETE FROM ALARMS")
    fun deleteAll()

    @Query("SELECT * FROM ALARMS")
    fun getAllAlarms(): Flow<List<AlarmInfo>>
}