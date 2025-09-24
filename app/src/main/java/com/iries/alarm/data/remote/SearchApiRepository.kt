package com.iries.alarm.data.remote

import com.iries.alarm.domain.models.Artist
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.io.IOException
import javax.inject.Inject

class SearchApiRepository @Inject constructor(private val httpClient: HttpClient) {
    private val apiBaseUrl = "https://api.iriesdev.workers.dev/alarm"
    private val json = Json { ignoreUnknownKeys = true }

    @Serializable
    data class RandomTrackResponse(
        val artistId: Long,
        val trackId: Long,
        val trackName: String,
        val streamUrl: String
    )

    private suspend fun getRequest(route: String, requestBody: Map<String, Any>): Result<String> {
        return try {
            println("Requesting: $route")
            val response: HttpResponse = httpClient.post("${apiBaseUrl}$route") {
                //header("Authorization", "OAuth $accessToken")
                contentType(ContentType.Application.Json)
                header("User-Agent", "Mozilla/5.0")
                setBody(requestBody)
            }
            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.bodyAsText()
                if (responseBody.isEmpty())
                    return Result.failure(NullPointerException())
                println("Success! $responseBody")
                Result.success(responseBody)
            } else {
                println("Error ${response.status}: ${response.bodyAsText()}")
                Result.failure(Exception("HTTP ${response.status}"))
            }
        } catch (e: IOException) {
            println("IO Exception: ${e.message}")
            Result.failure(e)
        }
    }

    /** Artists search */
    suspend fun findArtistsByName(artistName: String): Result<List<Artist>> {
        val result = getRequest("/artists", mapOf("artistName" to artistName))
        return result.mapCatching { response ->
            parseArtistsResponse(response)
        }
    }

    suspend fun findUserSubscriptions(accessToken: String): Result<List<Artist>> {
        val result = getRequest("/me/followings", mapOf("accessToken" to accessToken))
        return result.mapCatching { response ->
            parseArtistsResponse(response)
        }
    }

    private fun parseArtistsResponse(jsonString: String): List<Artist> {
        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val collectionArray = jsonObject["collection"]?.jsonArray
        return collectionArray?.map {
            json.decodeFromJsonElement<Artist>(it)
        } ?: emptyList()
    }

    /** Tracks search */
    /*suspend fun findTracksByGenre(genres: List<String>): Result<List<Track>> {
        val url = "/tracks?" +
                "genres=${genres.joinToString(separator = ",")}"
        val result = getRequest(url, mapOf("genres" to genres)
        return result.mapCatching { response ->
            parseTracksResponse(response)
        }
    }*/

    suspend fun findRandomTrackByArtist(artistId: Long): Result<RandomTrackResponse> {
        val result = getRequest(
            "/artists/tracks/random", mapOf("artistId" to artistId)
        )
        return result.mapCatching { response ->
            val data = json.parseToJsonElement(response).jsonObject["data"]
                ?: throw IllegalStateException("Missing 'data' field")
            json.decodeFromJsonElement<RandomTrackResponse>(data)
        }
    }
}