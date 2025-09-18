package com.iries.alarm.data.remote

import com.iries.alarm.domain.models.AuthData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import javax.inject.Inject

class AuthRepository @Inject constructor(private val httpClient: HttpClient) {

    private val baseAuthUrl = "https://api.iriesdev.workers.dev"

    @Serializable
    data class AuthRequest(
        val code: String
    )

    @Serializable
    data class TokenRefreshRequest(
        val refreshToken: String
    )

    @Serializable
    data class AuthResponse(
        val success: Boolean,
        val data: AuthData
    )

    suspend fun exchangeAccessToken(code: String): Result<AuthData> {
        return try {
            val response: AuthResponse = httpClient.post(
                "$baseAuthUrl/alarm/user/auth"
            ) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(code))
            }.body()
            if (response.data.accessToken.isEmpty())
                return Result.failure(NullPointerException())
            Result.success(response.data)
        } catch (e: Exception) {
            println("Token exchange failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun refreshAccessToken(refreshToken: String): Result<AuthData> {
        return try {
            val response: AuthResponse = httpClient.post(
                "$baseAuthUrl/alarm/user/token/refresh"
            ) {
                contentType(ContentType.Application.Json)
                setBody(TokenRefreshRequest(refreshToken))
            }.body()
            if (response.data.accessToken.isEmpty())
                return Result.failure(NullPointerException())
            Result.success(response.data)

        } catch (e: Exception) {
            println("Token refreshment failed: ${e.message}")
            Result.failure(e)
        }
    }
}