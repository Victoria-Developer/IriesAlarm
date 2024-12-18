package com.iries.youtubealarm.domain.models

import com.iries.youtubealarm.domain.constants.Duration
import com.iries.youtubealarm.domain.constants.Order

class UserConfigs {
    private var duration: Duration = Duration.ANY
    private var order: Order = Order.DATE
    private var isFirstLaunch = true

    fun getIsFirstLaunch(): Boolean {
        return isFirstLaunch
    }

    fun getDuration(): Duration {
        return duration
    }

    fun getOrder(): Order {
        return order
    }

    fun setFirstLaunch(isFirstLaunch: Boolean) {
        this.isFirstLaunch = isFirstLaunch
    }

    fun setDuration(duration: Duration) {
        this.duration = duration
    }

    fun setOrder(order: Order) {
        this.order = order
    }
}