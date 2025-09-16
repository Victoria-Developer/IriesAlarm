package com.iries.alarm.presentation.activities

import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iries.alarm.presentation.receivers.StopAlarmReceiver
import com.iries.alarm.presentation.theme.IriesAlarmTheme

class AlarmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTurnScreenOn(true)
        setShowWhenLocked(true)

        val km = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        km.requestDismissKeyguard(this, null)

        enableEdgeToEdge()
        setContent {
            IriesAlarmTheme {
                AlarmActivityScreen {
                    stopAlarm()
                }
            }
        }
    }

    private fun stopAlarm() {
        val stopIntent = Intent(this, StopAlarmReceiver::class.java)
        sendBroadcast(stopIntent)
        finish()
    }

    @Composable
    fun AlarmActivityScreen(onStopAlarm: () -> Unit) {
        Scaffold { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Iries Alarm")
                    Text("It's time to wake up!")
                    Button(onClick = onStopAlarm) {
                        Text("Turn off")
                    }
                }
            }
        }
    }

}