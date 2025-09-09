package com.iries.alarm.data.remote

import com.iries.alarm.domain.models.AuthData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import java.util.Base64
import javax.inject.Inject

class AuthRepository @Inject constructor(private val httpClient: HttpClient) {

    private val baseAuthUrl = "https://api.iriesdev.workers.dev/alarm/user"

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

    /*suspend fun authorizeApp(clientId: String, clientSecret: String) {
        val credentials = "${clientId}:${clientSecret}"
        val basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.toByteArray(Charsets.UTF_8))
        return try {
            val response = httpClient.post("https://secure.soundcloud.com/oauth/token") {
                header(HttpHeaders.Authorization, basicAuth)
                header(HttpHeaders.Accept, "application/json; charset=utf-8")
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(FormDataContent(Parameters.build {
                    append("grant_type", "client_credentials")
                    append("scope", "non-expiring")
                }))
            }
            println(response.bodyAsText())

        } catch (e: Exception) {
            println("Token exchange failed: ${e.message}")
        }
    }*/

    suspend fun exchangeAccessToken(code: String): Result<AuthData> {
        return try {
            val response = httpClient.post(
                "$baseAuthUrl/auth"
            ) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(code))
            }
            println(response.bodyAsText())
            val i: AuthResponse = response.body()
            if (i.data.accessToken.isEmpty())
                return Result.failure(NullPointerException())
            Result.success(i.data)

        } catch (e: Exception) {
            println("Token exchange failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun refreshAccessToken(refreshToken: String): Result<AuthData> {
        return try {
            val response: AuthResponse = httpClient.post(
                "$baseAuthUrl/token/refresh"
            ) {
                contentType(ContentType.Application.Json)
                setBody(TokenRefreshRequest(refreshToken))
            }.body()
            if (response.data.accessToken.isEmpty())
                return Result.failure(NullPointerException())
            println("Refreshed access token: ${response.data.accessToken}")
            Result.success(response.data)

        } catch (e: Exception) {
            println("Token refreshment failed: ${e.message}")
            Result.failure(e)
        }
    }
}