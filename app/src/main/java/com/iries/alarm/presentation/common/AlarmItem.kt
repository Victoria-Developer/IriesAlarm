package com.iries.alarm.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iries.alarm.domain.constants.Day
import com.iries.alarm.domain.models.Alarm
import java.util.Locale

@Composable
fun AlarmItem(
    alarm: Alarm,
    onRemoveAlarm: () -> Unit,
    onEditAlarm: () -> Unit,
    onSwitchAlarm: (Boolean) -> Unit
) {
    val isActive = remember { mutableStateOf(alarm.isActive) }

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 15.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    String.format(Locale.getDefault(), "%02d:%02d", alarm.hour, alarm.minute),
                    fontSize = 30.sp
                )
                val daysString = alarm.days.keys
                    .mapNotNull { dayId -> Day.getById(dayId).weekName }
                    .joinToString(separator = ",  ")

                Text(
                    text = daysString,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(0.7f),
                    textAlign = TextAlign.Center
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Switch(
                    checked = isActive.value,
                    onCheckedChange = {
                        isActive.value = it
                        onSwitchAlarm(it)
                    }
                )

                IconButton(onClick = onEditAlarm) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit alarm")
                }

                IconButton(
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = onRemoveAlarm
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove alarm")
                }
            }
        }
    }
}