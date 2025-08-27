package com.iries.alarm.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iries.alarm.domain.constants.AuthState

@Composable
fun AuthScreen(
    loginCode: String?,
    onRedirectToMusicScreen: () -> Unit,
    onRedirectToAlarmsScreen: () -> Unit
) {

    val context = LocalContext.current
    val viewModel: AuthViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(loginCode) {
        if (!loginCode.isNullOrEmpty() && state == AuthState.RequiresLogin) {
            viewModel.authorizeUser(loginCode)
        }
    }

    LaunchedEffect(state) {
        when (state) {
            AuthState.AuthorizedFirstTime -> onRedirectToMusicScreen()
            AuthState.Authorized -> onRedirectToAlarmsScreen()
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state) {
            AuthState.Loading -> {
                Text("Loading your data...")
                Spacer(Modifier.padding(vertical = 15.dp))
                CircularProgressIndicator()
            }

            AuthState.RequiresLogin -> {
                Text(
                    "Please, login to your Sound Cloud account to continue.",
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.padding(vertical = 15.dp))
                Button(onClick = { viewModel.login(context) }) {
                    Text("Login")
                }
            }

            AuthState.Authorized,
            AuthState.AuthorizedFirstTime -> {
                Text("You were successfully authorized!.")
            }
        }
    }
}