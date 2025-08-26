package com.iries.alarm.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.iries.alarm.domain.constants.Day
import com.iries.alarm.domain.models.Alarm

@Composable
fun DatePicker(
    onCloseDialog: () -> Unit,
    onConfirm: (alarm: Alarm) -> Unit,
    alarm: Alarm?
) {
    val selectedAlarm = alarm ?: Alarm()
    val days = selectedAlarm.days

    Dialog(onDismissRequest = onCloseDialog) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {

            WheelTimePicker(
                timeFormat = TimeFormat.HOUR_24
            ) { snappedTime ->
                selectedAlarm.hour = snappedTime.hour
                selectedAlarm.minute = snappedTime.minute
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(0.dp, 20.dp)
            ) {
                items(Day.entries.toList()) { dayOfWeek ->
                    ClickableText(
                        dayOfWeek.name,
                        days.contains(dayOfWeek.id)
                    ) {
                        if (it)
                            days[dayOfWeek.id] = 0
                        else
                            days.remove(dayOfWeek.id)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Button(onCloseDialog) {
                    Text("Dismiss")
                }
                Button({
                    onConfirm(selectedAlarm)
                    onCloseDialog()
                }) {
                    Text("Confirm")
                }
            }
        }
    }
}

@Composable
fun ClickableText(
    text: String,
    isClicked: Boolean,
    onClick: (isClicked: Boolean) -> Unit
) {
    var textClicked by remember { mutableStateOf(isClicked) }
    val colour = if (textClicked) Color.LightGray else Color.White

    Text(
        modifier = Modifier
            .background(colour)
            .clickable(enabled = true) {
                textClicked = !textClicked
                onClick(textClicked)
            },
        text = text,
        fontSize = 20.sp
    )
}