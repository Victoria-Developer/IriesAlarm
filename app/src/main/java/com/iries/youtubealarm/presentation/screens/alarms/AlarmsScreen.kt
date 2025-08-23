package com.iries.youtubealarm.presentation.screens.alarms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import com.iries.youtubealarm.data.entity.AlarmEntity
import com.iries.youtubealarm.presentation.common.AlarmItem
import com.iries.youtubealarm.presentation.common.DatePicker

@Composable
fun AlarmsScreen() {

    val context = LocalContext.current
    val viewModel: AlarmsViewModel = hiltViewModel()
    var showDialog by remember { mutableStateOf(false) }
    val alarmsList = viewModel.allAlarms.collectAsState()
    val selectedAlarm: MutableState<AlarmEntity?> = remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
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
                if (alarmsList.value.contains(it)) {
                    if (it.isActive()) {
                        viewModel.cancelAlarms(context, it.getDaysId())
                        viewModel.activateAlarm(context, it)
                    }
                    viewModel.updateAlarm(it)
                } else {
                    viewModel.activateAlarm(context, it)
                    viewModel.addAlarm(it)
                }
            },
            alarm = selectedAlarm.value
        )

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(alarmsList.value.toList()) { alarm ->
                AlarmItem(
                    alarm = alarm,
                    onRemoveAlarm = {
                        viewModel.removeAlarm(context, alarm)
                    },
                    onEditAlarm = {
                        selectedAlarm.value = alarm
                        showDialog = true
                    },
                    onSwitchAlarm = {
                        if (it) {
                            println("Set repeating alarm")
                            viewModel.activateAlarm(context, alarm)
                        } else {
                            println("Stop alarm alarm")
                            viewModel.cancelAlarms(context, alarm.getDaysId())
                        }
                        alarm.setActive(it)
                        viewModel.updateAlarm(alarm)
                    }
                )
            }
        }
    }

}








