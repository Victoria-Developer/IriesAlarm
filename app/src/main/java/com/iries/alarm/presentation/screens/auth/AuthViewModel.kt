package com.iries.alarm.presentation.screens.auth

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iries.alarm.BuildConfig
import com.iries.alarm.domain.AuthState
import com.iries.alarm.domain.usecases.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    val state: StateFlow<AuthState> = _state

    init {
        if (authUseCase.isUserLoggedIn()) {
            updateAuthState(AuthState.Authorized)
        } else {
            updateAuthState(AuthState.RequiresLogin)
        }
    }

    private fun updateAuthState(authState: AuthState) {
        _state.update { authState }
    }

    fun authorizeUser(code: String) = viewModelScope.launch(Dispatchers.IO) {
        updateAuthState(AuthState.Loading)
        authUseCase.authorize(code).onSuccess {
            updateAuthState(AuthState.AuthorizedFirstTime)
        }.onFailure {
            updateAuthState(AuthState.RequiresLogin)
        }
    }

    fun login(context: Context) {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        val loginUrl = ("https://secure.soundcloud.com/authorize?" +
                "client_id=${BuildConfig.client_id}" +
                "&redirect_uri=${BuildConfig.redirect_uri}" +
                "&response_type=code" +
                "&state=STATE" +
                "&display=popup").toUri()
        try {
            customTabsIntent.launchUrl(context, loginUrl)
        } catch (e: Exception) {
            context.startActivity(Intent(Intent.ACTION_VIEW, loginUrl))
        }
    }
}