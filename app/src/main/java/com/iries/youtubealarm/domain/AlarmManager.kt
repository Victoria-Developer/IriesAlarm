package com.iries.youtubealarm.domain

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.iries.youtubealarm.data.entity.AlarmInfo
import com.iries.youtubealarm.domain.constants.Day
import com.iries.youtubealarm.domain.constants.Extra
import com.iries.youtubealarm.presentation.receivers.AlarmReceiver
import com.iries.youtubealarm.presentation.services.RingtonePlayingService
import java.util.Calendar

object AlarmManager {

    fun setRepeatingAlarm(
        context: Context,
        alarm: AlarmInfo, day: Day
    ) {
        val hour: Int = alarm.getHour()
        val minute: Int = alarm.getMinute()
        val chosenDay: Int = day.getId()

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

        /** As audio links are short-lived it's impossible to cache the media
            without storing it in own server or user's storage.
            Temporary workaround is to fire alarm itself 5 seconds earlier. **/
        setAlarm(context, timeInMillis - 5000, code, true)
    }

    fun setAlarm(
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

    fun stopCurrentAlarm(context: Context) {
        val stopIntent = Intent(context, RingtonePlayingService::class.java)
        context.stopService(stopIntent)
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