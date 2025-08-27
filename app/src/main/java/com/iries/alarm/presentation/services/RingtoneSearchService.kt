package com.iries.alarm.presentation.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
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
        startForeground(333, buildNotification())
        showAlarmActivity()
        return START_STICKY
    }

    private fun buildNotification(): Notification {
        val channelId = "ringtone_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId, "Ringtone Service",
            NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
        }
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Iries Alarm")
            .setContentText("Your alarm will be fired soon.")
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()
    }

    private fun showAlarmActivity() = serviceScope.launch(Dispatchers.Main) {
        val ringtoneInfo = withContext(Dispatchers.IO) {
            searchApiUseCase.findRandomRingtone()
        }

       val alarmIntent = Intent(this@RingtoneSearchService, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Extra.RINGTONE_URI_EXTRA.extraName, ringtoneInfo.trackUri)
            putExtra(Extra.RINGTONE_NAME_EXTRA.extraName, ringtoneInfo.trackTitle)
            putExtra(Extra.RINGTONE_AUTHOR_EXTRA.extraName, ringtoneInfo.artistName)
        }

        val pendingIntent = PendingIntent.getActivity(
            this@RingtoneSearchService, 0, alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            this@RingtoneSearchService, "alarm_channel"
        )
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setFullScreenIntent(pendingIntent, true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()

        getSystemService(NotificationManager::class.java)
            .notify(444, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

}