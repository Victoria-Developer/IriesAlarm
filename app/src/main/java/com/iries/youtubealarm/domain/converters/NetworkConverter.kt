package com.iries.youtubealarm.domain.converters

import com.google.gson.JsonParser
import com.iries.youtubealarm.data.entity.YTChannel
import com.iries.youtubealarm.domain.models.Video

object NetworkConverter {

    fun parseSubsResponse(jsonString: String): List<YTChannel> {
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val itemsArray = jsonObject.getAsJsonArray("items")

        return itemsArray.map {
            val snippet = it.asJsonObject.getAsJsonObject("snippet")
            val channelId = snippet.get("resourceId").asJsonObject.get("channelId").asString
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

    fun parseChannelsResponse(jsonString: String): List<YTChannel> {
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

    fun parseVideoResponse(jsonString: String): List<Video> {
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
}