package com.iries.alarm.presentation.receivers

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
            val hour = intent.getIntExtra(Extra.ALARM_HOUR.extraName, 0)
            val minute = intent.getIntExtra(Extra.ALARM_MINUTE.extraName, 0)
            val day = intent.getIntExtra(Extra.ALARM_DAY.extraName, 0)
            val code = intent.getIntExtra(Extra.ALARM_CODE.extraName, 0)
            alarmsUseCase.setAlarm(
                requestCode = code, hour = hour, minute = minute, dayId = day
            )
        }
        Log.d("StartAlarmReceiver", "Started foreground service")
        context.startForegroundService(Intent(context, AlarmService::class.java))
    }

}