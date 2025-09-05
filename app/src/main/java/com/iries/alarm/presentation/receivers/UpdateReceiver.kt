package com.iries.alarm.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.iries.alarm.data.local.repository.AlarmsRepository
import com.iries.alarm.domain.usecases.AlarmUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UpdateReceiver : BroadcastReceiver() {
    @Inject
    lateinit var alarmsRepo: AlarmsRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            intent.action == Intent.ACTION_BOOT_COMPLETED
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                val alarms = alarmsRepo.getAllAlarms().first()
                for (alarm in alarms) {
                    if (!alarm.isActive) continue
                    alarm.days.forEach { (dayId, requestCode) ->
                        AlarmUseCase.cancelIntent(requestCode, context)
                        AlarmUseCase.setRepeatingAlarm(
                            context = context,
                            hour = alarm.hour,
                            minute = alarm.minute,
                            dayId = dayId,
                            requestCode = requestCode
                        )
                    }
                }
            }
        }
    }
}