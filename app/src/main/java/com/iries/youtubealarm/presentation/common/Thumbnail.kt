package com.iries.youtubealarm.presentation.common

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@Composable
fun Thumbnail(
    context: Context,
    imageUrl: String?
) {
   // var image by remember { mutableStateOf<Drawable?>(null) }

    AsyncImage(
        model = imageUrl,
        contentDescription = "Channel thumbnail",
        modifier = Modifier
            .size(40.dp, 40.dp)
            .clip(MaterialTheme.shapes.small)
    )
    /*image?.let {
        Image(
            painter = rememberDrawablePainter(it),
            contentDescription = "Channel thumbnail",
            contentScale = ContentScale.Fit,

            )
    }*/

    /*Glide.with(context)
        .load(imageUrl)
        .into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                image = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // Handle when the image load is cleared
            }
        })*/
}