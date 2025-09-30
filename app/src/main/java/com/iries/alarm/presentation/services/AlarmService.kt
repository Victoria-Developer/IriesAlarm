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
import android.util.Log
import androidx.core.app.NotificationCompat
import com.iries.alarm.R
import com.iries.alarm.domain.MediaPlayer
import com.iries.alarm.domain.usecases.SoundCloudApiUseCase
import com.iries.alarm.presentation.activities.AlarmActivity
import com.iries.alarm.presentation.receivers.StopAlarmReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service() {
    private var serviceScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var notificationManager: NotificationManager
    private lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var soundCloudApiUseCase: SoundCloudApiUseCase

    companion object {
        const val NOTIFICATION_CHANNEL_CODE = "main_channel"
        const val MAIN_NOTIFICATION_CODE = 444
        const val WARNING_NOTIFICATION_CODE = 333
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("AlarmService", "Started the service.")
        startService()
        return START_STICKY
    }

    private fun startService() = serviceScope.launch {
        // Wake up the screen
        val screenWakeLock = initializeScreenWakeLock()

        // Create notification channel if doesn't exist yet
        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_CODE) == null) {
            val channel = createNotificationChannel()
            notificationManager.createNotificationChannel(channel)
        }
        // Show warning notification
        startForeground(WARNING_NOTIFICATION_CODE, buildWarningNotification())

        mediaPlayer = MediaPlayer()

        // Find track and play a ringtone
        val ringtoneInfo = withContext(Dispatchers.IO) {
            soundCloudApiUseCase.findRandomRingtone()
        }
        val ringtoneUri = ringtoneInfo.trackUri
        if (ringtoneUri.isEmpty())
            mediaPlayer.playDefaultRingtone(this@AlarmService)
        else
            mediaPlayer.playTrack(this@AlarmService, ringtoneUri)

        // Show main notification
        notificationManager.notify(MAIN_NOTIFICATION_CODE, buildMainNotification())

        // Release screen wake lock
        screenWakeLock?.release()
    }

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
            NOTIFICATION_CHANNEL_CODE, "Main Channel",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
        }
    }

    private fun buildWarningNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_CODE)
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentTitle("Iries Alarm")
            .setContentText("Your alarm will be fired soon.")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()
    }

    private fun buildMainNotification(): Notification {
        // Full screen intent, also prevents notification hiding
        val fullScreenIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Stop the alarm intent
        val deleteIntent = PendingIntent.getBroadcast(
            this, 1,
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

    override fun onDestroy() {
        Log.e("AlarmService", "Finished the service.")
        mediaPlayer.stopPlayback()
        stopForeground(STOP_FOREGROUND_REMOVE)
        notificationManager.cancel(MAIN_NOTIFICATION_CODE)
        serviceScope.cancel()
        super.onDestroy()
    }
}