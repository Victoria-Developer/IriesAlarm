package com.iries.alarm.presentation.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.iries.alarm.domain.constants.Extra
import com.iries.alarm.domain.models.RingtoneInfo
import com.iries.alarm.domain.usecases.SearchApiUseCase
import com.iries.alarm.presentation.activities.AlarmActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class RingtoneSearchService : Service() {
    private var serviceScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var searchApiUseCase: SearchApiUseCase

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startService()
        return START_STICKY
    }

    private fun startService() = serviceScope.launch(Dispatchers.Main) {
        var ringtoneInfo: RingtoneInfo
        withContext(Dispatchers.IO) {
            ringtoneInfo = searchApiUseCase.findRandomRingtone()
        }
        val alarmActivityIntent = Intent(
            this@RingtoneSearchService, AlarmActivity::class.java
        ).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        alarmActivityIntent.putExtra(
            Extra.RINGTONE_URI_EXTRA.extraName, ringtoneInfo.trackUri
        )
        alarmActivityIntent.putExtra(
            Extra.RINGTONE_NAME_EXTRA.extraName, ringtoneInfo.trackTitle
        )
        alarmActivityIntent.putExtra(
            Extra.RINGTONE_AUTHOR_EXTRA.extraName, ringtoneInfo.artistName
        )
        startActivity(alarmActivityIntent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

}