package com.gomu.festup.ui.elements.screens

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest


/**
 * Imagen desplegable (al pulsar las diferentes imágenes de la aplicación)
 */
@Composable
fun FullImageScreen(
    type: String,
    filename: String
) {
    val context = LocalContext.current

    lateinit var imageUri: Uri

    when (type) {
        "user" -> imageUri = Uri.parse("http://34.71.128.243/userProfileImages/$filename.png")
        "cuadrilla" -> imageUri = Uri.parse("http://34.71.128.243/cuadrillaProfileImages/$filename.png")
        "evento" -> imageUri = Uri.parse("http://34.71.128.243/eventoImages/$filename.png")
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUri)
                .crossfade(true)
                .memoryCachePolicy(CachePolicy.DISABLED)  // Para que no la guarde en caché-RAM
                .diskCachePolicy(CachePolicy.DISABLED)    // Para que no la guarde en caché-disco
                .build(),
            contentDescription = "Full screen image",
            modifier = Modifier.fillMaxSize()
        )
    }
}
