package com.iries.youtubealarm.presentation

import android.app.KeyguardManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iries.youtubealarm.domain.constants.Extra
import com.iries.youtubealarm.presentation.theme.IriesAlarmTheme

class AlarmActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Wake the screen and show over lockscreen
        setTurnScreenOn(true)
        setShowWhenLocked(true)

        // Optional: dismiss keyguard if needed
        val km = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        km.requestDismissKeyguard(this, null)

        val ringtoneUri = intent.getStringExtra(Extra.RINGTONE_NAME_EXTRA.extraName)

        playRingtone(ringtoneUri = ringtoneUri.toString())

        enableEdgeToEdge()
        setContent {
            IriesAlarmTheme {
                Scaffold(
                    content = { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Iries Alarm")
                            Text("It's time to wake up!")
                            Button(onClick = {
                                finish()
                            }) { Text("Turn off") }
                        }
                    })
            }
        }
    }

    private fun playRingtone(ringtoneUri: String) {
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
    }

    override fun onDestroy() {
        mediaPlayer?.apply {
            println("Stop ringtone")
            stop()
            release()
        }
        super.onDestroy()
    }
}