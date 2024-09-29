package com.iries.youtubealarm.data.repository

import androidx.lifecycle.LiveData
import com.iries.youtubealarm.data.dao.AlarmsDao
import com.iries.youtubealarm.data.entity.AlarmInfo
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class AlarmsRepository @Inject constructor(private var alarmsDao: AlarmsDao) {
    private var allAlarms: LiveData<List<AlarmInfo>> = alarmsDao.getAllAlarms()

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

    fun getAllAlarms(): LiveData<List<AlarmInfo>> {
        return allAlarms
    }
}