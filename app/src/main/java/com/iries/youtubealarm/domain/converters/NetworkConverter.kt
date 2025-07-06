package com.iries.youtubealarm.domain.converters

import com.google.gson.JsonParser
import com.iries.youtubealarm.data.entity.YTChannel
import com.iries.youtubealarm.data.entity.Video

object NetworkConverter {

    fun parseSubsResponse(jsonString: String): List<YTChannel> {
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val itemsArray = jsonObject.getAsJsonArray("items")

        return itemsArray.map {
            val snippet = it.asJsonObject.getAsJsonObject("snippet")
            val channelId = snippet.get("resourceId").asJsonObject.get("channelId").asString
            val thumbnails = snippet
                .getAsJsonObject("thumbnails")
                .getAsJsonObject("default")

            YTChannel(
                title = snippet.get("title").asString,
                channelId = channelId,
                iconUrl = thumbnails.get("url").asString
            )
        }
    }

    fun parseChannelsResponse(jsonString: String): List<YTChannel> {
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val itemsArray = jsonObject.getAsJsonArray("items")

        return itemsArray.map {
            val snippet = it.asJsonObject.getAsJsonObject("snippet")
            val channelId = snippet.get("channelId").asString
            val thumbnails = snippet
                .getAsJsonObject("thumbnails")
                .getAsJsonObject("default")

            YTChannel(
                title = snippet.get("title").asString,
                channelId = channelId,
                iconUrl = thumbnails.get("url").asString
            )
        }
    }

    fun parseUploadsPlaylistResponse(jsonString: String):String{
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val itemsArray = jsonObject.getAsJsonArray("items")

        return itemsArray[0].asJsonObject
            .getAsJsonObject("contentDetails")
            .getAsJsonObject("relatedPlaylists")
            .get("uploads").asString
    }

    fun parsePlaylistItemResponse(jsonString: String): List<Video> {
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val itemsArray = jsonObject.getAsJsonArray("items")

        return itemsArray.map { item ->
            val itemObject = item.asJsonObject
            val snippet = itemObject.getAsJsonObject("snippet")
            val id = snippet
                .getAsJsonObject("resourceId")
                .get("videoId").asString

            Video(
                id = id,
                title = snippet.get("title").asString,
            )
        }
    }
}