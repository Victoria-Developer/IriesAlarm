package com.iries.youtubealarm.presentation.screens.youtube

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.iries.youtubealarm.data.network.YoutubeAuth
import com.iries.youtubealarm.presentation.common.SearchBar
import com.iries.youtubealarm.presentation.common.Thumbnail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun YouTubeScreen(onNavigateToAlarmsScreen: () -> Unit) {

    val context = LocalContext.current
    val viewModel: YouTubeViewModel = hiltViewModel()
    val visibleChannels = viewModel.visibleChannels.collectAsState()
    val dbChannels = viewModel.dbChannels.collectAsState()
    val isError = viewModel.isError.collectAsState()
    val isFetchRequest = viewModel.isFetchRequest.collectAsState()

    //Google Sign-in launcher
    val loginLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK)
            viewModel.showSubscriptions(context)
        else Toast.makeText(
            context, "Login failed. Please, try again", Toast.LENGTH_SHORT
        ).show()
    }

    if (isError.value) {
        AlertDialog(
            onDismissRequest = {

            },
            confirmButton = {
                Button(onClick = { viewModel.updateError(false) }) {
                    Text("Confirm")
                }
            },
            icon = {
                Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning Icon")
            },
            text = { Text("Something went wrong. Please, try again.") }
        )
    }

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding) //0.dp, 20.dp
            ) {
                SearchBar(
                    onSearch = {
                        viewModel.showChannelsByKeyWord(it)
                    }
                )

                Row(
                    modifier = Modifier.padding(40.dp, 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(onClick = { viewModel.showDBChannels() }) {
                        Text("Selected channels")
                    }

                    Button(
                        onClick = {
                            viewModel.viewModelScope.launch(Dispatchers.IO) {
                                val signedInAccount = GoogleSignIn.getLastSignedInAccount(context)
                                val signInIntent = YoutubeAuth.getSignInClient(context).signInIntent

                                if (signedInAccount != null) viewModel.showSubscriptions(context)
                                else loginLauncher.launch(signInIntent)
                            }
                        }
                    ) {
                        Text("My subscriptions")
                    }
                }

                if (!isError.value && isFetchRequest.value) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .width(64.dp)
                                .align(Alignment.Center),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                } else if (!visibleChannels.value.isNullOrEmpty())
                    LazyColumn {
                        items(visibleChannels.value!!.toList()) { visibleChannel ->
                            Row {

                                // Channel in db with the same channelId as in the search result
                                val dbMatch = dbChannels.value.firstOrNull { dbChannel ->
                                    dbChannel.getChannelId() == visibleChannel.getChannelId()
                                }

                                Checkbox(
                                    checked = dbMatch != null,
                                    onCheckedChange = {
                                        if (it) {
                                            viewModel.addChannelToDB(visibleChannel)
                                        } else {
                                            dbMatch?.let { it1 -> viewModel.removeChannelFromDB(it1) }
                                        }
                                    }
                                )

                                Thumbnail(context, visibleChannel.getIconUrl())

                                Spacer(Modifier.padding(20.dp, 0.dp))

                                Text(visibleChannel.getTitle().toString())
                            }
                        }
                    }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                ElevatedButton(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = {
                        if (viewModel.isDBEmpty())
                            Toast.makeText(
                                context, "You didn't choose any YouTube channels. " +
                                        "Default alarm ringtone is on.",
                                Toast.LENGTH_LONG
                            ).show()
                        onNavigateToAlarmsScreen()
                    },
                    content = { Text("Alarms") }
                )
            }

        }
    )


}

