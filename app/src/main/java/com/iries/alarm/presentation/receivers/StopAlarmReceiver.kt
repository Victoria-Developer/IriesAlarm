package com.iries.alarm.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.iries.alarm.presentation.services.AlarmService

class StopAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val alarmServiceIntent = Intent(context, AlarmService::class.java)
        context.stopService(alarmServiceIntent)

      /* val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NotificationService.NOTIFICATION_CODE)*/
    }

}