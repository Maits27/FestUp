package com.gomu.festup.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.gomu.festup.vm.MainVM

@Composable
fun Apuntarse(show: Boolean, apuntado: Boolean, mainVM: MainVM, onDismiss:() -> Unit){
    if(show){
        var cuadrillasNoApuntadas = mainVM.cuadrillasUsuarioNoApuntadas(mainVM.currentUser.value!!, mainVM.eventoMostrar.value!!.id).collectAsState(initial = emptyList())
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { onDismiss()}) {
                    Text(text = "Salir")
                }
            },
            title = {
                Text(text = "Apúntate a ${mainVM.eventoMostrar.value!!.nombre}") },
            text = {
                when {
                    cuadrillasNoApuntadas.value.isEmpty() && apuntado  -> Text(text = "Estás apuntado")
                    else ->{
                        Column {
                            Text(text = "¿Con qué perfil te quieres apuntar?")
                            LazyColumn {
                                if(!apuntado){
                                    item {
                                        val usuario = mainVM.currentUser.value!!
                                        UsuarioCardParaEventosAlert(usuario = usuario) {
                                            mainVM.apuntarse(usuario, mainVM.eventoMostrar.value!!)
                                            onDismiss()
                                        }
                                    }
                                }
                                items(cuadrillasNoApuntadas.value){cuadrilla ->
                                    CuadrillaCardParaEventosAlert(
                                        cuadrilla = cuadrilla,
                                    ){
                                        mainVM.apuntarse(cuadrilla, mainVM.eventoMostrar.value!!)
                                        onDismiss()
                                    }
                                }
                            }
                        }
                    }
                }

            }
        )
    }
}

@Composable
fun Desapuntarse(show: Boolean, apuntado: Boolean, mainVM: MainVM, onDismiss:() -> Unit){
    if(show){
        var cuadrillasApuntadas = mainVM.cuadrillasUsuarioApuntadas(mainVM.currentUser.value!!, mainVM.eventoMostrar.value!!.id).collectAsState(initial = emptyList())
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { onDismiss()}) {
                    Text(text = "Salir")
                }
            },
            title = {
                Text(text = "Desapúntate de ${mainVM.eventoMostrar.value!!.nombre}") },
            text = {
                when {
                    cuadrillasApuntadas.value.isEmpty() && !apuntado -> Text(text = "No estás apuntado")
                    else ->{
                        Column {
                            Text(text = "¿Con qué perfil te quieres desapuntar?")
                            LazyColumn {
                                if(apuntado){
                                    item {
                                        val usuario = mainVM.currentUser.value!!
                                        UsuarioCardParaEventosAlert(usuario = usuario) {
                                            mainVM.desapuntarse(usuario, mainVM.eventoMostrar.value!!)
                                            Log.d("EVENTO MOSTRAR", mainVM.eventoMostrar.value.toString())
                                            Log.d("CURRENT USER", mainVM.currentUser.value.toString())
                                            onDismiss()
                                        }
                                    }
                                }
                                items(cuadrillasApuntadas.value){ cuadrilla ->
                                    CuadrillaCardParaEventosAlert(
                                        cuadrilla = cuadrilla,
                                    ){
                                        mainVM.desapuntarse(cuadrilla, mainVM.eventoMostrar.value!!)
                                        onDismiss()
                                    }
                                }
                            }
                        }
                    }
                }

            }
        )
    }
}