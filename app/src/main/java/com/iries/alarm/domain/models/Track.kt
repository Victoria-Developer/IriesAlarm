package com.iries.alarm.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: Long,
    val title: String,
    @SerialName("artwork_url") val imgUrl: String? = null,
    @SerialName("streamable") val isStreamable: Boolean,
)