package com.iries.youtubealarm.domain.youtube_api

import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchResult
import com.google.api.services.youtube.model.Subscription
import com.google.api.services.youtube.model.SubscriptionListResponse
import com.iries.youtubealarm.data.entity.YTChannel
import com.iries.youtubealarm.data.Video
import com.iries.youtubealarm.domain.constants.DURATION
import com.iries.youtubealarm.domain.constants.ORDER
import java.io.IOException
import java.util.function.Consumer

object YoutubeSearch {

    fun getSubscriptions(youtube: YouTube): List<YTChannel> {
        val connectionsResponse: SubscriptionListResponse
        try {
            connectionsResponse = youtube
                .subscriptions()
                .list(mutableListOf("snippet", "contentDetails"))
                .setMine(true)
                .setMaxResults(20L)
                .execute()

            if (!connectionsResponse.isEmpty()) {
                val userSubs: ArrayList<YTChannel> = ArrayList()
                connectionsResponse.items.forEach(Consumer { sub: Subscription? ->
                    if (sub != null)
                        userSubs.add(
                            YTChannel(sub)
                        )
                })
                return userSubs
            } else return emptyList()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun findChannelByKeyword(youTube: YouTube, keyword: String): ArrayList<YTChannel> {
        val channels: ArrayList<YTChannel> = ArrayList()
        try {
            val searchListResponse = youTube.search()
                .list(listOf("snippet"))
                .setQ(keyword)
                .setType(listOf("channel")).execute()
            val results = searchListResponse.items

            if (results.isNotEmpty()) {

                results.forEach(Consumer { result: SearchResult? ->
                    if (result != null)
                        channels.add(
                            YTChannel(result)
                        )
                })
                return channels
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return channels
    }

    fun findVideoByFilters(
        youTube: YouTube,
        channelId: String?,
        order: ORDER, duration: DURATION
    ): List<Video>? {
        try {
            val searchListResponse = youTube.search()
                .list(listOf("snippet"))
                .setChannelId(channelId)
                .setOrder(order.getOrderName())
                .setVideoDuration(duration.getDurationName())
                .execute()

            val results = searchListResponse.items

            if (results.isNotEmpty()) {
                val videoList: MutableList<Video> = ArrayList()
                results.forEach(Consumer { result: SearchResult? ->
                    if (result != null)
                        videoList.add(
                            Video(result)
                        )
                })
                return videoList
            } else return null
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}