package com.iries.alarm.presentation.navigation

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iries.alarm.R
import com.iries.alarm.domain.AuthState
import com.iries.alarm.presentation.screens.alarms.AlarmsScreen
import com.iries.alarm.presentation.screens.music.MusicSearchScreen
import com.iries.alarm.presentation.screens.auth.AuthViewModel
import com.iries.alarm.presentation.screens.auth.AuthScreen

@Composable
fun AppNavigation(
    navController: NavHostController
) {

    val musicScreenDest = Destinations.ChannelsScreenDest.path
    val alarmsScreenDest = Destinations.AlarmsScreenDest.path
    val authScreenDest = Destinations.AuthScreenDest.path

    var currentPath by remember { mutableStateOf(authScreenDest) }

    fun navigate(path: String) {
        println("Attempt to navigate to $path")
        if (currentPath == path) return
        println("Navigate")
        val canPopStack = navController
            .popBackStack(route = path, inclusive = false)
        if (!canPopStack) navController.navigate(path)
        currentPath = path
    }

    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.state.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            AuthState.RequiresLogin, AuthState.Loading -> navigate(authScreenDest)
            AuthState.Authorized -> navigate(alarmsScreenDest)
            AuthState.AuthorizedFirstTime -> navigate(musicScreenDest)
        }
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
                        startDestination = authScreenDest
                    ) {
                        composable(authScreenDest) {
                            AuthScreen(authViewModel = authViewModel)
                        }

                        composable(musicScreenDest) {
                            MusicSearchScreen(authViewModel = authViewModel)
                        }
                        composable(alarmsScreenDest) {
                            AlarmsScreen()
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (authState == AuthState.Authorized
                || authState == AuthState.AuthorizedFirstTime
            )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavigationItem(
                        icon = R.drawable.musical_note,
                        isSelected = currentPath == musicScreenDest,
                        onClick = { navigate(musicScreenDest) }
                    )
                    BottomNavigationItem(
                        icon = R.drawable.baseline_access_alarm_24,
                        isSelected = currentPath == alarmsScreenDest,
                        onClick = { navigate(alarmsScreenDest) }
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