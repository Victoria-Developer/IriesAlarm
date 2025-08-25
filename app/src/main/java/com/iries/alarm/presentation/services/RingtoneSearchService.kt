package com.iries.alarm.presentation.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.iries.alarm.domain.constants.Extra
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
        val context = this

        serviceScope.launch(Dispatchers.Main) {
            var ringtoneUri: String? = null
            var ringtoneName: String? = null
            var ringtoneAuthor: String? = null
            withContext(Dispatchers.IO) {

                searchApiUseCase.getRandomArtist()?.let { artist ->
                    searchApiUseCase.findArtistTracks(artist.id).onSuccess { tracks ->
                        if (tracks.isNotEmpty()) {
                            val track =
                                tracks.filter { track -> track.progressiveUrl != null }.random()
                            searchApiUseCase.resolveStreamUrl(track.progressiveUrl!!)
                                .onSuccess { uri ->
                                    ringtoneUri = uri
                                    ringtoneName = track.title
                                    ringtoneAuthor = artist.username
                                }
                        }
                    }
                }
            }
            // Wake up the screen
            val wakeIntent = Intent(context, AlarmActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            wakeIntent.putExtra(Extra.RINGTONE_URI_EXTRA.extraName, ringtoneUri)
            wakeIntent.putExtra(Extra.RINGTONE_NAME_EXTRA.extraName, ringtoneName)
            wakeIntent.putExtra(Extra.RINGTONE_AUTHOR_EXTRA.extraName, ringtoneAuthor)
            startActivity(wakeIntent)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

}