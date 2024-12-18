package com.iries.youtubealarm.domain.models

class Video(private val id: String,
            private val title: String) {

    fun getId(): String {
        return id
    }

    fun getTitle(): String {
        return title
    }
}