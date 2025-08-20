package com.iries.youtubealarm.data.network

import com.iries.youtubealarm.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class YoutubeSearchApi @Inject constructor(private val httpClient: HttpClient) {

    suspend fun getSubscriptions(accessToken: String): Result<String> {
        val url = "https://www.googleapis.com/youtube/v3/subscriptions?part=snippet&mine=true&" +
                "maxResults=20&access_token=${accessToken}&" +
                "key=${BuildConfig.youtube_api_key}"
        try {
            val response: HttpResponse = httpClient.get(url) {
                contentType(ContentType.Application.Json)
            }
            val status = response.status
            return if (status == HttpStatusCode.OK)
                Result.success(response.bodyAsText())
            else Result.failure(NullPointerException())
        } catch (e: IOException) {
            return Result.failure(e)
        }
    }

    suspend fun findChannelByKeyword(keyword: String): Result<String> {
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
                Result.success(response.bodyAsText())
            else Result.failure(NullPointerException())
        } catch (e: IOException) {
            return Result.failure(e)
        }
    }

    suspend fun getUploadsId(channelId: String): Result<String> {
        val url = "https://www.googleapis.com/youtube/v3/channels"

        try {
            val response: HttpResponse = httpClient.get(url) {
                parameter("part", "contentDetails")
                parameter("id", channelId)
                parameter("key", BuildConfig.youtube_api_key)
                contentType(ContentType.Application.Json)
            }
            return if (response.status == HttpStatusCode.OK)
                Result.success(response.bodyAsText())
            else Result.failure(NullPointerException())
        } catch (e: IOException) {
            return Result.failure(e)
        }
    }

    suspend fun getPlaylistItems(playlistId: String): Result<String> {
        val url = "https://www.googleapis.com/youtube/v3/playlistItems"

        try {
            val response: HttpResponse = httpClient.get(url) {
                parameter("part", "snippet")
                parameter("maxResults", 5)
                parameter("playlistId", playlistId)
                parameter("key", BuildConfig.youtube_api_key)
                contentType(ContentType.Application.Json)
            }
            return if (response.status == HttpStatusCode.OK)
                Result.success(response.bodyAsText())
            else Result.failure(NullPointerException())
        } catch (e: IOException) {
            return Result.failure(e)
        }
    }

}