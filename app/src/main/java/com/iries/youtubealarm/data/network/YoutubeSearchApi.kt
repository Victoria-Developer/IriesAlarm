package com.iries.youtubealarm.data.network

import android.net.Uri
import com.iries.youtubealarm.BuildConfig
import com.iries.youtubealarm.domain.constants.Duration
import com.iries.youtubealarm.domain.constants.Order
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

    suspend fun findVideoByFilters(
        channelId: String,
        order: Order, duration: Duration
    ): Result<String> {
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
                Result.success(response.bodyAsText())
            else Result.failure(NullPointerException())
        } catch (e: IOException) {
            return Result.failure(e)
        }
    }

    fun videoUrlToAudio(videoURL: String): Result<Uri> {
        val request = YoutubeDLRequest(videoURL)
        request.addOption("--extract-audio")
        val streamInfo: VideoInfo
        try {
            streamInfo = YoutubeDL.getInstance().getInfo(request)
            return Result.success(Uri.parse(streamInfo.url))
        } catch (e: YoutubeDLException) {
            return Result.failure(e)
        } catch (e: InterruptedException) {
            return Result.failure(e)
        } catch (e: YoutubeDL.CanceledException) {
            return Result.failure(e)
        }

    }

}