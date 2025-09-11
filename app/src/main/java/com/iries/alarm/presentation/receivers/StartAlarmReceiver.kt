package com.iries.alarm.presentation.receivers

import android.app.AlarmManager.INTERVAL_DAY
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.iries.alarm.domain.constants.Extra
import com.iries.alarm.domain.usecases.AlarmsUseCase
import com.iries.alarm.presentation.services.AlarmService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/** From Android 4.4 (API Level 19) onward, all repeating alarms are considered Inexact.
 * Developers have to implement exact repeating alarms by themselves. */

@AndroidEntryPoint
class StartAlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var alarmsUseCase: AlarmsUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val isRepeating = intent
            .getBooleanExtra(Extra.IS_ALARM_REPEATING.extraName, false)
        if (isRepeating) {
            val timeInMillis = intent.getLongExtra(Extra.ALARM_TIME.extraName, 0)
            val alarmId = intent.getIntExtra(Extra.ALARM_ID.extraName, 0)
            alarmsUseCase.setAlarm(
                timeInMillis + INTERVAL_DAY * 7, alarmId, true
            )
        }
        Log.d("StartAlarmReceiver", "Started foreground service")
        context.startForegroundService(Intent(context, AlarmService::class.java))
    }

}