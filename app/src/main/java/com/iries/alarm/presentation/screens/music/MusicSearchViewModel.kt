package com.iries.alarm.presentation.screens.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iries.alarm.domain.models.Artist
import com.iries.alarm.domain.usecases.SearchApiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicSearchViewModel @Inject constructor(
    private val searchApiUseCase: SearchApiUseCase,
) : ViewModel() {

    private val _dbArtists = MutableStateFlow<List<Artist>>(emptyList())
    val dbArtists: StateFlow<List<Artist>> = _dbArtists

    private val _visibleArtists = MutableStateFlow<List<Artist>?>(emptyList())
    val visibleArtists: StateFlow<List<Artist>?> = _visibleArtists

    private val _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?> = _error

    private val _isFetchRequest = MutableStateFlow(false)
    val isFetchRequest: StateFlow<Boolean> = _isFetchRequest

    init {
        populateArtists()
    }

    private fun populateArtists() = viewModelScope.launch(Dispatchers.IO) {
        searchApiUseCase.getAllArtists().collect { artists ->
            _dbArtists.update { artists }
            if (_visibleArtists.value.isNullOrEmpty()) {
                _visibleArtists.update { artists }
            }
        }
    }

    fun saveArtist(artist: Artist) = viewModelScope.launch(Dispatchers.IO) {
        searchApiUseCase.insert(artist)
    }

    fun removeArtist(artist: Artist) = viewModelScope.launch(Dispatchers.IO) {
        searchApiUseCase.delete(artist)
    }

    fun showSavedArtists() {
        _visibleArtists.update { dbArtists.value }
    }

    fun showSubscriptions() = viewModelScope.launch(Dispatchers.IO) {
        search {
            searchApiUseCase.findUserSubscriptions()
        }
    }

    fun showArtistsByName(artistName: String) = viewModelScope.launch(Dispatchers.IO) {
        search {
            searchApiUseCase.findArtistsByName(artistName)
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