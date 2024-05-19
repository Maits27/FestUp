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

// Dialogo para mostrar una lista con el usuario registrado y las cuadrillas a las que pertenece para que puede apuntarse o desapuntarse
@Composable
fun Apuntarse(
    show: Boolean,
    apuntado: Boolean,
    mainVM: MainVM,
    onDismiss:() -> Unit
){
    if(show){
        // Obtener el estado del usuario y sus cuadrillas (apuntado o desapuntado) desde el VM
        val cuadrillasNoApuntadas = mainVM.cuadrillasUsuarioNoApuntadas(mainVM.currentUser.value!!, mainVM.eventoMostrar.value!!.id).collectAsState(initial = emptyList())
        val cuadrillasApuntadas = mainVM.cuadrillasUsuarioApuntadas(mainVM.currentUser.value!!, mainVM.eventoMostrar.value!!.id).collectAsState(initial = emptyList())
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

                    // Mostrar el card del usuario actual activado o desactivado en funcion de si esta apuntado o no
                    item {
                        val usuario = mainVM.currentUser.value!!
                        UsuarioCardParaEventosAlert(usuario = usuario, apuntado, mainVM)
                    }

                    // Mostrar los cards de las cuadrillas del usuario que no estan apuntadas
                    items(cuadrillasNoApuntadas.value){cuadrilla ->
                        CuadrillaCardParaEventosAlert(
                            cuadrilla = cuadrilla,
                            false,
                            mainVM,
                        )
                    }

                    // Mostrar los cards de las cuadrillas del usuario que si estan apuntadas
                    items(cuadrillasApuntadas.value){cuadrilla ->
                        CuadrillaCardParaEventosAlert(
                            cuadrilla = cuadrilla,
                            true,
                            mainVM,
                        )
                    }
                }
            }
        )
    }
}
