package com.iries.youtubealarm.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iries.youtubealarm.R
import com.iries.youtubealarm.presentation.screens.alarms.AlarmsScreen
import com.iries.youtubealarm.presentation.screens.music.YouTubeScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    isFirstLaunch: Boolean
) {

    val ytChannelsPath = Destinations.ChannelsScreenDest.path
    val alarmsPath = Destinations.AlarmsScreenDest.path

    val startDestination = if (isFirstLaunch) ytChannelsPath else alarmsPath
    var currentPath by remember { mutableStateOf(startDestination) }

    fun navigate(path: String) {
        val canPopStack = navController
            .popBackStack(route = path, inclusive = false)
        if (!canPopStack) navController.navigate(path)
        currentPath = path
    }

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable(ytChannelsPath) {
                            YouTubeScreen()
                        }
                        composable(alarmsPath) {
                            AlarmsScreen()
                        }
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavigationItem(
                    icon = R.drawable.youtube_icon,
                    isSelected = currentPath == ytChannelsPath,
                    onClick = { navigate(ytChannelsPath) }
                )
                BottomNavigationItem(
                    icon = R.drawable.baseline_access_alarm_24,
                    isSelected = currentPath == alarmsPath,
                    onClick = { navigate(alarmsPath) }
                )
            }
        }
    )
}

@Composable
fun BottomNavigationItem(
    label: String = "",
    icon: Int,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier.size(35.dp),
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier.fillMaxSize(),
            tint = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
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