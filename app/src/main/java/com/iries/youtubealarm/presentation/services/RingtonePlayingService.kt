package com.iries.youtubealarm.presentation.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import com.iries.youtubealarm.data.repository.ChannelsRepository
import com.iries.youtubealarm.domain.ConfigsReader
import com.iries.youtubealarm.domain.constants.Extra
import com.iries.youtubealarm.domain.models.UserConfigs
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
            val channelId = channelsRepo.getRandomChannelId()
            val userConfigs = ConfigsReader(
                this@RingtonePlayingService
            ).loadUserConfigs()
            startService(channelId, userConfigs)
        }

        return START_STICKY
    }

    private suspend fun startService(
        channelId: String?,
        userConfigs: UserConfigs
    ) {
        var youtubeVideoUri: Uri? = null
        val defaultAlarmUri = RingtoneManager
            .getDefaultUri(RingtoneManager.TYPE_ALARM)

        val defaultRingtoneName = "Default ringtone"
        var ringtoneName = defaultRingtoneName

        if (channelId != null) {
            channelsRepo.fetchVideoByFilters(
                channelId,
                userConfigs.getOrder(),
                userConfigs.getDuration()
            ).onSuccess { video ->
                channelsRepo.videoToAudioUrl(video).onSuccess {
                    youtubeVideoUri = it
                    ringtoneName = video.getTitle()
                }
            }
        }

        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener { start() }
            setOnErrorListener { mp, what, extra ->
                println("MediaPlayer error: what=$what, extra=$extra")
                mp.reset()
                false
            }
            try {
                setDataSource(youtubeVideoUri?.toString() ?: defaultAlarmUri.toString())
                prepareAsync()
            } catch (e: Exception) {
                println("Error setting data source: ${e.message}")
                ringtoneName = defaultRingtoneName
                setDataSource(defaultAlarmUri.toString())
                prepareAsync()
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