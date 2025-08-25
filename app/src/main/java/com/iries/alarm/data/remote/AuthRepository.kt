package com.iries.alarm.data.remote

import com.iries.alarm.BuildConfig
import com.iries.alarm.domain.models.AuthData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import javax.inject.Inject

class AuthRepository @Inject constructor(private val httpClient: HttpClient) {

    private val oathUrl = "https://secure.soundcloud.com/oauth/token"

    suspend fun exchangeAccessToken(code: String): Result<AuthData> {
        println("Code is $code")
        return try {
            val response  = httpClient.post(oathUrl) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("client_id", BuildConfig.client_id)
                            append("client_secret", BuildConfig.client_secret)
                            append("redirect_uri", "iriesalarm://callback")
                            append("grant_type", "authorization_code")
                            append("code", code)
                        }
                    )
                )
            }
            println(response.bodyAsText())
            Result.success(response.body())

        } catch (e: Exception) {
            println("Token exchange failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun refreshAccessToken(refreshToken: String): Result<AuthData> {
        return try {
            val response: AuthData = httpClient.post(oathUrl) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("client_id", BuildConfig.client_id)
                            append("client_secret", BuildConfig.client_secret)
                            append("grant_type", "refresh_token")
                            append("refresh_token", refreshToken)
                        }
                    )
                )
            }.body()
            println("Refreshed access token: ${response.accessToken}")
            Result.success(response)

        } catch (e: Exception) {
            println("Token refreshment failed: ${e.message}")
            Result.failure(e)
        }
    }
}