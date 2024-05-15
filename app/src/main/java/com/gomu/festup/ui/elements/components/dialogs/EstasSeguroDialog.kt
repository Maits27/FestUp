package com.gomu.festup.ui.elements.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gomu.festup.R

@Composable
fun EstasSeguroDialog(
    show: Boolean,
    mensaje: String,
    onDismiss:() -> Unit,
    onConfirm:() -> Unit
){
    if(show){
        AlertDialog(
            onDismissRequest = {onDismiss()},
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text(text = stringResource(id = R.string.no))
                }
            },
            confirmButton = {
                TextButton(onClick = { onConfirm() }) {
                    Text(text = stringResource(id = R.string.si))
                }
            },
            title = {
                Text(text = stringResource(id = R.string.seguro)) },
            text = {
                Text(text = mensaje)
            }
        )
    }
}