package com.iries.alarm.domain.usecases

import com.iries.alarm.data.local.repository.ArtistsRepository
import com.iries.alarm.data.remote.SearchApiRepository
import com.iries.alarm.domain.models.Artist
import com.iries.alarm.domain.models.RingtoneInfo
import com.iries.alarm.domain.models.Track
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

    /** Some artists might have all tracks blocked for certain regions */
    private suspend fun filterOutForbiddenResults(
        artists: Result<List<Artist>>
    ): Result<List<Artist>> = artists.map { list ->
        coroutineScope {
            list.map { artist ->
                async {
                    val tracks = findArtistTracks(artist.id).getOrNull()
                        ?.filter { track ->  track.isStreamable }
                    artist.takeIf { !tracks.isNullOrEmpty() }
                }
            }.awaitAll()
                .filterNotNull()
        }
    }

    suspend fun findArtistsByName(artistName: String): Result<List<Artist>> {
        return authGuard { accessToken ->
            val artists = searchApiRepository.findArtistsByName(
                artistName, accessToken
            )
            // Filter out if all tracks are blocked
            filterOutForbiddenResults(artists)
        }
    }

    suspend fun findUserSubscriptions(): Result<List<Artist>> {
        return authGuard { accessToken ->
            val artists = searchApiRepository.findUserSubscriptions(
                accessToken
            )
            // Filter out if all tracks are blocked
            filterOutForbiddenResults(artists)
        }
    }

    private suspend fun findArtistTracks(artistId: Long): Result<List<Track>> {
        return authGuard { accessToken ->
            searchApiRepository.findArtistTracks(
                artistId, accessToken
            )
        }
    }

    private suspend fun resolveStreamUrl(trackId: Long): Result<String> {
        return authGuard { accessToken ->
            searchApiRepository.resolveStreamUrl(
                trackId, accessToken
            )
        }
    }

    suspend fun findRandomRingtone(): RingtoneInfo {
        val ringtoneInfo = RingtoneInfo()
        val artist = getRandomArtist() ?: return ringtoneInfo
        val tracksResult = findArtistTracks(artist.id)
        tracksResult.onSuccess { tracks ->
            if (tracks.isEmpty()) return@onSuccess
            val track = tracks.random()
            resolveStreamUrl(track.id).onSuccess { uri ->
                ringtoneInfo.trackUri = uri
                ringtoneInfo.trackTitle = track.title
                ringtoneInfo.artistName = artist.username
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