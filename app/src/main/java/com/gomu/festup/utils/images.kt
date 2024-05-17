package com.gomu.festup.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi


/**
 * Gestión del Bitmap
 */
@RequiresApi(Build.VERSION_CODES.P)
fun Context.localUriToBitmap(uri: Uri): Bitmap {
    val contentResolver: ContentResolver = this.contentResolver
    val source = ImageDecoder.createSource(contentResolver, uri)
    return ImageDecoder.decodeBitmap(source)
}