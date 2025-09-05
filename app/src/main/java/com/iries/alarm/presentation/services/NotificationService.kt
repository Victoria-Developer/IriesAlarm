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
import com.iries.alarm.presentation.activities.AlarmActivity
import com.iries.alarm.presentation.receivers.StopAlarmReceiver


class NotificationService : Service() {

    companion object {
        const val NOTIFICATION_CHANNEL_CODE = "main_channel"
        const val NOTIFICATION_CODE = 444
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create notification channel if doesn't exist yet
        val manager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(NOTIFICATION_CHANNEL_CODE) == null) {
            val channel = createNotificationChannel()
            manager.createNotificationChannel(channel)
        }

        // Wake up the screen
        //val screenWakeLock = initializeScreenWakeLock()

        // Show notification
        startForeground(NOTIFICATION_CODE, buildNotification())

        // Release screen lock
       // screenWakeLock?.release()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
        return START_STICKY
    }

    /*
    private fun initializeScreenWakeLock(): PowerManager.WakeLock? {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val screenWakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "IriesAlarm::ScreenWakeLock"
        )
        screenWakeLock.acquire(10 * 1000L)
        return screenWakeLock
    }
     */

    private fun createNotificationChannel(): NotificationChannel {
        return NotificationChannel(
            NOTIFICATION_CHANNEL_CODE, "Main Channel",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
        }
    }

    private fun buildNotification(): Notification {
        // Full screen intent, also prevents notification hiding
        val fullScreenIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Stop the alarm intent
        val deleteIntent = PendingIntent.getBroadcast(
            this, 0,
            Intent(this, StopAlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_CODE)
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentTitle("Iries Alarm")
            .setContentText("It's time to wake up.")
            .setDeleteIntent(deleteIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setFullScreenIntent(fullScreenIntent, true)
            .addAction(
                R.drawable.baseline_access_alarm_24,
                "Stop",
                deleteIntent
            )
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

}