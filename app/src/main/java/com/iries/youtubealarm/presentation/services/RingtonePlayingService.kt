package com.iries.youtubealarm.presentation.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.IBinder
import com.iries.youtubealarm.data.repository.ChannelsRepository
import com.iries.youtubealarm.domain.constants.Extra
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RingtonePlayingService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var serviceScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var channelsRepo: ChannelsRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceScope.launch(Dispatchers.IO) {
            var ringtoneUri = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM)
            var ringtoneName = "Default ringtone"

            val randomVideo = channelsRepo.getRandomChannelUploadsId()
                ?.let { channelsRepo.fetchVideos(it).getOrNull() }
                ?.randomOrNull()

            val url = randomVideo?.let { channelsRepo.videoToAudioUrl(it).getOrNull() }

            if (url != null) {
                ringtoneUri = url
                ringtoneName = randomVideo.getTitle() ?: "No title"
                println("$url , name $ringtoneName")
            }

            startService(ringtoneUri = ringtoneUri.toString(), ringtoneName = ringtoneName)
        }

        return START_STICKY
    }

    private fun startService(ringtoneUri: String, ringtoneName: String) {
        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener { start() }
            setOnErrorListener { mp, what, extra ->
                println("MediaPlayer error: what=$what, extra=$extra")
                mp.reset()
                false
            }
            try {
                setDataSource(ringtoneUri)
                prepareAsync()
            } catch (e: Exception) {
                println("Error setting data source: ${e.message}")
            }
        }

        startNotificationService(ringtoneName)
    }

    private fun startNotificationService(ringtoneName: String) {
        val notificationIntent = Intent(this, NotificationService::class.java)
        notificationIntent.putExtra(Extra.RINGTONE_NAME_EXTRA.extraName, ringtoneName)
        startService(notificationIntent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        mediaPlayer?.apply {
            println("Stop ringtone")
            stop()
            release()
        }
    }

}