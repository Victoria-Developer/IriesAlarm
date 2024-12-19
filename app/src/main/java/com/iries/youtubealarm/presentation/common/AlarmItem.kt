package com.iries.youtubealarm.presentation.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iries.youtubealarm.data.entity.AlarmInfo

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlarmItem(
    alarm: AlarmInfo,
    onRemoveAlarm: () -> Unit,
    onEditAlarm: () -> Unit,
    onSwitchAlarm: (Boolean) -> Unit
) {
    val isActive = remember { mutableStateOf(alarm.isActive()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RectangleShape
            )
    ) {

        Column{

            Text(
                alarm.getFormattedTime(),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                fontSize = 30.sp
            )
            FlowRow(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp, 10.dp)
                    .fillMaxWidth(0.7f)
                    .wrapContentWidth()
            ) {
                alarm.getDaysId().keys.toList().forEach { dayOfWeek ->
                    Spacer(Modifier.padding(10.dp, 5.dp))
                    Text(dayOfWeek.name)
                }
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {

            Switch(
                checked = isActive.value,
                onCheckedChange = {
                    isActive.value = it
                    onSwitchAlarm(it)
                },
            )

            IconButton(
                onClick = onEditAlarm,
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit alarm")
            }

            IconButton(
                modifier = Modifier.padding(0.dp, 10.dp),
                onClick = onRemoveAlarm
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Remove alarm")
            }
        }
    }
}