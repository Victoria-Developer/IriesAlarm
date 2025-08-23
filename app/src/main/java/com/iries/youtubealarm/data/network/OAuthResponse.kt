package com.iries.youtubealarm.data.network

import io.ktor.util.date.getTimeMillis
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OAuthResponse(
    @SerialName("access_token")
    val accessToken: String, // 1 hour lifespan

    @SerialName("refresh_token")
    val refreshToken: String,

    var accessTokenTimeStamp: Long = getTimeMillis(),

    @SerialName("expires_in")
    var expiresIn: Long = 1000 * 60 * 60
)