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
import com.iries.alarm.presentation.activities.MainActivity
import com.iries.alarm.presentation.receivers.StopAlarmReceiver


class NotificationService : Service() {

    companion object {
        const val MAIN_CHANNEL = "main_channel"
        const val MAIN_CODE = 444
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create notification channel if doesn't exist yet
        val manager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(MAIN_CHANNEL) == null) {
            val channel = createNotificationChannel()
            manager.createNotificationChannel(channel)
        }

        // Wake up the screen
        val screenWakeLock = initializeScreenWakeLock()

        // Show notification
        startForeground(MAIN_CODE, buildNotification())

        // Release screen lock
        screenWakeLock?.release()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
        return START_STICKY
    }

    @Suppress("DEPRECATION")
    private fun initializeScreenWakeLock(): PowerManager.WakeLock? {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val screenWakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "IriesAlarm::ScreenWakeLock"
        )
        screenWakeLock.acquire(10 * 1000L)
        return screenWakeLock
    }

    private fun createNotificationChannel(): NotificationChannel {
        return NotificationChannel(
            MAIN_CHANNEL, "Main Channel",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
        }
    }

    private fun buildNotification(): Notification {
        // PendingIntent for tapping the notification
        val tapIntent = PendingIntent.getActivity(
            this@NotificationService, 0,
            Intent(this@NotificationService, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // PendingIntent to stop the alarm when user swipes away
        val deleteIntent = PendingIntent.getBroadcast(
            this@NotificationService, 0,
            Intent(this@NotificationService, StopAlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, MAIN_CHANNEL)
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentTitle("Iries Alarm")
            .setContentText("Swap to stop alarm.")
            .setContentIntent(tapIntent)
            .setDeleteIntent(deleteIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

}