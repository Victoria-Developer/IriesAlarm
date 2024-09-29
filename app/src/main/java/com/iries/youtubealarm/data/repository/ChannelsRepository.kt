package com.iries.youtubealarm.data.repository

import androidx.lifecycle.LiveData
import com.iries.youtubealarm.data.dao.ChannelsDao
import com.iries.youtubealarm.data.entity.YTChannel
import javax.inject.Inject

class ChannelsRepository @Inject constructor(private var channelsDao: ChannelsDao) {
    private var allChannels: LiveData<List<YTChannel>> = channelsDao.getAllChannels()

    fun insert(channel: YTChannel) {
        channelsDao.insert(channel)
    }

    fun update(channel: YTChannel) {
        channelsDao.update(channel)
    }

    fun delete(channel: YTChannel) {
        channelsDao.delete(channel)
    }

    fun deleteAll() {
        channelsDao.deleteAll()
    }

    fun getAllChannels(): LiveData<List<YTChannel>> {
        return allChannels
    }

}