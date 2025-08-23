package com.iries.youtubealarm.domain.models

class UserConfigs {
    private var isFirstLaunch = true

    fun getIsFirstLaunch(): Boolean {
        return isFirstLaunch
    }

    fun setFirstLaunch(isFirstLaunch: Boolean) {
        this.isFirstLaunch = isFirstLaunch
    }
}