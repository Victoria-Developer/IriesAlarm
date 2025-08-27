package com.iries.alarm.presentation.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.iries.alarm.R


@Composable
fun Thumbnail(
    imageUrl: String?
) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Channel thumbnail",
        modifier = Modifier
            .size(60.dp)
            .clip(MaterialTheme.shapes.small),
        placeholder = painterResource(R.drawable.placeholder), // Loading image
        error = painterResource(R.drawable.placeholder) // Error image
    )
}