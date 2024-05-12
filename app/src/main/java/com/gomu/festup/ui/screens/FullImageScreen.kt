package com.gomu.festup.ui.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage

@Composable
fun FullImageScreen(
    type: String,
    filename: String
) {

    lateinit var imageUri: Uri

    when (type) {
        "user" -> imageUri = Uri.parse("http://34.16.74.167/userProfileImages/$filename.png")
        "cuadrilla" -> imageUri = Uri.parse("http://34.16.74.167/cuadrillaProfileImages/$filename.png")
        "evento" -> imageUri = Uri.parse("http://34.16.74.167/eventoImages/$filename.png")
    }

    Log.d("FullImageScreen", "imageUri: $imageUri")

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "Full screen image",
            modifier = Modifier.fillMaxSize()
        )
    }
}
