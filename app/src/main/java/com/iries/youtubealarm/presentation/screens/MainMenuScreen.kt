package com.iries.youtubealarm.presentation.screens

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.iries.youtubealarm.domain.youtube_api.YoutubeAuth
import com.iries.youtubealarm.presentation.viewmodels.ChannelsViewModel

@Composable
fun MainMenuScreen(
    context: Context,
    onNavigateToChannelsScreen: () -> Unit,
    onNavigateToAlarmsScreen: () -> Unit
) {
    val viewModel: ChannelsViewModel = hiltViewModel()
    val list = viewModel.getAllChannels().observeAsState().value

    //sign-in launcher
    val activityLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            onNavigateToAlarmsScreen()
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        /* val shouldShowWarning = remember{
             mutableStateOf(false)
         }


         LaunchedEffect(shouldShowWarning) {
             shouldShowWarning.value = list.isEmpty()
         }

         if (shouldShowWarning.value) {
             ChannelsWarning(
                 LocalContext.current,
                 "Please, select YouTube channels first."
             )

         }*/
        /*if (list == null) {
            //CircularProgressIndicator(modifier = Modifier.size(80.dp))
            println("PROGRESS INDICATOR")
        } else*/

        Button(
            onClick = {
                //check if user is logged in
                val signedInAccount = GoogleSignIn.getLastSignedInAccount(context)
                val signInIntent = YoutubeAuth.getSignInClient(context).signInIntent

                if (signedInAccount != null)
                    onNavigateToChannelsScreen()
                else activityLauncher.launch(signInIntent)
            }
        ) {
            Text("Youtube Channels")
        }

        Button(
            enabled = !list.isNullOrEmpty(),
            onClick = {
                onNavigateToAlarmsScreen()
            }
        ) {
            Text("Alarms")
        }


    }
}


@Composable
fun ChannelsWarning(context: Context, text: String) {
    Toast.makeText(
        context,
        text,
        Toast.LENGTH_SHORT
    ).show()

}

