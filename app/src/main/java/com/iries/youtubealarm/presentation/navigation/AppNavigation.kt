package com.iries.youtubealarm.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iries.youtubealarm.presentation.screens.AlarmsScreen
import com.iries.youtubealarm.presentation.screens.ChannelsScreen
import com.iries.youtubealarm.presentation.screens.MainMenuScreen
import com.iries.youtubealarm.presentation.viewmodels.AlarmsViewModel

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    val context = LocalContext.current
    val mainMenuPath = Destinations.MainMenuDest.path

    NavHost(navController = navController, startDestination = mainMenuPath) {

        composable(mainMenuPath) {
            MainMenuScreen(
                context = context,
                { navController.navigate(Destinations.ChannelsScreenDest.path) },
                { navController.navigate(Destinations.AlarmsScreenDest.path) }
            )
        }

        composable(Destinations.ChannelsScreenDest.path) {
            ChannelsScreen(
                context = context
            )
        }

        composable(Destinations.AlarmsScreenDest.path) {
            AlarmsScreen(context = context)
        }
    }
}

sealed interface Destinations {
    val path: String

    data object MainMenuDest : Destinations {
        override val path = "Main_Menu_Screen"
    }

    data object ChannelsScreenDest : Destinations {
        override val path = "Channels_Screen"
    }

    data object AlarmsScreenDest : Destinations {
        override val path: String = "Alarms_Screen"
    }

}