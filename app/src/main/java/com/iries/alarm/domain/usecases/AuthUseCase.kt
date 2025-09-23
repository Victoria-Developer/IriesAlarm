package com.iries.alarm.domain.usecases

import com.iries.alarm.data.local.AuthStore
import com.iries.alarm.data.remote.AuthRepository
import com.iries.alarm.domain.models.AuthData
import io.ktor.util.date.getTimeMillis
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private var authStore: AuthStore,
) {

    private var authData: AuthData = AuthData()

    init {
        authData = authStore.loadAuthData()
    }

    fun isUserLoggedIn(): Boolean {
        return authStore.containsAuthData()
    }

    private fun saveAuthData(authData: AuthData) {
        this.authData = authData
        authStore.saveAuthData(authData)
    }

    suspend fun authorize(code: String): Result<String> {
        return authRepository.exchangeAccessToken(code).fold(
            onSuccess = {
                saveAuthData(it)
                Result.success(authData.accessToken)
            },
            onFailure = {
                Result.failure(it)
            })
    }

    private suspend fun refreshAccessToken(refreshToken: String): Result<AuthData> {
        return authRepository.refreshAccessToken(refreshToken).onSuccess {
            saveAuthData(it)
        }
    }

    suspend fun getAccessToken(): Result<String> {
        val isExpired = authData.accessToken.isEmpty() ||
                (getTimeMillis() - authData.timeStamp >= authData.expiresIn)

        if (isExpired) {
            return refreshAccessToken(authData.refreshToken)
                .map { it.accessToken }
        }
        return Result.success(authData.accessToken)
    }
}