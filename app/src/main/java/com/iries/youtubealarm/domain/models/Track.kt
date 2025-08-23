package com.iries.youtubealarm.domain.models

class Track {
    var id: Long = 0
    var title: String = "Unnamed"
    var imgUrl: String? = null
    var isStreamable: Boolean = false
    var progressiveUrl: String? = null
}