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
        const val WARNING_CODE = 333
        const val MAIN_CHANNEL = "main_channel"
        const val MAIN_CODE = 444
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channel = NotificationChannel(
            MAIN_CHANNEL, "Main Channel",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
        }
        manager.createNotificationChannel(channel)
        startForeground(WARNING_CODE, buildWarningNotification())
        startService()
        return START_STICKY
    }

    private fun buildWarningNotification(): Notification {
        return NotificationCompat.Builder(this, MAIN_CHANNEL)
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentTitle("Iries Alarm")
            .setContentText("Your alarm will be fired soon.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()
    }

    private fun buildMainNotification(deleteIntent: PendingIntent) {
        val notification = NotificationCompat.Builder(this, MAIN_CHANNEL)
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentTitle("Iries Alarm")
            .setContentText("Swap to stop alarm.")
            .setDeleteIntent(deleteIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()

        manager.notify(MAIN_CODE, notification)
    }

    @Suppress("DEPRECATION")
    private fun startService() = serviceScope.launch(Dispatchers.Main) {
        val ringtoneInfo = withContext(Dispatchers.IO) {
            searchApiUseCase.findRandomRingtone()
        }

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val screenWakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "IriesAlarm::ScreenWakeLock"
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

        buildMainNotification(stopRingtoneIntent)
        screenWakeLock.release()

        stopForeground(false)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

}