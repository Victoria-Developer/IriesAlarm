package com.iries.youtubealarm.presentation.screens.youtube

import android.content.Context
import androidx.lifecycle.LiveData
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

    private val dbChannels: LiveData<List<YTChannel>> = channelsRepo.getAllChannels()

    private val _visibleChannels = MutableStateFlow<List<YTChannel>?>(arrayListOf())
    val visibleChannels: StateFlow<List<YTChannel>?> = _visibleChannels

    fun isDBChannel(channelId: String?): YTChannel? {
        return dbChannels.value?.firstOrNull { dbChannel ->
            dbChannel.getChannelId() == channelId
        } // Channel in db with the same channelId as in the search result
    }

    fun isDBEmpty(): Boolean {
        return dbChannels.value.isNullOrEmpty()
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
        val searchResults = youtubeSearchApi.findChannelByKeyword(keyWord)
        _visibleChannels.update { searchResults }
    }

    fun showSubscriptions(
       context: Context
    ) = viewModelScope.launch(Dispatchers.IO) {
        val youTube = YoutubeAuth.getYoutube(context)
        val subs = youtubeSearchApi.getSubscriptions(youTube)
        _visibleChannels.update { subs }
    }

}