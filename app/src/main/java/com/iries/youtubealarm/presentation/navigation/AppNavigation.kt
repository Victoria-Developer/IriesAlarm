package com.iries.youtubealarm.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iries.youtubealarm.presentation.screens.alarms.AlarmsScreen
import com.iries.youtubealarm.presentation.screens.youtube.YouTubeScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    isFirstLaunch:Boolean
) {

    val ytChannelsPath = Destinations.ChannelsScreenDest.path
    val alarmsPath = Destinations.AlarmsScreenDest.path

    val startDestination = if (isFirstLaunch) ytChannelsPath else alarmsPath

    fun navigate(path: String) {
        val canPopStack = navController
            .popBackStack(route = path, inclusive = false)
        if (!canPopStack) navController.navigate(path)
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(ytChannelsPath) {
            YouTubeScreen(onNavigateToAlarmsScreen = {
                navigate(alarmsPath)
            })
        }

        composable(alarmsPath) {
            AlarmsScreen(onNavigateToYouTubeScreen = {
                navigate(ytChannelsPath)
            })
        }
    }
}

sealed interface Destinations {
    val path: String

    data object ChannelsScreenDest : Destinations {
        override val path = "YouTube"
    }

    data object AlarmsScreenDest : Destinations {
        override val path: String = "Alarms"
    }

}