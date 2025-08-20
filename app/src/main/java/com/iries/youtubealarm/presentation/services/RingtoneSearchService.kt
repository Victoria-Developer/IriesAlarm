package com.iries.youtubealarm.presentation.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.iries.youtubealarm.data.repository.ChannelsRepository
import com.iries.youtubealarm.domain.constants.Extra
import com.iries.youtubealarm.presentation.AlarmActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class RingtoneSearchService : Service() {
    private var serviceScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var channelsRepo: ChannelsRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val context = this

        serviceScope.launch(Dispatchers.Main) {
            /** Replace with some music search api*/

            val ringtoneUri = withContext(Dispatchers.IO) {
                delay(5000)
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            }
            // Wake up the screen
            val wakeIntent = Intent(context, AlarmActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            wakeIntent.putExtra(Extra.RINGTONE_NAME_EXTRA.extraName, ringtoneUri)
            startActivity(wakeIntent)
        }

        return START_STICKY
    }

/** Notification prior to the alarm */
/*
    private fun buildNotification(): Notification {
        val channelId = "ringtone_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Ringtone Service",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
        }
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Iries Alarm")
            .setContentText("Your alarm will be fired soon.")
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
    }
*/
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

}