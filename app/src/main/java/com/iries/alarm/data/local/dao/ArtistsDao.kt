package com.iries.alarm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.iries.alarm.data.local.entity.ArtistEntity
import com.iries.alarm.domain.models.Artist
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistsDao {
    @Insert
    fun insert(artist: ArtistEntity)

    @Delete
    fun delete(artistId: ArtistEntity)

    @Update
    fun update(artist: ArtistEntity)

    @Query("DELETE FROM ARTISTS")
    fun deleteAll()

    @Query("SELECT * FROM ARTISTS")
    fun getAllArtists(): Flow<List<ArtistEntity>>

    //@Query("SELECT channelId FROM CHANNELS WHERE id = :dbId")
    @Query("SELECT * FROM ARTISTS ORDER BY RANDOM() LIMIT 1")
    fun getRandomArtist(): Artist?
}