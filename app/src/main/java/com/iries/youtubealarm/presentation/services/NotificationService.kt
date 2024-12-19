package com.iries.youtubealarm.presentation.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.app.Service
import com.iries.youtubealarm.presentation.MainActivity
import com.iries.youtubealarm.R
import com.iries.youtubealarm.domain.constants.Extra

class NotificationService : Service() {

    private val channelId: String = "0"
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val ringtoneName = intent
            .getStringExtra(Extra.RINGTONE_NAME_EXTRA.extraName)

        createNotification(ringtoneName)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel() {
        val name: CharSequence = resources
            .getString(R.string.app_name)
        val description: String = resources
            .getString(R.string.notification_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel = NotificationChannel(channelId, name, importance)
        notificationChannel.description = description
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val notificationManager
                : NotificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }


    private fun createNotification(ringtoneName: String?) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
            != PackageManager.PERMISSION_GRANTED
        ) return

        val mNotificationManager
                : NotificationManager =
            getSystemService(NotificationManager::class.java)
        val existingChannel = mNotificationManager
            .getNotificationChannel(channelId)
        if (existingChannel == null) createNotificationChannel()

        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent
            .getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
                        or PendingIntent.FLAG_IMMUTABLE
            )

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            channelId
        )
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText("Now playing: $ringtoneName")
            .setFullScreenIntent(pendingIntent, true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val notificationManager = NotificationManagerCompat
            .from(this)
        notificationManager.notify(channelId.toInt(), builder.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}