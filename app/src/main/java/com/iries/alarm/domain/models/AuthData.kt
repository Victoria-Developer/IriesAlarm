package com.iries.alarm.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    @SerialName("access_token")
    val accessToken: String = "", // 1 hour lifespan

    @SerialName("refresh_token")
    val refreshToken: String = "",

    @SerialName("time_stamp")
    var timeStamp: Long = 0,

    @SerialName("expires_in")
    var expiresIn: Long = 0
)