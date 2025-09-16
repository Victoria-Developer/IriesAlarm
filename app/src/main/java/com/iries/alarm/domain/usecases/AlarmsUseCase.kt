package com.iries.alarm.domain.usecases

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.iries.alarm.data.local.repository.AlarmsRepository
import com.iries.alarm.domain.constants.Extra
import com.iries.alarm.domain.models.Alarm
import com.iries.alarm.presentation.receivers.StartAlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject


class AlarmsUseCase @Inject constructor(
    private val alarmsRepository: AlarmsRepository,
    @ApplicationContext private val context: Context
) {

    private val alarmAction = "manage alarm"

    fun getAllAlarms(): Flow<List<Alarm>> {
        return alarmsRepository.getAllAlarms()
    }

    fun addAlarm(alarm: Alarm) {
        alarmsRepository.insert(alarm)
    }

    fun updateAlarm(alarm: Alarm) {
        alarmsRepository.update(alarm)
    }

    fun removeAlarm(alarm: Alarm) {
        alarmsRepository.delete(alarm)
        cancelAlarm(alarm)
    }

    suspend fun rescheduleAlarms() {
        val alarms = alarmsRepository.getAllAlarms().first()
        for (alarm in alarms) {
            if (!alarm.isActive) continue
            cancelAlarm(alarm)
            for (dayCode in alarm.days) {
                setAlarm(
                    alarm.hour, alarm.minute, dayCode.key, dayCode.value
                )
            }
        }
    }

    fun cancelAlarm(alarm: Alarm) {
        for (dayCode in alarm.days) {
            val pendingIntent = buildAlarmPendingIntent(
                requestCode = dayCode.value,
                hour = alarm.hour,
                minute = alarm.minute,
                dayId = dayCode.key
            )
            val alarmManager = context
                .getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }

    fun setAlarm(hour: Int, minute: Int, dayId: Int, requestCode: Int) {
        val alarmTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.DAY_OF_WEEK, dayId)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (alarmTime.timeInMillis <= Calendar.getInstance().timeInMillis) {
            alarmTime.add(Calendar.WEEK_OF_YEAR, 1)
        }
        val timeInMillis = alarmTime.timeInMillis

        val pendingIntent = buildAlarmPendingIntent(
            requestCode = requestCode,
            hour = hour,
            minute = minute,
            dayId = dayId
        )

        val alarmManager = context
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis, pendingIntent
        )
    }

    private fun buildAlarmPendingIntent(
        requestCode: Int,
        hour: Int,
        minute: Int,
        dayId: Int,
        isRepeating: Boolean = true
    ): PendingIntent {
        val intent = Intent(context, StartAlarmReceiver::class.java).apply {
            putExtra(Extra.ALARM_HOUR.extraName, hour)
            putExtra(Extra.ALARM_MINUTE.extraName, minute)
            putExtra(Extra.ALARM_DAY.extraName, dayId)
            putExtra(Extra.ALARM_CODE.extraName, requestCode)
            putExtra(Extra.IS_ALARM_REPEATING.extraName, isRepeating)
            action = alarmAction
        }

        val flags = if (isRepeating) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        }

        return PendingIntent.getBroadcast(context, requestCode, intent, flags)
    }

}