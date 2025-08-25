package com.iries.alarm.domain.usecases

import com.iries.alarm.data.local.repository.ArtistsRepository
import com.iries.alarm.data.remote.SearchApiRepository
import com.iries.alarm.domain.models.Artist
import com.iries.alarm.domain.models.Track
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchApiUseCase @Inject constructor(
    private val artistsRepository: ArtistsRepository,
    private val searchApiRepository: SearchApiRepository,
    private val authUseCase: AuthUseCase,
) {

    /** Remote source */
    private suspend fun <T> authGuard(apiMethod: suspend (String) -> Result<T>): Result<T> {
        val accessToken = authUseCase.getAccessToken()
        return accessToken.fold(
            onSuccess = { token -> apiMethod(token) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    suspend fun findArtistsByName(artistName: String): Result<List<Artist>> {
        return authGuard { accessToken ->
            searchApiRepository.findArtistsByName(
                artistName, accessToken
            )
        }
    }

    suspend fun findArtistTracks(artistId: Long): Result<List<Track>> {
        return authGuard { accessToken ->
            searchApiRepository.findArtistTracks(
                artistId, accessToken
            )
        }
    }

    suspend fun resolveStreamUrl(mediaUrl: String): Result<String> {
        return authGuard { accessToken ->
            searchApiRepository.resolveStreamUrl(
                mediaUrl, accessToken
            )
        }
    }

    /** Local database */
    fun getAllArtists(): Flow<List<Artist>> {
        return artistsRepository.getAllArtists()
    }

    fun getRandomArtist(): Artist? {
        return artistsRepository.getRandomArtist()
    }

    fun insert(artist: Artist) {
        artistsRepository.insert(artist)
    }

    fun delete(artist: Artist) {
        artistsRepository.delete(artist)
    }

}