package com.iries.youtubealarm.data

import com.iries.youtubealarm.domain.constants.DURATION
import com.iries.youtubealarm.domain.constants.ORDER

class UserConfigs {
    private var duration: DURATION = DURATION.ANY
    private var order: ORDER = ORDER.DATE

    fun getDuration(): DURATION {
        return duration
    }

    fun getOrder(): ORDER {
        return order
    }

    fun setDuration(duration: DURATION) {
        this.duration = duration
    }

    fun setOrder(order: ORDER) {
        this.order = order
    }
}