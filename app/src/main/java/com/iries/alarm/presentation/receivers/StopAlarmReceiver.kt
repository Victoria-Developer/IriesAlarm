package com.iries.alarm.presentation.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.iries.alarm.presentation.services.NotificationService
import com.iries.alarm.presentation.services.RingtonePlaybackService

class StopAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val ringtonePlaybackServiceIntent = Intent(context, RingtonePlaybackService::class.java)
        context.stopService(ringtonePlaybackServiceIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NotificationService.NOTIFICATION_CODE)
    }

}