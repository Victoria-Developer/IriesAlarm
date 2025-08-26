package com.iries.alarm.presentation.receivers

import android.app.AlarmManager.INTERVAL_DAY
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.iries.alarm.domain.usecases.AlarmUseCase
import com.iries.alarm.domain.constants.Extra
import com.iries.alarm.presentation.services.RingtoneSearchService

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isRepeating = intent
            .getBooleanExtra(Extra.IS_ALARM_REPEATING.extraName, false)
        if (isRepeating) {
            val timeInMillis = intent.getLongExtra(Extra.ALARM_TIME.extraName, 0)
            val alarmId = intent.getIntExtra(Extra.ALARM_ID.extraName, 0)
            AlarmUseCase.setOneshotAlarm(
                context,
                timeInMillis + INTERVAL_DAY * 7, alarmId, true
            )
        }

        val startIntent = Intent(
            context,
            RingtoneSearchService::class.java
        )
        context.startForegroundService(startIntent)
    }

}