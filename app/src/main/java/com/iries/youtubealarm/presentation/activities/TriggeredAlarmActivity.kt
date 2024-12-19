package com.iries.youtubealarm.presentation.activities

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iries.youtubealarm.domain.AlarmManager
import com.iries.youtubealarm.domain.constants.Extra


class TriggeredAlarmActivity : AppCompatActivity() {
    private val addMillis: Int = 180000 //3 min
    private var ringtoneText: String = "Unknown"

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ringtoneText = "Now playing: \n" + savedInstanceState
            ?.getString(Extra.RINGTONE_NAME_EXTRA.extraName)

        setShowWhenLocked(true)
        setTurnScreenOn(true)
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this, null)

        setContent {
            TriggeredAlarmScreen()
        }

    }

    @Composable
    fun TriggeredAlarmScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                ringtoneText,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                softWrap = true
            )

            Spacer(Modifier.padding(0.dp, 20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Button({ dismiss() }) {
                    Text("Stop", fontSize = 20.sp)
                }

                Button({ snooze() }) {
                    Text("Snooze", fontSize = 20.sp)
                }

            }
        }

    }

    private fun snooze() {
        AlarmManager.stopCurrentAlarm(this)
        println("Alarm Snoozed")
        //set new one shot alarm
        AlarmManager.setAlarm(
            this,
            System.currentTimeMillis() + addMillis, 0,
            false
        )
        toMainActivity()
    }

    private fun dismiss() {
        AlarmManager.stopCurrentAlarm(this)
        println("Alarm Dismissed")
        toMainActivity()
    }

    private fun toMainActivity() {
        val intent = Intent(
            this@TriggeredAlarmActivity,
            MainActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}