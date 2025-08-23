package com.iries.youtubealarm.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.iries.youtubealarm.data.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmsDao {
    @Insert
    fun insert(alarmEntity: AlarmEntity) : Long

    @Delete
    fun delete(alarmEntity: AlarmEntity)

    @Update
    fun update(alarmEntity: AlarmEntity)

    @Query("DELETE FROM ALARMS")
    fun deleteAll()

    @Query("SELECT * FROM ALARMS")
    fun getAllAlarms(): Flow<List<AlarmEntity>>
}