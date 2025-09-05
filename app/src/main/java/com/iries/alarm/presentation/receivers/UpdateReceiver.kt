package com.iries.alarm.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.iries.alarm.domain.usecases.AlarmsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Android removes alarms associated with the app on any uninstallation,
 * including app updates */

@AndroidEntryPoint
class UpdateReceiver : BroadcastReceiver() {
    @Inject
    lateinit var alarmsUseCase: AlarmsUseCase

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            intent.action == Intent.ACTION_BOOT_COMPLETED
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                alarmsUseCase.rescheduleAlarms()
            }
        }
    }
}