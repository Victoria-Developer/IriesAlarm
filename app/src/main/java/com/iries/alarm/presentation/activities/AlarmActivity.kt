package com.iries.alarm.presentation.activities

import android.app.KeyguardManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.iries.alarm.domain.constants.Extra
import com.iries.alarm.presentation.theme.IriesAlarmTheme

class AlarmActivity : ComponentActivity() {
    private var player: ExoPlayer? = null
    private var ringtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTurnScreenOn(true)
        setShowWhenLocked(true)

        val km = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        km.requestDismissKeyguard(this, null)

        val ringtoneUri = intent.getStringExtra(Extra.RINGTONE_URI_EXTRA.extraName)
        val ringtoneName =
            intent.getStringExtra(Extra.RINGTONE_NAME_EXTRA.extraName) ?: "Unknown Track"
        val ringtoneAuthor =
            intent.getStringExtra(Extra.RINGTONE_AUTHOR_EXTRA.extraName) ?: "Unknown Author"

        if (ringtoneUri.isNullOrEmpty()) playDefaultRingtone()
        else playTrack(ringtoneUri = ringtoneUri)

        enableEdgeToEdge()
        setContent {
            IriesAlarmTheme {
                Scaffold(
                    content = { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Iries Alarm", fontSize = 27.sp)
                            Spacer(modifier = Modifier.height(15.dp))
                            Text("It's time to wake up!", fontSize = 22.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                "Now playing $ringtoneName by $ringtoneAuthor",
                                fontSize = 22.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(onClick = {
                                finish()
                            }) { Text("Turn off", fontSize = 20.sp) }
                        }
                    })
            }
        }
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