package com.iries.alarm.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: Long,
    val username: String,
    @SerialName("avatar_url") val imgUrl: String = ""
)