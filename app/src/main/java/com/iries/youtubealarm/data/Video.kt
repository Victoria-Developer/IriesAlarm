package com.iries.youtubealarm.data

import com.google.api.services.youtube.model.SearchResult

class Video(result: SearchResult) {
    private var id: String
    private var title: String

    /*constructor(id: String?, title: String?) {
        this.id = id
        this.title = title
    }*/

    init {
        val snippet = result.snippet
        this.id = result.id.videoId
        this.title = snippet.title
    }

    fun getId(): String {
        return id
    }

    fun getTitle(): String {
        return title
    }
}