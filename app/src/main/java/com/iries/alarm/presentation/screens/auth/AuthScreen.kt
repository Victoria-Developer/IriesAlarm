package com.iries.alarm.presentation.screens.auth

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iries.alarm.domain.AuthState
import com.iries.alarm.presentation.activities.AuthActivity

@Composable
fun AuthScreen(authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val state by authViewModel.state.collectAsState()

    val loginLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val code = result.data?.getStringExtra("code")
            code?.let { authViewModel.authorizeUser(it) }
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
                Button(onClick = {
                    val intent = Intent(context, AuthActivity::class.java)
                    loginLauncher.launch(intent)
                }) {
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