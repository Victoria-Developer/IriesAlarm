package com.iries.youtubealarm.presentation.screens.alarms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iries.youtubealarm.data.entity.AlarmInfo
import com.iries.youtubealarm.domain.AlarmManager
import com.iries.youtubealarm.presentation.common.AlarmItem
import com.iries.youtubealarm.presentation.common.DatePicker

@Composable
fun AlarmsScreen(onNavigateToYouTubeScreen: () -> Unit) {

    val context = LocalContext.current
    val viewModel: AlarmsViewModel = hiltViewModel()
    var showDialog by remember { mutableStateOf(false) }
    val alarmsList = viewModel.allAlarms.collectAsState()
    val selectedAlarm: MutableState<AlarmInfo?> = remember { mutableStateOf(null) }

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .size(60.dp),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add alarm"
                    )
                }

                Spacer(Modifier.padding(0.dp, 20.dp))

                if (showDialog) DatePicker(
                    onCloseDialog = { showDialog = false },
                    onConfirm = {
                        if (alarmsList.value.contains(it))
                            viewModel.update(it)
                        else
                            viewModel.insert(context, it)
                    },
                    selectedAlarm.value
                )

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(alarmsList.value.toList()) { alarm ->
                        AlarmItem(
                            alarm = alarm,
                            onRemoveAlarm = {
                                viewModel.remove(context, alarm)
                            },
                            onEditAlarm = {
                                selectedAlarm.value = alarm
                                showDialog = true
                            },
                            onSwitchAlarm = {
                                if (alarm.isActive()) {
                                    println("Stop alarm alarm")
                                    AlarmManager.stopAlarm(context)
                                    viewModel.stopAlarms(context, alarm.getDaysId())
                                } else {
                                    println("Set repeating alarm")
                                    viewModel.setRepeatingAlarm(context, alarm)
                                }
                                alarm.setActive(it)
                                viewModel.update(alarm)
                            }
                        )
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
                    onClick = onNavigateToYouTubeScreen,
                    content = { Text("YouTube search") }
                )
            }
        }
    )

}








