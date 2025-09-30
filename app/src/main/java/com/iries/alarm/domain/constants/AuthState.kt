package com.iries.alarm.domain.constants

sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthorized : AuthState()
    data object Authorized : AuthState()
}