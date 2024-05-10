package com.gomu.festup.ui.components

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gomu.festup.R

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
