package com.iries.youtubealarm.data.network

import com.iries.youtubealarm.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.util.date.getTimeMillis
import java.io.IOException
import java.net.URLEncoder
import java.util.Base64
import javax.inject.Inject

class SoundCloudApiService @Inject constructor(private val httpClient: HttpClient) {
    private val apiBaseUrl = "https://api.soundcloud.com"
    private val oathUrl = "https://secure.soundcloud.com/oauth/token"
    private var oathData: OAuthResponse? = null

    private fun isAccessTokenExpired(): Boolean {
        return oathData == null
                || getTimeMillis() - oathData!!.accessTokenTimeStamp >= oathData!!.expiresIn
    }

    private suspend fun requestAccessToken() {
        try {
            println("Requesting new oath token")
            val credentials = "${BuildConfig.client_id}:${BuildConfig.client_secret}"
            val basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.toByteArray())

            val response: HttpResponse =
                httpClient.post(oathUrl) {
                    header(HttpHeaders.Authorization, basicAuth)
                    header(HttpHeaders.Accept, "application/json; charset=utf-8")
                    header(HttpHeaders.UserAgent, "Mozilla/5.0")

                    contentType(ContentType.Application.FormUrlEncoded)

                    setBody(FormDataContent(Parameters.build {
                        append("grant_type", "client_credentials")
                    }))
                }
            if (response.status == HttpStatusCode.OK) {
                oathData = response.body()
                println("Access token is ${oathData?.accessToken}")
            } else {
                println("Error ${response.status}: ${response.bodyAsText()}")
            }
        } catch (e: IOException) {
            println("IO Exception: $e")
        }
    }

    private suspend fun getRequest(url: String): Result<String> {
        if (isAccessTokenExpired()) requestAccessToken()

        return try {
            println("Requesting: $url")
            val response: HttpResponse = httpClient.get(url) {
                header("Authorization", "OAuth ${oathData?.accessToken}")
                header("User-Agent", "Mozilla/5.0")
            }
            if (response.status == HttpStatusCode.OK) {
                val body = response.bodyAsText()
                if(body.isEmpty())
                    return Result.failure(NullPointerException())
                println("Success! $body")
                Result.success(body)
            } else {
                println("Error ${response.status}: ${response.bodyAsText()}")
                Result.failure(Exception("HTTP ${response.status}"))
            }
        } catch (e: IOException) {
            println("IO Exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun findTracksByGenre(genres:List<String>): Result<String> {
        val url = "$apiBaseUrl/tracks?" +
                "genres=${genres.joinToString(separator=",")}"
        return getRequest(url)
    }

    suspend fun findArtistsByName(query: String): Result<String> {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "$apiBaseUrl/users?" +
                "q=$encodedQuery&limit=10&offset=0&linked_partitioning=true"
        return getRequest(url)
    }

    suspend fun findTracksByArtist(userId: Long): Result<String> {
        val url = "$apiBaseUrl/users/$userId/tracks?"
        return getRequest(url)
    }

    suspend fun resolveAudioUrl(mediaUrl: String): Result<String> {
        val url = "$mediaUrl?"
        return getRequest(url)
    }

}