package com.iries.youtubealarm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.iries.youtubealarm.data.entity.AlarmInfo

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
    fun getAllAlarms(): LiveData<List<AlarmInfo>>
}