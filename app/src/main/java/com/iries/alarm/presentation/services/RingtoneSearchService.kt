package com.iries.alarm.presentation.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.iries.alarm.R
import com.iries.alarm.domain.constants.Extra
import com.iries.alarm.domain.usecases.SearchApiUseCase
import com.iries.alarm.presentation.activities.AlarmActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class RingtoneSearchService : Service() {
    private var serviceScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    @Inject
    lateinit var searchApiUseCase: SearchApiUseCase

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(333, showNotification())
        showAlarmActivity()
        return START_STICKY
    }

    private fun showNotification(): Notification {
        val channelId = "alarm_channel"
        val channel = NotificationChannel(
            channelId,
            "Iries Alarm",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Your alarm will ring soon.")
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun showAlarmActivity() = serviceScope.launch {
        val ringtoneInfo = withContext(Dispatchers.IO) {
            searchApiUseCase.findRandomRingtone()
        }
        val alarmActivityIntent = Intent(
            this@RingtoneSearchService, AlarmActivity::class.java
        ).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Extra.RINGTONE_URI_EXTRA.extraName, ringtoneInfo.trackUri)
            putExtra(Extra.RINGTONE_NAME_EXTRA.extraName, ringtoneInfo.trackTitle)
            putExtra(Extra.RINGTONE_AUTHOR_EXTRA.extraName, ringtoneInfo.artistName)
        }
        startActivity(alarmActivityIntent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

}