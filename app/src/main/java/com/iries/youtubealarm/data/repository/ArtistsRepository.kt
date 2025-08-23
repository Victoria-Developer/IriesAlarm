package com.iries.youtubealarm.data.repository

import com.iries.youtubealarm.data.dao.ArtistsDao
import com.iries.youtubealarm.data.entity.ArtistEntity
import com.iries.youtubealarm.data.network.SoundCloudApiService
import com.iries.youtubealarm.domain.converters.SoundCloudConverter
import com.iries.youtubealarm.domain.models.Artist
import com.iries.youtubealarm.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ArtistsRepository @Inject constructor(
    private var artistsDao: ArtistsDao,
    private val soundCloudApiService: SoundCloudApiService
) {
    private var allArtists: Flow<List<ArtistEntity>> = artistsDao.getAllArtists()

    fun insert(artist: Artist) {
        artistsDao.insert(dtoToArtistEntity(artist))
    }

    fun update(artist: Artist) {
        artistsDao.update(dtoToArtistEntity(artist))
    }

    fun delete(artist: Artist) {
        artistsDao.delete(dtoToArtistEntity(artist))
    }

    fun deleteAll() {
        artistsDao.deleteAll()
    }

    fun getAllArtists(): Flow<List<Artist>> {
        return allArtists
            .map { artistEntities ->
                artistEntities.map { entity ->
                    artistEntityToDto(entity)
                }
            }
    }

    fun getRandomArtist(): Artist? {
        return artistsDao.getRandomArtist()
    }

    suspend fun resolveStreamUrl(mediaUrl: String): Result<String> {
        val result = soundCloudApiService.resolveAudioUrl(mediaUrl)
        return result.mapCatching {
            SoundCloudConverter.parseResolvedUrl(it)
        }
    }

    suspend fun findArtistTracks(userId: Long): Result<List<Track>> {
        val result = soundCloudApiService.findTracksByArtist(userId)
        return result.mapCatching { response ->
            SoundCloudConverter.parseTracksResponse(response)
        }
    }

    suspend fun findArtistsByName(keyWord: String): Result<List<Artist>> {
        val result = soundCloudApiService.findArtistsByName(keyWord)
        return result.mapCatching { response ->
            SoundCloudConverter.parseArtistsResponse(response)
        }
    }

    private fun artistEntityToDto(artistEntity: ArtistEntity): Artist {
        return Artist().apply {
            id = artistEntity.getId()
            username = artistEntity.getUsername().toString()
            imgUrl = artistEntity.getImgUrl().toString()
        }
    }

    private fun dtoToArtistEntity(artist: Artist): ArtistEntity {
        return ArtistEntity().apply {
            setId(artist.id)
            setUsername(artist.username)
            setImgUrl(artist.imgUrl)
        }
    }

}