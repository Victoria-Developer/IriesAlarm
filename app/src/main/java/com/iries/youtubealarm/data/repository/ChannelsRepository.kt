package com.iries.youtubealarm.data.repository

import android.content.Context
import android.net.Uri
import com.iries.youtubealarm.data.dao.ChannelsDao
import com.iries.youtubealarm.data.entity.YTChannel
import com.iries.youtubealarm.data.network.YoutubeAuth
import com.iries.youtubealarm.data.network.YoutubeSearchApi
import com.iries.youtubealarm.domain.converters.NetworkConverter
import com.iries.youtubealarm.data.entity.Video
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChannelsRepository @Inject constructor(
    private var channelsDao: ChannelsDao,
    private val youtubeSearchApi: YoutubeSearchApi
) {
    private var allChannels: Flow<List<YTChannel>> = channelsDao.getAllChannels()

    fun insert(channel: YTChannel) {
        channelsDao.insert(channel)
    }

    fun update(channel: YTChannel) {
        channelsDao.update(channel)
    }

    fun delete(channel: YTChannel) {
        channelsDao.delete(channel)
    }

    fun deleteAll() {
        channelsDao.deleteAll()
    }

    fun getAllChannels(): Flow<List<YTChannel>> {
        return allChannels
    }

    fun getRandomChannelUploadsId(): String? {
        return channelsDao.getRandomChannelUploadsId()
    }

    suspend fun getUploadsPlaylistId(channelId:String): Result<String> {
        val result = youtubeSearchApi.getUploadsId(channelId)
        return result.mapCatching { response ->
            NetworkConverter.parseUploadsPlaylistResponse(response)
        }
    }

    suspend fun fetchSubscriptions(context: Context): Result<List<YTChannel>> {
        val accessToken = YoutubeAuth.authorize(context).token
        val result = youtubeSearchApi.getSubscriptions(accessToken)
        return result.mapCatching { response ->
            NetworkConverter.parseSubsResponse(response)
        }
    }

    suspend fun fetchChannelsByName(keyWord: String): Result<List<YTChannel>> {
        val result = youtubeSearchApi.findChannelByKeyword(keyWord)
        return result.mapCatching { response ->
            NetworkConverter.parseChannelsResponse(response)
        }
    }

    suspend fun fetchVideos(
        playlistId: String
    ): Result<List<Video>> {
        val result = youtubeSearchApi.getPlaylistItems(playlistId)
        return result.mapCatching { response ->
            NetworkConverter.parsePlaylistItemResponse(response)
        }
    }

    fun videoToAudioUrl(video: Video): Result<Uri> {
        return youtubeSearchApi.videoUrlToAudio(
            "https://youtu.be/" + video.getId()
        )
    }
}