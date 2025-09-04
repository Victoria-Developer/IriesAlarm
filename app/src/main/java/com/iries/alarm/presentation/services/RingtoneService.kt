package com.iries.alarm.presentation.services

import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.IBinder
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.iries.alarm.domain.constants.Extra

class RingtoneService : Service(){
    private var player: ExoPlayer? = null
    private var ringtone: Ringtone? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val ringtoneUri = intent?.getStringExtra(Extra.RINGTONE_URI_EXTRA.extraName)
        if (ringtoneUri.isNullOrEmpty()) playDefaultRingtone()
        else playTrack(ringtoneUri = ringtoneUri)
        return START_STICKY
    }

    private fun playDefaultRingtone() {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(this, uri)
        ringtone!!.play()
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
        } catch (e: Exception) {
            Log.e("ExoPlayer", "Error preparing/playing media", e)
            player!!.release()
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