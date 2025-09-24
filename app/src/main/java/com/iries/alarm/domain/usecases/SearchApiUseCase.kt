package com.iries.alarm.domain.usecases

import com.iries.alarm.data.local.repository.ArtistsRepository
import com.iries.alarm.data.remote.SearchApiRepository
import com.iries.alarm.domain.models.Artist
import com.iries.alarm.domain.models.RingtoneInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchApiUseCase @Inject constructor(
    private val artistsRepository: ArtistsRepository,
    private val searchApiRepository: SearchApiRepository,
    private val authUseCase: AuthUseCase,
) {

    /** Remote source */
    // to the backend
    private suspend fun <T> authGuard(apiMethod: suspend (String) -> Result<T>): Result<T> {
        val accessToken = authUseCase.getAccessToken()
        return accessToken.fold(
            onSuccess = { token -> apiMethod(token) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    suspend fun findArtistsByName(artistName: String): Result<List<Artist>> {
        return searchApiRepository.findArtistsByName(artistName)
    }

    suspend fun findUserSubscriptions(): Result<List<Artist>> {
        // auth guard to the backend
        return authGuard { accessToken ->
            searchApiRepository.findUserSubscriptions(
                accessToken
            )
        }
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