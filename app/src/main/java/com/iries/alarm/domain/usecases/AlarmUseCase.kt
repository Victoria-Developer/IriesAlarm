package com.iries.alarm.domain.usecases

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.iries.alarm.domain.constants.Extra
import com.iries.alarm.presentation.receivers.AlarmReceiver
import java.util.Calendar

object AlarmUseCase {

    fun setRepeatingAlarm(
        context: Context,
        hour: Int, minute: Int,
        dayId: Int, requestCode: Int
    ) {
        val currentCalendar = Calendar.getInstance()
        val currentDay = currentCalendar[Calendar.DAY_OF_WEEK]

        val alarmCalendar = Calendar.getInstance()
        alarmCalendar[Calendar.HOUR_OF_DAY] = hour
        alarmCalendar[Calendar.MINUTE] = minute
        alarmCalendar[Calendar.DAY_OF_WEEK] = dayId
        // Important, to avoid alarm seconds and millis precise time
        alarmCalendar.set(Calendar.SECOND, 0)
        alarmCalendar.set(Calendar.MILLISECOND, 0)

        val timeInMillis: Long
        if (alarmCalendar.before(currentCalendar)) {
            val days = currentDay + (7 - dayId)
            currentCalendar.add(Calendar.DATE, days)
            timeInMillis = currentCalendar.timeInMillis
        } else
            timeInMillis = alarmCalendar.timeInMillis

        setOneshotAlarm(context, timeInMillis, requestCode, true)
    }

    fun setOneshotAlarm(
        context: Context,
        timeInMillis: Long,
        requestCode: Int,
        isRepeating: Boolean
    ) {
        val flags =
            if (isRepeating) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE

        val nextAlarmIntent = Intent(context, AlarmReceiver::class.java)
        nextAlarmIntent.putExtra(Extra.ALARM_TIME.extraName, timeInMillis)
        nextAlarmIntent.putExtra(Extra.ALARM_ID.extraName, requestCode)
        nextAlarmIntent.putExtra(Extra.IS_ALARM_REPEATING.extraName, isRepeating)

        val pendingIntent = PendingIntent
            .getBroadcast(context, requestCode, nextAlarmIntent, flags)

        val alarmManager = context
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis, pendingIntent
        )
    }

    fun cancelIntent(requestCode: Int, context: Context) {
        val flags = PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent
            .getBroadcast(context, requestCode, alarmIntent, flags)
        val alarmManager = context
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

}