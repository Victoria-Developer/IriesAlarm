package com.iries.alarm.presentation.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.iries.alarm.R
import com.iries.alarm.domain.constants.Extra
import com.iries.alarm.domain.usecases.SearchApiUseCase
import com.iries.alarm.presentation.receivers.StopRingtoneReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : Service() {
    private var serviceScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private val manager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Inject
    lateinit var searchApiUseCase: SearchApiUseCase

    companion object {
        const val WARNING_CHANNEL = "warning_channel"
        const val MAIN_CHANNEL = "main_channel"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // or just use
        buildChannel(
            WARNING_CHANNEL, "Warning Channel",
            NotificationManager.IMPORTANCE_DEFAULT,
            Notification.VISIBILITY_PRIVATE
        )
        buildChannel(
            MAIN_CHANNEL, "Main Channel",
            NotificationManager.IMPORTANCE_HIGH,
            Notification.VISIBILITY_PUBLIC
        )
        startForeground(333, buildNotification())
        showAlarmActivity()
        return START_STICKY
    }

    private fun buildChannel(id: String, name: String, importance: Int, visibility: Int) {
        val channel = NotificationChannel(
            id, name,
            importance,
        ).apply {
            lockscreenVisibility = visibility
            setSound(null, null)
        }
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, WARNING_CHANNEL)
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

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val screenWakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "MyApp::ScreenWakeLock"
        )
        screenWakeLock.acquire(10 * 1000L)

        val startRingtoneIntent = Intent(
            this@NotificationService, RingtoneService::class.java
        ).apply {
            putExtra(Extra.RINGTONE_URI_EXTRA.extraName, ringtoneInfo.trackUri)
        }
        startService(startRingtoneIntent)

        val stopRingtoneIntent = PendingIntent.getBroadcast(
            this@NotificationService, 0,
            Intent(this@NotificationService, StopRingtoneReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            this@NotificationService, MAIN_CHANNEL
        )
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentTitle("Iries Alarm")
            .setContentText("Swap to stop alarm.")
            .setDeleteIntent(stopRingtoneIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()

        manager.notify(444, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

}