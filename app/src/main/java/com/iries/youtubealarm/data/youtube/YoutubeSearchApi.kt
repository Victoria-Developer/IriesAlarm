package com.iries.youtubealarm.data.youtube

import android.net.Uri
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Subscription
import com.google.api.services.youtube.model.SubscriptionListResponse
import com.google.api.services.youtube.model.SubscriptionSnippet
import com.google.gson.JsonParser
import com.iries.youtubealarm.BuildConfig
import com.iries.youtubealarm.data.entity.YTChannel
import com.iries.youtubealarm.domain.constants.Duration
import com.iries.youtubealarm.domain.constants.Order
import com.iries.youtubealarm.domain.models.Video
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.mapper.VideoInfo
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.io.IOException
import javax.inject.Inject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class YoutubeSearchApi @Inject constructor(private val httpClient: HttpClient) {

    fun getSubscriptions(youtube: YouTube): List<YTChannel>? {
        val connectionsResponse: SubscriptionListResponse
        try {
            connectionsResponse = youtube
                .subscriptions()
                .list(mutableListOf("snippet", "contentDetails"))
                .setMine(true)
                .setMaxResults(20L)
                .execute()

            return if (!connectionsResponse.isEmpty())
                return connectionsResponse
                    .items.map { parseSubsResponse(it) }
            else {
                println("Connection response is empty.")
                null
            }
        } catch (e: IOException) {
            println(e)
            return null
        }
    }

    private fun parseSubsResponse(subscription: Subscription): YTChannel {
        val snippet: SubscriptionSnippet = subscription.snippet
        return YTChannel(
            title = snippet.title,
            channelId = snippet.resourceId.channelId,
            iconUrl = snippet.thumbnails.default.url,
            uploadsId = snippet.resourceId.playlistId
        )

    }

    suspend fun findChannelByKeyword(keyword: String): List<YTChannel>? {
        val encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.toString())
        val url = "https://www.googleapis.com/youtube/v3/search?" +
                "part=snippet&maxResults=20&q=$encodedKeyword&type=channel" +
                "&key=${BuildConfig.youtube_api_key}"

        try {
            val response: HttpResponse = httpClient.get(url) {
                contentType(ContentType.Application.Json)
            }
            val status = response.status
            return if (status == HttpStatusCode.OK)
                parseChannelsResponse(response.bodyAsText())
            else {
                println(status)
                null
            }
        } catch (e: IOException) {
            println(e)
            return null
        }
    }

    private fun parseChannelsResponse(jsonString: String): List<YTChannel> {
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val itemsArray = jsonObject.getAsJsonArray("items")

        return itemsArray.map {
            val snippet = it.asJsonObject.getAsJsonObject("snippet")
            val channelId = snippet.get("channelId").asString
            val uploadsId = (channelId.substring(0, 1)
                    + 'U' + channelId.substring(5))
            val thumbnails = snippet
                .getAsJsonObject("thumbnails")
                .getAsJsonObject("default")

            YTChannel(
                title = snippet.get("title").asString,
                channelId = channelId,
                uploadsId = uploadsId,
                iconUrl = thumbnails.get("url").asString
            )
        }
    }

    suspend fun findVideoByFilters(
        channelId: String,
        order: Order, duration: Duration
    ): List<Video>? {
        val url = "https://www.googleapis.com/youtube/v3/search"

        try {
            val response: HttpResponse = httpClient.get(url) {
                parameter("part", "snippet")
                parameter("maxResults", 5)
                parameter("order", order)
                parameter("duration", duration)
                parameter("type", "videos")
                parameter("channelId", channelId)
                parameter("key", BuildConfig.youtube_api_key)
                contentType(ContentType.Application.Json)
            }
            return if (response.status == HttpStatusCode.OK)
                parseVideoResponse(response.bodyAsText())
            else null
        } catch (e: IOException) {
            println(e)
            return null
        }
    }

    private fun parseVideoResponse(jsonString: String): List<Video> {
        println(jsonString)
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val itemsArray = jsonObject.getAsJsonArray("items")

        return itemsArray.map { item ->
            val itemObject = item.asJsonObject
            val snippet = itemObject.getAsJsonObject("snippet")
            val id = itemObject.getAsJsonObject("id").get("videoId").asString

            Video(
                id = id,
                title = snippet.get("title").asString,
            )
        }
    }

    fun extractAudio(videoURL: String): Uri {
        val request = YoutubeDLRequest(videoURL)
        request.addOption("--extract-audio")
        val streamInfo: VideoInfo
        try {
            streamInfo = YoutubeDL.getInstance().getInfo(request)
        } catch (e: YoutubeDLException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } catch (e: YoutubeDL.CanceledException) {
            throw RuntimeException(e)
        }
        return Uri.parse(streamInfo.url)
    }

}