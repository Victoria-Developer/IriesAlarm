package com.iries.alarm.presentation.services

import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.iries.alarm.domain.usecases.SearchApiUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class RingtonePlaybackService : Service(){
    private var serviceScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    @Inject
    lateinit var searchApiUseCase: SearchApiUseCase

    private var player: ExoPlayer? = null
    private var ringtone: Ringtone? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceScope.launch {
            val ringtoneInfo = withContext(Dispatchers.IO) {
                searchApiUseCase.findRandomRingtone()
            }
            val ringtoneUri = ringtoneInfo.trackUri
            if (ringtoneUri.isEmpty())
                playDefaultRingtone()
            else
                playTrack(ringtoneUri = ringtoneUri)
        }
        return START_STICKY
    }

    private fun sendNotification(){
        val startIntent = Intent(this, NotificationService::class.java)
        ContextCompat.startForegroundService(this, startIntent)
    }

    private fun playDefaultRingtone() {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(this, uri)
        ringtone!!.play()
        sendNotification()
    }

    private fun playTrack(ringtoneUri: String?) {
        player = ExoPlayer.Builder(this).build()
        val mediaItem = MediaItem.Builder()
            .setUri(ringtoneUri)
            .build()
        player!!.setMediaItem(mediaItem)

        try {
            player!!.prepare()
            player!!.play()
            sendNotification()
        } catch (e: Exception) {
            Log.e("ExoPlayer", "Error preparing/playing media", e)
            player!!.release()
            playDefaultRingtone()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        if (ringtone != null)
            ringtone!!.stop()

        if (player != null)
            player!!.apply {
                stop()
                release()
            }
        super.onDestroy()
    }
}