package com.iries.youtubealarm.presentation.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.iries.youtubealarm.data.entity.YTChannel
import com.iries.youtubealarm.domain.youtube_api.YoutubeAuth
import com.iries.youtubealarm.domain.youtube_api.YoutubeSearch
import com.iries.youtubealarm.presentation.common.SearchBar
import com.iries.youtubealarm.presentation.common.Thumbnail
import com.iries.youtubealarm.presentation.viewmodels.AlarmsViewModel
import com.iries.youtubealarm.presentation.viewmodels.ChannelsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChannelsScreen(
    context: Context
) {

    val viewModel : ChannelsViewModel = hiltViewModel()
    val databaseChannels = viewModel.getAllChannels().observeAsState()
    val visibleChannels = remember {
        mutableStateOf(listOf<YTChannel>())
    }
    val youTube = YoutubeAuth.getYoutube(context)

    Column(
        modifier = Modifier.padding(0.dp, 20.dp)
    ) {
        SearchBar(
            onSearch = {
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    visibleChannels.value = YoutubeSearch.findChannelByKeyword(youTube, it)
                }
            }
        )

        Row(
            modifier = Modifier.padding(40.dp, 15.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    visibleChannels.value = databaseChannels.value!!
                }
            ) {
                Text("Selected channels")
            }

            Button(
                onClick = {
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        visibleChannels.value = YoutubeSearch.getSubscriptions(youTube)
                    }
                }
            ) {
                Text("My subscriptions")
            }
        }

        LazyColumn {
            items(visibleChannels.value.toList()) {
                Row {
                    val visibleChannel = it

                    val dbMatch = databaseChannels.value!!.firstOrNull { dbChannel ->
                        dbChannel.getChannelId() == visibleChannel.getChannelId()
                    } // same channel in db
                    val dbContains = dbMatch != null

                    val isChecked = remember(dbContains) {
                        mutableStateOf(dbContains)
                    }

                    Checkbox(
                        checked = isChecked.value,
                        onCheckedChange = {
                            if (it)
                                viewModel.insert(visibleChannel)
                            else
                                viewModel.remove(dbMatch!!)
                            isChecked.value = it
                        }
                    )

                    Thumbnail(context, visibleChannel.getIconUrl())

                    Spacer(Modifier.padding(20.dp, 0.dp))

                    Text(visibleChannel.getTitle())
                }
            }
        }
    }

}

