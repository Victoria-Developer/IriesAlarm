package com.iries.alarm.domain.usecases

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.iries.alarm.data.local.entity.AlarmEntity
import com.iries.alarm.domain.constants.Day
import com.iries.alarm.domain.constants.Extra
import com.iries.alarm.presentation.receivers.AlarmReceiver
import java.util.Calendar

object AlarmUseCase {

    fun setRepeatingAlarm(
        context: Context,
        alarm: AlarmEntity, day: Day
    ) {
        val hour: Int = alarm.getHour()
        val minute: Int = alarm.getMinute()
        val chosenDay: Int = day.id

        val currentCalendar = Calendar.getInstance()
        val currentDay = currentCalendar[Calendar.DAY_OF_WEEK]

        val alarmCalendar = Calendar.getInstance()
        alarmCalendar[Calendar.HOUR_OF_DAY] = hour
        alarmCalendar[Calendar.MINUTE] = minute
        alarmCalendar[Calendar.DAY_OF_WEEK] = chosenDay
        alarmCalendar.set(Calendar.SECOND, 0)
        alarmCalendar.set(Calendar.MILLISECOND, 0)

        val timeInMillis: Long
        if (alarmCalendar.before(currentCalendar)) {
            val days = currentDay + (7 - chosenDay)
            currentCalendar.add(Calendar.DATE, days)
            timeInMillis = currentCalendar.timeInMillis
        } else
            timeInMillis = alarmCalendar.timeInMillis

        // alarm code
        val days = alarm.getDaysId()
        val alarmId: Long = alarm.getAlarmId()
        val code = "$alarmId${chosenDay}".toInt()
        days[day] = code
        setOneshotAlarm(context, timeInMillis, code, true)
    }

    fun setOneshotAlarm(
        context: Context,
        timeInMillis: Long,
        fullAlarmId: Int,
        isRepeating: Boolean
    ) {
        val flags =
            if (isRepeating) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE

        val nextAlarmIntent = Intent(context, AlarmReceiver::class.java)
        nextAlarmIntent.putExtra(Extra.ALARM_TIME.extraName, timeInMillis)
        nextAlarmIntent.putExtra(Extra.ALARM_ID.extraName, fullAlarmId)
        nextAlarmIntent.putExtra(Extra.IS_ALARM_REPEATING.extraName, isRepeating)

        val pendingIntent = PendingIntent
            .getBroadcast(context, fullAlarmId, nextAlarmIntent, flags)

        val alarmManager = context
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis, pendingIntent
        )
    }

    fun cancelIntent(intentId: Int, context: Context) {
        val flags = PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent
            .getBroadcast(context, intentId, alarmIntent, flags)
        val alarmManager = context
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

}