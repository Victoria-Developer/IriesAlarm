package com.iries.alarm.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

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