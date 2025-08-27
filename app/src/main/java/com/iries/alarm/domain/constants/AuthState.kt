package com.iries.alarm.domain.constants

sealed class AuthState {
    data object Loading : AuthState()
    data object RequiresLogin : AuthState()
    data object Authorized : AuthState()
    data object AuthorizedFirstTime : AuthState()
}