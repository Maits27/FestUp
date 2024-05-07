package com.gomu.festup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.R
import com.gomu.festup.ui.components.dialogs.Apuntarse
import com.gomu.festup.ui.components.cards.CuadrillaCard
import com.gomu.festup.ui.components.dialogs.Desapuntarse
import com.gomu.festup.ui.components.cards.UsuarioCard
import com.gomu.festup.utils.toStringNuestro
import com.gomu.festup.vm.MainVM

@Composable
fun Evento(
    navController: NavController,
    mainVM: MainVM
) {
    val evento = mainVM.eventoMostrar.value!!
    val users = mainVM.getUsuariosEvento(evento).collectAsState(initial = emptyList()).value
    val cuadrillas = mainVM.getCuadrillasEvento(evento).collectAsState(initial = emptyList()).value
    val apuntado = mainVM.estaApuntado(mainVM.currentUser.value!!, evento.id)

    var imageUri by remember {
        mutableStateOf("http://34.16.74.167/eventoImages/${evento.id}.png")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row (
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            AsyncImage(
                model = imageUri,
                contentDescription = "Event image",
                onError = {
                    imageUri = "http://34.16.74.167/eventoImages/no-image.png"
                },
                placeholder = painterResource(id = R.drawable.no_image),
                modifier = Modifier
                    .height(150.dp)
                    .padding(16.dp)
            )
            DatosEvento(evento)
        }
        Text(
            text = evento.descripcion,
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier.padding(8.dp)
        )
        EstadisticasEvento(mainVM = mainVM, evento = evento, apuntado = apuntado)
        Divider(modifier = Modifier.padding(10.dp))
        Text(
            text = "Asistentes:",
            style = TextStyle(fontSize = 17.sp)
        )
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            contentPadding = PaddingValues(bottom = 70.dp)
        ) {
            items(users){ usuario ->
                UsuarioCard(usuario = usuario, mainVM = mainVM, navController = navController)
            }
            items(cuadrillas){cuadrilla ->
                CuadrillaCard(cuadrilla = cuadrilla, mainVM = mainVM, navController = navController, isRemoveAvailable = false)
            }
        }
    }
}

@Composable
fun DatosEvento(evento: Evento) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = evento.fecha.toStringNuestro(),
            style = TextStyle(fontSize = 16.sp),
            maxLines = 1,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = evento.localizacion,
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun EstadisticasEvento(mainVM: MainVM, evento: Evento, apuntado: Boolean){
    var apuntarse by remember { mutableStateOf(false) }
    var desapuntarse by remember { mutableStateOf(false) }
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edad media",
                    style = TextStyle(fontSize = 16.sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.onSecondary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp)
                        .wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${mainVM.calcularEdadMediaEvento(mainVM.eventoMostrar.value!!)}", // TODO EDAD MEDIA
                        color = Color.White,
                        style = TextStyle(fontSize = 24.sp)
                    )
                }
            }
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Personas",
                    style = TextStyle(fontSize = 16.sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.onSecondary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp)
                        .wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mainVM.numeroDeAsistentes(mainVM.eventoMostrar.value!!).toString(),
                        color = Color.White,
                        style = TextStyle(fontSize = 24.sp)
                    )
                }
            }
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Button(
                    onClick = { apuntarse = true },
                ) {
                    Text(
                        text = "Apuntarme",
                        style = TextStyle(fontSize = 17.sp)
                    )
                }

                Button(
                    onClick = { desapuntarse = true},
                ) {
                    Text(
                        text = "Desapuntarse",
                        style = TextStyle(fontSize = 17.sp)
                    )
                }
            }
        }
    }

    Apuntarse(show = apuntarse, apuntado = apuntado,  mainVM = mainVM) { apuntarse = false }
    Desapuntarse(show = desapuntarse , apuntado = apuntado, mainVM = mainVM) { desapuntarse = false }
}

