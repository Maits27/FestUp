package com.gomu.festup.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun EstasSeguroDialog(show: Boolean, mensaje: String, onDismiss:()->Unit, onConfirm:() -> Unit){
    if(show){
        AlertDialog(
            onDismissRequest = {},
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text(text = "No")
                }
            },
            confirmButton = {
                TextButton(onClick = { onConfirm() }) {
                    Text(text = "Si")
                }
            },
            title = {
                Text(text = "¿Estás seguro?") },
            text = {
                Text(text = mensaje)
            }
        )
    }
}