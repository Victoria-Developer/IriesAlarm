package com.iries.alarm.data.local.repository

import com.iries.alarm.data.local.dao.AlarmsDao
import com.iries.alarm.data.local.entity.AlarmEntity
import com.iries.alarm.domain.models.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AlarmsRepository @Inject constructor(private var alarmsDao: AlarmsDao) {
    private var allAlarms: Flow<List<AlarmEntity>> = alarmsDao.getAllAlarms()
    private val json = Json { ignoreUnknownKeys = true }

    fun insert(alarm: Alarm) {
        alarmsDao.insert(dtoToAlarmEntity(alarm))
    }

    fun update(alarm: Alarm) {
        alarmsDao.update(dtoToAlarmEntity(alarm))
    }

    fun delete(alarm: Alarm) {
        alarmsDao.delete(dtoToAlarmEntity(alarm))
    }

    fun deleteAll() {
        alarmsDao.deleteAll()
    }

    fun getAllAlarms(): Flow<List<Alarm>> {
        return allAlarms.map { alarmEntities ->
            alarmEntities.map { alarmEntity ->
                alarmEntityToDto(alarmEntity)
            }
        }
    }

    private fun alarmEntityToDto(alarmEntity: AlarmEntity): Alarm {
        return Alarm(
            id = alarmEntity.id,
            days = json.decodeFromString(alarmEntity.days),
            isActive = alarmEntity.isActive,
            hour = alarmEntity.hour,
            minute = alarmEntity.minute,
            isRepeating = alarmEntity.isRepeating
        )
    }

    private fun dtoToAlarmEntity(alarm: Alarm): AlarmEntity {
        return AlarmEntity().apply {
            id = alarm.id
            days = json.encodeToString(alarm.days)
            isActive = alarm.isActive
            hour = alarm.hour
            minute = alarm.minute
            isRepeating = alarm.isRepeating
        }
    }
}