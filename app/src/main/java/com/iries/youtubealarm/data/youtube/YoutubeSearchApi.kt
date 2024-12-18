package com.iries.youtubealarm.data.youtube

import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchResult
import com.google.api.services.youtube.model.Subscription
import com.google.api.services.youtube.model.SubscriptionListResponse
import com.google.api.services.youtube.model.SubscriptionSnippet
import com.google.gson.JsonParser
import com.iries.youtubealarm.BuildConfig
import com.iries.youtubealarm.data.entity.YTChannel
import com.iries.youtubealarm.domain.constants.Duration
import com.iries.youtubealarm.domain.constants.Order
import com.iries.youtubealarm.domain.models.Video
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.io.IOException
import java.util.function.Consumer
import javax.inject.Inject

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
        val url = "https://www.googleapis.com/youtube/v3/search?" +
                "part=snippet&maxResults=20&q=$keyword&type=channel" +
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
        channelId: String?,
        order: Order, duration: Duration
    ): List<Video>? {
        val url = "https://www.googleapis.com/youtube/v3/search?" +
                "channelId=$channelId&part=snippet&maxResults=5&" +
                "order=$order&duration=$duration&type=videos" +
                "&key=${BuildConfig.youtube_api_key}"

        try {
            val response: HttpResponse = httpClient.get(url) {
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
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val itemsArray = jsonObject.getAsJsonArray("items")

        return itemsArray.map {
            val snippet = it.asJsonObject.getAsJsonObject("snippet")
            val id = snippet
                .getAsJsonObject("id")
                .get("videoId").asString

            Video(
                id = id,
                title = snippet.get("title").asString,
            )
        }
    }
}