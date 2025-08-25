package com.iries.alarm.data.local.repository

import com.iries.alarm.data.local.dao.ArtistsDao
import com.iries.alarm.data.local.entity.ArtistEntity
import com.iries.alarm.domain.models.Artist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ArtistsRepository @Inject constructor(
    private var artistsDao: ArtistsDao
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

    private fun artistEntityToDto(artistEntity: ArtistEntity): Artist {
        return Artist(
            id = artistEntity.getId(),
            username = artistEntity.getUsername().toString(),
            imgUrl = artistEntity.getImgUrl().toString()
        )
    }

    private fun dtoToArtistEntity(artist: Artist): ArtistEntity {
        return ArtistEntity().apply {
            setId(artist.id)
            setUsername(artist.username)
            setImgUrl(artist.imgUrl)
        }
    }

}