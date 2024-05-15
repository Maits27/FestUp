package com.gomu.festup.ui.elements.components.dialogs

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import com.gomu.festup.R
import com.gomu.festup.ui.elements.components.cards.CuadrillaCardParaEventosAlert
import com.gomu.festup.ui.elements.components.cards.UsuarioCardParaEventosAlert
import com.gomu.festup.ui.vm.MainVM

@Composable
fun Apuntarse(
    show: Boolean,
    apuntado: Boolean,
    mainVM: MainVM,
    recibirNotificaciones: Boolean,
    onDismiss:() -> Unit
){
    if(show){
        var cuadrillasNoApuntadas = mainVM.cuadrillasUsuarioNoApuntadas(mainVM.currentUser.value!!, mainVM.eventoMostrar.value!!.id).collectAsState(initial = emptyList())
        var cuadrillasApuntadas = mainVM.cuadrillasUsuarioApuntadas(mainVM.currentUser.value!!, mainVM.eventoMostrar.value!!.id).collectAsState(initial = emptyList())
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = { onDismiss()}) {
                    Text(text = stringResource(id = R.string.aceptar))
                }
            },
            title = {
                Text(text = mainVM.eventoMostrar.value!!.nombre) },
            text = {
                LazyColumn {
                    item {
                        val usuario = mainVM.currentUser.value!!
                        UsuarioCardParaEventosAlert(usuario = usuario, apuntado, mainVM, recibirNotificaciones)
                    }
                    items(cuadrillasNoApuntadas.value){cuadrilla ->
                        CuadrillaCardParaEventosAlert(
                            cuadrilla = cuadrilla,
                            false,
                            mainVM,
                            recibirNotificaciones
                        )
                    }
                    items(cuadrillasApuntadas.value){cuadrilla ->
                        CuadrillaCardParaEventosAlert(
                            cuadrilla = cuadrilla,
                            true,
                            mainVM,
                            recibirNotificaciones
                        )
                    }
                }
            }
        )
    }
}
