package com.iries.youtubealarm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.api.services.youtube.model.SearchResult
import com.google.api.services.youtube.model.SearchResultSnippet
import com.google.api.services.youtube.model.Subscription
import com.google.api.services.youtube.model.SubscriptionSnippet
import java.io.Serializable

@Entity(tableName = "CHANNELS")
class YTChannel() :
    Serializable {

    @PrimaryKey(autoGenerate = true)
    private var id: Long = 0
    private var title: String = "No title"
    private var channelId: String? = null
    private var uploadsId: String? = null
    private var iconUrl: String? = null

    // should be moved to network
    constructor(searchResult: SearchResult) : this() {
        val snippet: SearchResultSnippet = searchResult.snippet
        this.title = snippet.channelTitle
        this.channelId = snippet.channelId
        this.iconUrl = snippet.thumbnails.default.url
        generateUploadsId()
    }

    // should be moved to network
    constructor(subscription: Subscription) : this() {
        val snippet: SubscriptionSnippet = subscription.snippet
        this.title = snippet.title
        this.channelId = snippet.resourceId.channelId
        this.iconUrl = snippet.thumbnails.default.url
        this.uploadsId = snippet.resourceId.playlistId
    }

    private fun generateUploadsId() {
        this.uploadsId =
            if (channelId.isNullOrEmpty())
                null
            else (channelId!!.substring(0, 1)
                    + 'U' + channelId!!.substring(5))
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getTitle(): String {
        return title
    }

    fun setIconUrl(iconUrl: String) {
        this.iconUrl = iconUrl
    }

    fun getIconUrl(): String? {
        return iconUrl
    }

    fun setChannelId(channelId: String) {
        this.channelId = channelId
    }

    fun getChannelId(): String? {
        return channelId
    }

    fun setUploadsId(uploadsId: String) {
        this.uploadsId = uploadsId
    }

    fun getUploadsId(): String? {
        return uploadsId
    }

    fun setId(id: Long) {
        this.id = id
    }

    fun getId(): Long {
        return id
    }
}