package com.iries.youtubealarm.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.iries.youtubealarm.data.entity.YTChannel
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelsDao {
    @Insert
    fun insert(ytChannel: YTChannel)

    @Delete
    fun delete(ytChannel: YTChannel)

    @Update
    fun update(ytChannel: YTChannel)

    @Query("DELETE FROM CHANNELS")
    fun deleteAll()

    @Query("SELECT * FROM CHANNELS")
    fun getAllChannels(): Flow<List<YTChannel>>

    @Query("SELECT COUNT(id) FROM CHANNELS")
    fun getChannelsCount(): Int

    //@Query("SELECT channelId FROM CHANNELS WHERE id = :dbId")
    @Query("SELECT channelId FROM CHANNELS ORDER BY RANDOM() LIMIT 1")
    fun getRandomChannelId(): String?
}