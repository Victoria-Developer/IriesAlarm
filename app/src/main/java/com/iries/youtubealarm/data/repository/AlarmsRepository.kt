package com.iries.youtubealarm.data.repository

import com.iries.youtubealarm.data.dao.AlarmsDao
import com.iries.youtubealarm.data.entity.AlarmInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmsRepository @Inject constructor(private var alarmsDao: AlarmsDao) {
    private var allAlarms: Flow<List<AlarmInfo>> = alarmsDao.getAllAlarms()

    fun insert(alarmInfo: AlarmInfo) : Long{
       return alarmsDao.insert(alarmInfo)
    }

    fun update(alarmInfo: AlarmInfo) {
        alarmsDao.update(alarmInfo)
    }

    fun delete(alarmInfo: AlarmInfo) {
        alarmsDao.delete(alarmInfo)
    }

    fun deleteAll() {
        alarmsDao.deleteAll()
    }

    fun getAllAlarms(): Flow<List<AlarmInfo>> {
        return allAlarms
    }
}