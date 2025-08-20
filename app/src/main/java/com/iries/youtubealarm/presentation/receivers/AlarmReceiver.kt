package com.iries.youtubealarm.presentation.receivers

import android.app.AlarmManager.INTERVAL_DAY
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.iries.youtubealarm.domain.AlarmManager
import com.iries.youtubealarm.domain.constants.Extra
import com.iries.youtubealarm.presentation.services.RingtoneSearchService

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isRepeating = intent
            .getBooleanExtra(Extra.IS_ALARM_REPEATING.extraName, false)
        if (isRepeating) {
            val timeInMillis = intent.getLongExtra(Extra.ALARM_TIME.extraName, 0)
            val alarmId = intent.getIntExtra(Extra.ALARM_ID.extraName, 0)
            AlarmManager.setAlarm(
                context,
                timeInMillis + INTERVAL_DAY * 7, alarmId, true
            )
        }

        val startIntent = Intent(
            context,
            RingtoneSearchService::class.java
        )
        context.startService(startIntent)
    }

}