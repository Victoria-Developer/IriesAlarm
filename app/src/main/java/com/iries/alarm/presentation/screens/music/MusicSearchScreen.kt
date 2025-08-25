package com.iries.alarm.presentation.screens.music

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.network.HttpException
import com.iries.alarm.domain.AuthState
import com.iries.alarm.presentation.common.SearchBar
import com.iries.alarm.presentation.common.Thumbnail
import com.iries.alarm.presentation.screens.auth.AuthViewModel

@Composable
fun MusicSearchScreen(authViewModel: AuthViewModel) {

    val viewModel: MusicSearchViewModel = hiltViewModel()
    val currentArtists = viewModel.visibleArtists.collectAsState()
    val savedArtists = viewModel.dbArtists.collectAsState()
    val error = viewModel.error.collectAsState()
    val isFetchRequest = viewModel.isFetchRequest.collectAsState()

    if (error.value != null) {
        AlertDialog(
            onDismissRequest = {

            },
            confirmButton = {
                Button(onClick = { viewModel.updateError(null) }) {
                    Text("Confirm")
                }
            },
            icon = {
                Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning Icon")
            },
            text = { Text("Something went wrong. Please, try again.") }
        )

        if (error.value is HttpException
            && (error.value as HttpException).response.code == 401
        ) {
            authViewModel.updateAuthState(AuthState.RequiresLogin)
        }
    }

    Column {
        SearchBar(onSearch = { name -> viewModel.showArtistsByName(name) })

        Column(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { viewModel.showSavedArtists() }) {
                Text("Saved artists")
            }

            Button(onClick = { viewModel.showSubscriptions() }) {
                Text("My subscriptions")
            }
        }

        if (error.value == null && isFetchRequest.value) {
            Box(modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        } else if (!currentArtists.value.isNullOrEmpty())
            LazyColumn {
                items(currentArtists.value!!.toList()) { visibleArtists ->
                    Row {
                        // Saved artist with the same id as the search result
                        val idMatch = savedArtists.value.firstOrNull { artist ->
                            artist.id == visibleArtists.id
                        }

                        Checkbox(
                            checked = idMatch != null,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    viewModel.saveArtist(visibleArtists)
                                } else {
                                    idMatch?.let { artist -> viewModel.removeArtist(artist) }
                                }
                            }
                        )

                        Thumbnail(visibleArtists.imgUrl)

                        Spacer(Modifier.padding(20.dp, 0.dp))

                        Text(visibleArtists.username)
                    }
                }
            }
    }

}