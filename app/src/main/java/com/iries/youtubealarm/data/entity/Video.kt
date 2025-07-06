package com.iries.youtubealarm.data.entity

import java.io.Serializable

class Video(private val id: String? = null,
            private val title: String? = null) : Serializable {

    fun getId(): String? {
        return id
    }

    fun getTitle(): String? {
        return title
    }
}