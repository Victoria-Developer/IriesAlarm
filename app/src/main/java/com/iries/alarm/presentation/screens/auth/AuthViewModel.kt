package com.iries.alarm.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun authorizeUser(code: String) = viewModelScope.launch(Dispatchers.IO) {
        authUseCase.authorize(code).onSuccess {
           updateAuthState(AuthState.AuthorizedFirstTime)
        }
    }

    fun updateAuthState(authState: AuthState){
        _state.update { authState }
    }
}