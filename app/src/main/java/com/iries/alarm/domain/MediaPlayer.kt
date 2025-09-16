package com.iries.alarm.domain

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MediaPlayer{

    private var player: ExoPlayer? = null
    private var ringtone: Ringtone? = null

    fun playDefaultRingtone(context:Context) {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, uri)
        ringtone!!.play()
        Log.e("MediaPlayer", "Started default ringtone playback.")
    }

    fun playTrack(context:Context, ringtoneUri: String?) {
        player = ExoPlayer.Builder(context).build()
        val mediaItem = MediaItem.Builder()
            .setUri(ringtoneUri)
            .build()
        player!!.setMediaItem(mediaItem)

        try {
            player!!.prepare()
            player!!.play()
            Log.e("ExoPlayer", "Started audio playback.")
        } catch (e: Exception) {
            Log.e("ExoPlayer", "Error preparing/playing media", e)
            player!!.release()
        }
    }

    fun stopPlayback() {
        if (ringtone != null)
            ringtone!!.stop()

        if (player != null)
            player!!.apply {
                stop()
                release()
            }
        Log.e("MediaPlayer", "Finished the service.")
    }
}