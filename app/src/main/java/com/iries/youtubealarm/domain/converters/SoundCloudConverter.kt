package com.iries.youtubealarm.domain.converters

import com.google.gson.JsonParser
import com.iries.youtubealarm.domain.models.Artist
import com.iries.youtubealarm.domain.models.Track

object SoundCloudConverter {

    fun parseArtistsResponse(jsonString: String): List<Artist> {
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val itemsArray = jsonObject.getAsJsonArray("collection")
        return itemsArray.map {
            val artistInfo = it.asJsonObject
            Artist().apply {
                id = artistInfo.get("id").asLong
                username = artistInfo.get("username").asString
                imgUrl = artistInfo.get("avatar_url").asString.orEmpty()
            }
        }
    }

    fun parseTracksResponse(jsonString: String): List<Track> {
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val itemsArray = jsonObject.getAsJsonArray("collection")
        return itemsArray.map { item ->
            val trackInfo = item.asJsonObject

            val transcodings = trackInfo.get("media").asJsonObject
                .getAsJsonArray("transcodings")
            val media = transcodings.firstOrNull { transcoding ->
                transcoding.asJsonObject.get("format")
                    .asJsonObject.get("protocol").asString == "progressive"
            }
            val url = media?.asJsonObject?.get("url")

            Track().apply {
                id = trackInfo.get("id").asLong
                title = trackInfo.get("title").asString
                imgUrl = trackInfo.get("artwork_url").asString
                isStreamable = trackInfo.get("streamable").asBoolean
                progressiveUrl = url?.asString
            }
        }
    }

    fun parseResolvedUrl(jsonString: String):String {
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        return jsonObject.get("url").asString
    }

}