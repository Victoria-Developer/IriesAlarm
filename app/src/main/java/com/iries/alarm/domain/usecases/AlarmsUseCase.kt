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
            activateAlarm(alarm)
        }
    }

    fun cancelAlarm(
        alarm: Alarm,
        requestCodes: MutableCollection<Int> = alarm.days.values
    ) {
        alarm.isActive = false
        for (code in requestCodes) {
            val flags = PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            val alarmIntent = Intent(context, StartAlarmReceiver::class.java).apply {
                action = alarmAction
            }
            val pendingIntent = PendingIntent
                .getBroadcast(context, code, alarmIntent, flags)
            val alarmManager = context
                .getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }

    fun activateAlarm(alarm: Alarm) {
        alarm.isActive = true

        for (dayCode in alarm.days) {
            val currentTime = Calendar.getInstance()

            val alarmTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, alarm.hour)
                set(Calendar.MINUTE, alarm.minute)
                set(Calendar.DAY_OF_WEEK, dayCode.key)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (alarmTime.timeInMillis <= currentTime.timeInMillis) {
                alarmTime.add(Calendar.WEEK_OF_YEAR, 1)
            }
            val timeInMillis: Long = alarmTime.timeInMillis
            setAlarm(timeInMillis, dayCode.value, true)
        }
    }

    fun setAlarm(
        timeInMillis: Long,
        requestCode: Int,
        isRepeating: Boolean
    ) {
        val flags =
            if (isRepeating) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE

        val nextAlarmIntent = Intent(context, StartAlarmReceiver::class.java).apply {
            putExtra(Extra.ALARM_TIME.extraName, timeInMillis)
            putExtra(Extra.ALARM_ID.extraName, requestCode)
            putExtra(Extra.IS_ALARM_REPEATING.extraName, isRepeating)
            action = alarmAction
        }

        val pendingIntent = PendingIntent
            .getBroadcast(context, requestCode, nextAlarmIntent, flags)

        val alarmManager = context
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis, pendingIntent
        )
    }

}