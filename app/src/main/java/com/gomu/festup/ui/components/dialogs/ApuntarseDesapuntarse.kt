package com.gomu.festup.ui.components.dialogs

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.gomu.festup.ui.components.cards.CuadrillaCardParaEventosAlert
import com.gomu.festup.ui.components.cards.UsuarioCardParaEventosAlert
import com.gomu.festup.vm.MainVM

@Composable
fun Apuntarse(show: Boolean, apuntado: Boolean, mainVM: MainVM, onDismiss:() -> Unit){
    if(show){
        var cuadrillasNoApuntadas = mainVM.cuadrillasUsuarioNoApuntadas(mainVM.currentUser.value!!, mainVM.eventoMostrar.value!!.id).collectAsState(initial = emptyList())
        var cuadrillasApuntadas = mainVM.cuadrillasUsuarioApuntadas(mainVM.currentUser.value!!, mainVM.eventoMostrar.value!!.id).collectAsState(initial = emptyList())
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = { onDismiss()}) {
                    Text(text = "Salir")
                }
            },
            title = {
                Text(text = mainVM.eventoMostrar.value!!.nombre) },
            text = {
                LazyColumn {
                    if(!apuntado){
                        item {
                            val usuario = mainVM.currentUser.value!!
                            UsuarioCardParaEventosAlert(usuario = usuario, apuntado, mainVM) {
                                mainVM.apuntarse(usuario, mainVM.eventoMostrar.value!!)
                                onDismiss()
                            }
                        }
                    }
                    else{
                        item {
                            val usuario = mainVM.currentUser.value!!
                            UsuarioCardParaEventosAlert(usuario = usuario, apuntado, mainVM) {
                                mainVM.desapuntarse(usuario, mainVM.eventoMostrar.value!!)
                                onDismiss()
                            }
                        }
                    }
                    items(cuadrillasNoApuntadas.value){cuadrilla ->
                        CuadrillaCardParaEventosAlert(
                            cuadrilla = cuadrilla,
                            false,
                            mainVM,
                        ){
                            mainVM.apuntarse(cuadrilla, mainVM.eventoMostrar.value!!)
                            onDismiss()
                        }
                    }
                    items(cuadrillasApuntadas.value){cuadrilla ->
                        CuadrillaCardParaEventosAlert(
                            cuadrilla = cuadrilla,
                            true,
                            mainVM
                        ){
                            mainVM.desapuntarse(cuadrilla, mainVM.eventoMostrar.value!!)
                            onDismiss()
                        }
                    }

                }

            }
        )
    }
}
