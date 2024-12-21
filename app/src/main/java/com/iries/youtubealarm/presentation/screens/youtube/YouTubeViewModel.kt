package com.iries.youtubealarm.presentation.screens.youtube

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iries.youtubealarm.data.entity.YTChannel
import com.iries.youtubealarm.data.repository.ChannelsRepository
import com.iries.youtubealarm.data.youtube.YoutubeAuth
import com.iries.youtubealarm.data.youtube.YoutubeSearchApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YouTubeViewModel @Inject constructor(
    private val channelsRepo: ChannelsRepository,
    private val youtubeSearchApi: YoutubeSearchApi
) : ViewModel() {

    private val _dbChannels = MutableStateFlow<List<YTChannel>>(emptyList())
    private val dbChannels: StateFlow<List<YTChannel>> = _dbChannels

    private val _visibleChannels = MutableStateFlow<List<YTChannel>?>(arrayListOf())
    val visibleChannels: StateFlow<List<YTChannel>?> = _visibleChannels

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _isFetchRequest = MutableStateFlow(false)
    val isFetchRequest: StateFlow<Boolean> = _isFetchRequest

    init {
        viewModelScope.launch {
            channelsRepo.getAllChannels().collect { channels ->
                _dbChannels.update { channels }
                _visibleChannels.update { channels }
            }
        }
    }

    fun getDBChannelById(channelId: String?): YTChannel? {
        return dbChannels.value.firstOrNull { dbChannel ->
            dbChannel.getChannelId() == channelId
        } // Channel in db with the same channelId as in the search result
    }

    fun isDBEmpty(): Boolean {
        return dbChannels.value.isEmpty()
    }

    fun addChannelToDB(
        ytChannel: YTChannel
    ) = viewModelScope.launch(Dispatchers.IO) {
        channelsRepo.insert(ytChannel)
    }

    fun removeChannelFromDB(
        ytChannel: YTChannel
    ) = viewModelScope.launch(Dispatchers.IO) {
        channelsRepo.delete(ytChannel)
    }

    fun showDBChannels() {
        _visibleChannels.update { dbChannels.value }
    }

    fun showChannelsByKeyWord(
        keyWord: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        onSearch {
            youtubeSearchApi.findChannelByKeyword(keyWord)
        }
    }

    fun showSubscriptions(
        context: Context
    ) = viewModelScope.launch(Dispatchers.IO) {
        onSearch {
            val youTube = YoutubeAuth.getYoutube(context)
            youtubeSearchApi.getSubscriptions(youTube)
        }
    }

    private suspend fun onSearch(
        channelsFetchMethod: suspend () -> List<YTChannel>?
    ) {
        _isFetchRequest.update { true }

        val channels = try {
            channelsFetchMethod()
        } catch (e: Exception) {
            _isError.update { true }
            null
        }

        _visibleChannels.update { channels }
        _isFetchRequest.update { false }
    }

    fun updateError(isError: Boolean) {
        _isError.update { isError }
    }

}