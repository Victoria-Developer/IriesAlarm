package com.iries.youtubealarm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "CHANNELS")
class YTChannel(
    private var title: String? = null,
    private var channelId: String? = null,
    private var uploadsId: String? = null,
    private var iconUrl: String? = null,
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    private var id: Long = 0

    fun setTitle(title: String) {
        this.title = title
    }

    fun getTitle(): String? {
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