package com.iries.alarm.domain.usecases

import com.iries.alarm.data.local.repository.ArtistsRepository
import com.iries.alarm.data.remote.AuthRepository
import com.iries.alarm.data.remote.SearchApiRepository
import com.iries.alarm.domain.models.Artist
import com.iries.alarm.domain.models.AuthData
import com.iries.alarm.domain.models.RingtoneInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SoundCloudApiUseCase @Inject constructor(
    private val artistsRepository: ArtistsRepository,
    private val searchApiRepository: SearchApiRepository,
    private val authRepository: AuthRepository,
) {

    /** Remote source */

    /** Auth */
    suspend fun authorize(code: String): Result<AuthData> {
        return authRepository.exchangeAccessToken(code)
    }

    suspend fun logout(accessToken: String): Result<Boolean> {
        return authRepository.logout(accessToken)
    }

    // to backend
    suspend fun refreshAccessToken(refreshToken: String): Result<AuthData> {
        return authRepository.refreshAccessToken(refreshToken)
    }

    /** Search */
    suspend fun findArtistsByName(artistName: String): Result<List<Artist>> {
        return searchApiRepository.findArtistsByName(artistName)
    }

    suspend fun findUserSubscriptions(accessToken: String): Result<List<Artist>> {
        // auth guard to the backend
        // just pass loaded access token instead
        return searchApiRepository.findUserSubscriptions(accessToken)
    }

    suspend fun findRandomRingtone(): RingtoneInfo {
        val ringtoneInfo = RingtoneInfo()
        val artist = getRandomArtist()
        if (artist != null) {
            searchApiRepository.findRandomTrackByArtist(artist.id)
                .onSuccess { track ->
                    ringtoneInfo.apply {
                        trackUri = track.streamUrl
                        trackTitle = track.trackName
                        artistName = artist.username
                    }
                }
        }
        return ringtoneInfo
    }

    /** Local database */
    fun getAllArtists(): Flow<List<Artist>> {
        return artistsRepository.getAllArtists()
    }

    private fun getRandomArtist(): Artist? {
        return artistsRepository.getRandomArtist()
    }

    fun insert(artist: Artist) {
        artistsRepository.insert(artist)
    }

    fun delete(artist: Artist) {
        artistsRepository.delete(artist)
    }

}