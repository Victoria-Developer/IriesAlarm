package com.iries.alarm.data.remote

import com.iries.alarm.domain.models.Artist
import com.iries.alarm.domain.models.Track
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import java.net.URLEncoder
import javax.inject.Inject

class SearchApiRepository @Inject constructor(private val httpClient: HttpClient) {
    private val apiBaseUrl = "https://api.soundcloud.com"
    private val json = Json { ignoreUnknownKeys = true }

    private suspend fun getRequest(route: String, accessToken: String): Result<String> {
        return try {
            println("Requesting: $route")
            val response: HttpResponse = httpClient.get("${apiBaseUrl}$route") {
                header("Authorization", "OAuth $accessToken")
                header("User-Agent", "Mozilla/5.0")
            }
            if (response.status == HttpStatusCode.OK) {
                val body = response.bodyAsText()
                if (body.isEmpty())
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

    /** Artists search */
    suspend fun findArtistsByName(artistName: String, accessToken: String): Result<List<Artist>> {
        val encodedQuery = URLEncoder.encode(artistName, "UTF-8")
        val url = "/users?" +
                "q=$encodedQuery&limit=20&offset=0&linked_partitioning=true"
        val result = getRequest(url, accessToken)
        return result.mapCatching { response ->
            parseArtistsResponse(response)
        }
    }

    suspend fun findUserSubscriptions(accessToken: String):Result<List<Artist>> {
        val result = getRequest( "/me/followings", accessToken)
        return result.mapCatching { response ->
            parseArtistsResponse(response)
        }
    }

    private fun parseArtistsResponse(jsonString: String): List<Artist> {
        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val collectionArray = jsonObject["collection"]!!.jsonArray
        return collectionArray.map {
            json.decodeFromJsonElement<Artist>(it)
        }
    }

    /** Tracks search */
    suspend fun findTracksByGenre(genres: List<String>, accessToken: String): Result<List<Track>> {
        val url = "/tracks?" +
                "genres=${genres.joinToString(separator = ",")}"
        val result = getRequest(url, accessToken)
        return result.mapCatching { response ->
            parseTracksResponse(response)
        }
    }

    suspend fun findArtistTracks(userId: Long, accessToken: String): Result<List<Track>> {
        val result = getRequest("/users/$userId/tracks?", accessToken)
        return result.mapCatching { response ->
            parseTracksResponse(response)
        }
    }

    private fun parseTracksResponse(jsonString: String): List<Track> {
        val itemsArray = json.parseToJsonElement(jsonString).jsonArray
        return itemsArray.map { element ->
            json.decodeFromJsonElement<Track>(element)
        }
    }

    /** Resolve stream url */
    suspend fun resolveStreamUrl(trackId:Long, accessToken: String): Result<String> {
        val result = getRequest("/tracks/$trackId/streams", accessToken)
        return result.mapCatching {
            parseResolvedUrl(it)
        }
    }

    private fun parseResolvedUrl(jsonString: String): String {
        println(jsonString)
        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        return jsonObject["http_mp3_128_url"]!!.jsonPrimitive.content
    }
}