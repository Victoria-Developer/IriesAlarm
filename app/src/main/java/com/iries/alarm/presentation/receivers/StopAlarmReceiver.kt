package com.iries.alarm.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.iries.alarm.presentation.services.RingtonePlaybackService

class StopAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val ringtonePlaybackServiceIntent = Intent(context, RingtonePlaybackService::class.java)
        context.stopService(ringtonePlaybackServiceIntent)
    }

}