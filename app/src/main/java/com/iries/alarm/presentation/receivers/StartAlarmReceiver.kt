package com.iries.alarm.presentation.receivers

import android.app.AlarmManager.INTERVAL_DAY
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.iries.alarm.domain.usecases.AlarmUseCase
import com.iries.alarm.domain.constants.Extra
import com.iries.alarm.presentation.services.RingtonePlaybackService

class StartAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId1 = intent.getIntExtra(Extra.ALARM_ID.extraName, 0)
        println("Launching alarm with id $alarmId1")

        val isRepeating = intent
            .getBooleanExtra(Extra.IS_ALARM_REPEATING.extraName, false)
        if (isRepeating) {
            val timeInMillis = intent.getLongExtra(Extra.ALARM_TIME.extraName, 0)
            val alarmId = intent.getIntExtra(Extra.ALARM_ID.extraName, 0)
            AlarmUseCase.setAlarm(
                context,
                timeInMillis + INTERVAL_DAY * 7, alarmId, true
            )
        }

        val startIntent = Intent(context, RingtonePlaybackService::class.java)
        context.startService(startIntent)
    }

}