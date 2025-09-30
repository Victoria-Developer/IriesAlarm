package com.iries.alarm.presentation.screens.alarms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iries.alarm.domain.models.Alarm
import com.iries.alarm.presentation.common.AlarmItem
import com.iries.alarm.presentation.common.TimePicker
import java.time.LocalTime

@Composable
fun AlarmsScreen(onRedirect: () -> Unit) {

    val viewModel: AlarmsViewModel = hiltViewModel()
    var showDialog by remember { mutableStateOf(false) }
    val alarmsList = viewModel.allAlarms.collectAsState()
    val selectedAlarm: MutableState<Alarm> = remember { mutableStateOf(Alarm()) }

    LaunchedEffect (Unit){
        println("Rendering")
        val shouldRedirect = viewModel.checkAvailableArtists()
        if (shouldRedirect) {
            onRedirect()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (showDialog) TimePicker(
            onCloseDialog = { showDialog = false },
            onConfirm = { chosenTime, chosenDays ->
                viewModel.editAlarm(
                    selectedAlarm.value, chosenTime, chosenDays
                )
            },
            initialTime = LocalTime.of(
                selectedAlarm.value.hour, selectedAlarm.value.minute
            ),
            initialDays = selectedAlarm.value.days.keys
        )


        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.80f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(alarmsList.value.toList()) { alarm ->
                AlarmItem(
                    alarm = alarm,
                    onRemoveAlarm = {
                        viewModel.removeAlarm(alarm)
                    },
                    onEditAlarm = {
                        selectedAlarm.value = alarm
                        showDialog = true
                    },
                    onSwitchAlarm = {
                        viewModel.toggleAlarmActivity(
                            alarm, it
                        )
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    selectedAlarm.value = viewModel.draftNewAlarm()
                    showDialog = true
                },
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
        }


    }
}








