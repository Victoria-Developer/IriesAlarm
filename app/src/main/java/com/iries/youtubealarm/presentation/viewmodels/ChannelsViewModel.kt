package com.iries.youtubealarm.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iries.youtubealarm.data.entity.YTChannel
import com.iries.youtubealarm.data.repository.ChannelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelsViewModel @Inject constructor(
    private val channelsRepo: ChannelsRepository
) : ViewModel() {
    private var allChannels: LiveData<List<YTChannel>> = channelsRepo.getAllChannels()

    fun getAllChannels(): LiveData<List<YTChannel>> {
        return allChannels
    }

    fun insert(ytChannel: YTChannel) {
        viewModelScope.launch(Dispatchers.IO) {
            channelsRepo.insert(ytChannel)
        }
    }

    fun remove(ytChannel: YTChannel) {
        viewModelScope.launch(Dispatchers.IO) {
            channelsRepo.delete(ytChannel)
        }
    }

}