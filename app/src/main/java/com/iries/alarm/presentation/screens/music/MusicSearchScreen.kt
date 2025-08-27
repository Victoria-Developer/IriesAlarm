package com.iries.alarm.presentation.screens.music

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.network.HttpException
import com.iries.alarm.domain.constants.SearchCategory
import com.iries.alarm.presentation.common.SearchBar
import com.iries.alarm.presentation.common.Thumbnail

@Composable
fun MusicSearchScreen(onRedirectToAuthScreen: () -> Unit) {

    val viewModel: MusicSearchViewModel = hiltViewModel()
    val currentArtists = viewModel.visibleArtists.collectAsState()
    val savedArtists = viewModel.dbArtists.collectAsState()
    val error = viewModel.error.collectAsState()
    val isFetchRequest = viewModel.isFetchRequest.collectAsState()
    var selectedSearchCategory by remember {
        mutableStateOf(SearchCategory.SAVED_IN_DATABASE)
    }

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
            onRedirectToAuthScreen()
        }
    }

    @Composable
    fun searchCategoryButtonColor(searchCategory: SearchCategory): ButtonColors {
        val isSelected = selectedSearchCategory == searchCategory
        val containerColor by animateColorAsState(
            targetValue = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant
        )
        val contentColor by animateColorAsState(
            targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )

        return ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    }

    Column (modifier = Modifier.padding(top = 10.dp)){
        SearchBar(onSearch = {
            name -> viewModel.showArtistsByName(name)
            selectedSearchCategory = SearchCategory.SEARCH_BY_KEYWORD
        })

        Row(
            modifier = Modifier
                .padding(vertical = 30.dp, horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                colors = searchCategoryButtonColor(
                    SearchCategory.SAVED_IN_DATABASE
                ),
                onClick = {
                    viewModel.showSavedArtists()
                    selectedSearchCategory = SearchCategory.SAVED_IN_DATABASE
                }
            ) {
                Text("Favorites", maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Button(
                modifier = Modifier.weight(1f),
                colors = searchCategoryButtonColor(
                    SearchCategory.USER_SUBSCRIPTIONS
                ),
                onClick = {
                    viewModel.showSubscriptions()
                    selectedSearchCategory = SearchCategory.USER_SUBSCRIPTIONS
                }
            ) {
                Text("Subscriptions", maxLines = 1, overflow = TextOverflow.Ellipsis)
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
            LazyColumn (
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){
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