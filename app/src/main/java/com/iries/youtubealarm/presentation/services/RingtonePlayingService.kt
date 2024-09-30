package com.iries.youtubealarm.presentation.services

import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import com.google.api.services.youtube.YouTube
import com.iries.youtubealarm.data.UserConfigs
import com.iries.youtubealarm.data.dao.ChannelsDao
import com.iries.youtubealarm.data.Video
import com.iries.youtubealarm.domain.ConfigsReader
import com.iries.youtubealarm.domain.constants.Extra
import com.iries.youtubealarm.domain.youtube_api.YoutubeAuth
import com.iries.youtubealarm.domain.youtube_api.YoutubeSearch
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.mapper.VideoInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RingtonePlayingService : Service() {
    private var ringtone: Ringtone? = null
    private var channelId: String? = null
    private lateinit var settings: UserConfigs
    private var serviceScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var channelsDao: ChannelsDao

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        serviceScope.launch(Dispatchers.IO) {
            channelId = channelsDao.getRandomChannelId()
            settings = ConfigsReader.load()
            if (channelId == null)
                playRingtone(null)
            else
                startService()
        }

        return START_STICKY
    }

    private fun startService() {
        val youTube: YouTube = YoutubeAuth.getYoutube(this)

        serviceScope.launch(Dispatchers.IO) {
            val video: Video? = YoutubeSearch.findVideoByFilters(
                youTube,
                channelId,
                settings.getOrder(),
                settings.getDuration()
            )?.get(0)

            playRingtone(video)
        }

    }

    private fun startNotificationService(ringtoneName: String) {
        val notificationIntent = Intent(this, NotificationService::class.java)
        notificationIntent.putExtra(Extra.RINGTONE_NAME_EXTRA.extraName, ringtoneName)
        startService(notificationIntent)
    }

    private fun playRingtone(video: Video?) {
        val context = this

        serviceScope.launch(Dispatchers.IO) {

            val alarmUri: Uri?
            val ringtoneName: String
            val videoId = video?.getId()

            if (videoId == null) {
                alarmUri = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_ALARM)
                ringtoneName = "default ringtone"
            } else {
                alarmUri = extractAudio(
                    "https://youtu.be/" + video.getId()
                )
                ringtoneName = video.getTitle()
            }

            ringtone = RingtoneManager.getRingtone(context, alarmUri)
            ringtone!!.play()
            startNotificationService(ringtoneName)
        }

    }

    private fun extractAudio(videoURL: String): Uri {
        val request = YoutubeDLRequest(videoURL)
        request.addOption("--extract-audio")
        val streamInfo: VideoInfo
        try {
            streamInfo = YoutubeDL.getInstance().getInfo(request)
        } catch (e: YoutubeDLException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } catch (e: YoutubeDL.CanceledException) {
            throw RuntimeException(e)
        }
        return Uri.parse(streamInfo.url)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onDestroy() {
        super.onDestroy()
        println("Stop ringtone")
        shutUpRingtone()
    }

    private fun shutUpRingtone() {
        if (ringtone != null) ringtone!!.stop()
        ringtone = null
    }
}