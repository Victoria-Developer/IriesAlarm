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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.iries.alarm.domain.constants.Day
import java.time.LocalTime

@Composable
fun TimePicker(
    onCloseDialog: () -> Unit,
    onConfirm: (LocalTime, MutableSet<Int>) -> Unit,
    initialTime: LocalTime,
    initialDays: Set<Int>
) {
    val days: MutableSet<Int> = remember { initialDays.toMutableSet() }
    println(days)
    var chosenTime = remember { initialTime }

    Dialog(
        onDismissRequest = onCloseDialog,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {

            WheelTimePicker(
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.surface
                ),
                textColor = MaterialTheme.colorScheme.primary,
                timeFormat = TimeFormat.HOUR_24,
                startTime = initialTime
            ) { snappedTime ->
                if (initialTime != snappedTime) {
                    chosenTime = snappedTime
                }
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
                        val dayId = dayOfWeek.id
                        if (it) days.add(dayId)
                        else days.remove(dayId)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Button(onCloseDialog) {
                    Text("Dismiss")
                }
                Button({
                    onConfirm(chosenTime, days)
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

    val backgroundColor = if (textClicked) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        else MaterialTheme.colorScheme.surface

    val textColor = if (textClicked) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurface

    Text(
        text = text,
        color = textColor,
        fontSize = 20.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(enabled = true) {
                textClicked = !textClicked
                onClick(textClicked)
            }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}