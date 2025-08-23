package com.iries.youtubealarm.presentation.screens.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iries.youtubealarm.data.repository.ArtistsRepository
import com.iries.youtubealarm.domain.models.Artist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicSearchViewModel @Inject constructor(
    private val artistsRepository: ArtistsRepository
) : ViewModel() {

    private val _dbArtists = MutableStateFlow<List<Artist>>(emptyList())
    val dbArtists: StateFlow<List<Artist>> = _dbArtists

    private val _visibleArtists = MutableStateFlow<List<Artist>?>(arrayListOf())
    val visibleArtists: StateFlow<List<Artist>?> = _visibleArtists

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _isFetchRequest = MutableStateFlow(false)
    val isFetchRequest: StateFlow<Boolean> = _isFetchRequest

    init {
        viewModelScope.launch {
            artistsRepository.getAllArtists().collect { artists ->
                _dbArtists.update { artists }
                if (_visibleArtists.value.isNullOrEmpty()) {
                    _visibleArtists.update { artists }
                }
            }
        }
    }

    fun saveArtist(artist: Artist) = viewModelScope.launch(Dispatchers.IO) {
        artistsRepository.insert(artist)
    }

    fun removeArtist(artist: Artist) = viewModelScope.launch(Dispatchers.IO) {
        artistsRepository.delete(artist)
    }

    fun showSavedArtists() {
        _visibleArtists.update { dbArtists.value }
    }

    fun showArtistsByName(name: String) = viewModelScope.launch(Dispatchers.IO) {
        _isFetchRequest.update { true }

        var artists: List<Artist>? = null
        artistsRepository.findArtistsByName(name)
            .onSuccess {
                artists = it
            }.onFailure {
                _isError.update { true }
            }

        _visibleArtists.update { artists }
        _isFetchRequest.update { false }
    }

    fun updateError(isError: Boolean) {
        _isError.update { isError }
    }

}