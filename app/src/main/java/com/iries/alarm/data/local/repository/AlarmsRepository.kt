package com.iries.alarm.data.local.repository

import com.iries.alarm.data.local.dao.AlarmsDao
import com.iries.alarm.data.local.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmsRepository @Inject constructor(private var alarmsDao: AlarmsDao) {
    private var allAlarms: Flow<List<AlarmEntity>> = alarmsDao.getAllAlarms()

    fun insert(alarmEntity: AlarmEntity) : Long{
       return alarmsDao.insert(alarmEntity)
    }

    fun update(alarmEntity: AlarmEntity) {
        alarmsDao.update(alarmEntity)
    }

    fun delete(alarmEntity: AlarmEntity) {
        alarmsDao.delete(alarmEntity)
    }

    fun deleteAll() {
        alarmsDao.deleteAll()
    }

    fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return allAlarms
    }
}