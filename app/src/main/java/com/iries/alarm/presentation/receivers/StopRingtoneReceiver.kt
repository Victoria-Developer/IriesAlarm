package com.iries.alarm.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.iries.alarm.presentation.services.RingtoneService

class StopRingtoneReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, RingtoneService::class.java)
        context.stopService(serviceIntent)
    }

}