package com.iries.alarm.presentation.screens.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iries.alarm.data.local.AuthStore
import com.iries.alarm.domain.constants.AuthState
import com.iries.alarm.domain.models.Artist
import com.iries.alarm.domain.models.AuthData
import com.iries.alarm.domain.usecases.SoundCloudApiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicSearchViewModel @Inject constructor(
    private val soundCloudApiUseCase: SoundCloudApiUseCase,
    private val authStore: AuthStore,
) : ViewModel() {

    private val _dbArtists = MutableStateFlow<List<Artist>>(emptyList())
    val dbArtists: StateFlow<List<Artist>> = _dbArtists

    private val _visibleArtists = MutableStateFlow<List<Artist>?>(emptyList())
    val visibleArtists: StateFlow<List<Artist>?> = _visibleArtists

    private val _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?> = _error

    private val _isFetchRequest = MutableStateFlow(false)
    val isFetchRequest: StateFlow<Boolean> = _isFetchRequest

    private val _authorizationStatus = MutableStateFlow<AuthState>(AuthState.Loading)
    val authorizationStatus: StateFlow<AuthState> = _authorizationStatus

    private var authData: AuthData = AuthData()

    init {
        populateArtists()
        initAuthData()
    }

    private fun populateArtists() = viewModelScope.launch(Dispatchers.IO) {
        soundCloudApiUseCase.getAllArtists().collect { artists ->
            _dbArtists.update { artists }
            if (_visibleArtists.value.isNullOrEmpty()) {
                _visibleArtists.update { artists }
            }
        }
    }

    private fun initAuthData() = viewModelScope.launch(Dispatchers.IO) {
        authData = authStore.loadAuthData()
        _authorizationStatus.update {
            if(authData.accessToken.isNotEmpty()) AuthState.Authorized
            else AuthState.Unauthorized
        }
    }

    private fun setAuthData(authData: AuthData) {
        this.authData = authData
        authStore.saveAuthData(authData)
    }

    fun authorize(code: String?) = viewModelScope.launch(Dispatchers.IO) {
        _authorizationStatus.update { AuthState.Loading }
        if (code == null) {
            updateError(Exception("No auth code provided."))
        } else {
            soundCloudApiUseCase.authorize(code)
                .onSuccess {
                    setAuthData(it)
                    _authorizationStatus.update { AuthState.Authorized }
                }
                .onFailure { updateError(it) }
        }
    }

    fun logout() = viewModelScope.launch(Dispatchers.IO) {
        _authorizationStatus.update { AuthState.Loading }
        soundCloudApiUseCase.logout(authData.accessToken)
        _authorizationStatus.update { AuthState.Unauthorized }
        setAuthData(AuthData())
    }

    // to backend
    private suspend fun checkAccessToken() {
        val isExpired = (getTimeMillis() - authData.timeStamp >= authData.expiresIn)
        if (isExpired) {
            soundCloudApiUseCase.refreshAccessToken(authData.refreshToken)
                .onSuccess { setAuthData(it) }
                .onFailure {
                    updateError(it)
                    logout()
                }
        }
    }

    fun saveArtist(artist: Artist) = viewModelScope.launch(Dispatchers.IO) {
        soundCloudApiUseCase.insert(artist)
    }

    fun removeArtist(artist: Artist) = viewModelScope.launch(Dispatchers.IO) {
        soundCloudApiUseCase.delete(artist)
    }

    fun showSavedArtists() {
        _visibleArtists.update { dbArtists.value }
    }

    fun showSubscriptions() = viewModelScope.launch(Dispatchers.IO) {
        checkAccessToken()
        search {
            soundCloudApiUseCase.findUserSubscriptions(authData.accessToken)
        }
    }

    fun showArtistsByName(artistName: String) = viewModelScope.launch(Dispatchers.IO) {
        search {
            soundCloudApiUseCase.findArtistsByName(artistName)
        }
    }

    private suspend fun search(apiSearchMethod: suspend () -> Result<List<Artist>>) {
        _isFetchRequest.update { true }
        _error.update { null }

        var artists: List<Artist>? = null
        apiSearchMethod().onSuccess {
            artists = it
        }.onFailure {
            updateError(it)
        }

        _visibleArtists.update { artists }
        _isFetchRequest.update { false }
    }

    fun updateError(error: Throwable?) {
        _error.update { error }
    }

}