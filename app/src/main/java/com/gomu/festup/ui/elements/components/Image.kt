package com.gomu.festup.ui.elements.components

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.compose.gradientsColors
import com.gomu.festup.R


/**
    EN ESTE FICHERO SE DEFINEN LOS DIFERENTE TIPOS DE IMAGENES QUE SE EMPLEAN EN DIFERENTES PANTALLAS
**/


@Composable
fun Imagen(imageUri: Uri?, noImagePainterId: Int, size: Dp, onClick: () -> Unit) {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUri)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.DISABLED)  // Para que no la guarde en caché-RAM
            .diskCachePolicy(CachePolicy.DISABLED)    // Para que no la guarde en caché-disco
            .build(),
        contentDescription = null,
        placeholder = painterResource(id = noImagePainterId),
        contentScale = ContentScale.Crop,
        error = painterResource(id = noImagePainterId),
        modifier = Modifier
            .border(BorderStroke(2.dp, brush = Brush.linearGradient(colors = gradientsColors)), CircleShape)
            .size(size)
            .clip(CircleShape)
            .clickable { onClick() }
    )
}

@Composable
fun ImagenMiniConBorde(imageUri: Uri?, noImagePainterId: Int) {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUri)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.DISABLED)  // Para que no la guarde en caché-RAM
            .diskCachePolicy(CachePolicy.DISABLED)    // Para que no la guarde en caché-disco
            .build(),
        contentDescription = null,
        placeholder = painterResource(id = noImagePainterId),
        contentScale = ContentScale.Crop,
        error = painterResource(id = noImagePainterId),
        modifier = Modifier
            .border(BorderStroke(2.dp, brush = Brush.linearGradient(colors = gradientsColors)), CircleShape)
            .size(50.dp)
            .clip(CircleShape)
    )
}

@Composable
fun ImagenEvento(imageUri: Uri?, noImagePainterId: Int, size: Dp, onClick: () -> Unit) {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUri)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)  // Para que no la guarde en caché-RAM
            .diskCachePolicy(CachePolicy.ENABLED)    // Para que no la guarde en caché-disco
            .build(),
        contentDescription = null,
        placeholder = painterResource(id = noImagePainterId),
        contentScale = ContentScale.Crop,
        error = painterResource(id = noImagePainterId),
        modifier = Modifier
            .border(BorderStroke(2.dp, brush = Brush.linearGradient(colors = gradientsColors)), RoundedCornerShape(10.dp))
            .size(size)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick()}
    )
}

@Composable
fun ImagenEventoMiniConBorde(imageUri: Uri?, noImagePainterId: Int) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUri)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.DISABLED)  // Para que no la guarde en caché-RAM
            .diskCachePolicy(CachePolicy.DISABLED)    // Para que no la guarde en caché-disco
            .build(),
        contentDescription = null,
        placeholder = painterResource(id = noImagePainterId),
        contentScale = ContentScale.Crop,
        error = painterResource(id = noImagePainterId),
        modifier = Modifier
            .border(BorderStroke(2.dp, brush = Brush.linearGradient(colors = gradientsColors)), RoundedCornerShape(10.dp))
            .size(50.dp)
            .clip(RoundedCornerShape(10.dp))
    )
}

@Composable
fun EditImageIcon(singlePhotoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(bottom = 16.dp, end = 8.dp)
            .clip(CircleShape)
            .clickable(onClick = {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            })
    ) {
        //Añadir circle y edit
        Icon(painterResource(id = R.drawable.circle), contentDescription = null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
        Icon(painterResource(id = R.drawable.edit), contentDescription = null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.surface)
    }
}
