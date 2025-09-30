package com.iries.alarm.presentation.navigation

sealed interface Destinations {
    val path: String

    data object ChannelsScreenDest : Destinations {
        override val path = "MusicSearchDest"
    }

    data object AlarmsScreenDest : Destinations {
        override val path: String = "AlarmsDest"
    }
}