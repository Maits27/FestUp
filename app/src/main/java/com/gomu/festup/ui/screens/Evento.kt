package com.gomu.festup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.components.CuadrillaCard
import com.gomu.festup.ui.components.UsuarioCard
import com.gomu.festup.vm.MainVM
import java.util.Date

@Composable
fun Evento(
    navController: NavController,
    mainVM: MainVM
) {
    val evento = mainVM.eventoMostrar.value!!
    val users = mainVM.getUsuariosEvento(evento).collectAsState(initial = emptyList()).value
    val cuadrillas = mainVM.getCuadrillasEvento(evento).collectAsState(initial = emptyList()).value
    val apuntado = mainVM.estaApuntado(mainVM.currentUser.value!!, evento.id)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
        AsyncImage(
            // TODO esto será evento.eventoImagePath
            model = "http://34.16.74.167/eventoImages/no-image.png",
            contentDescription = "Event image",
            placeholder = painterResource(id = R.drawable.party),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        )
        DatosEvento(evento)
        Divider(modifier = Modifier.padding(10.dp))
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
    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Fecha",
                style = TextStyle(fontSize = 16.sp),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = evento.fecha.toString(),
                style = TextStyle(fontSize = 16.sp),
                maxLines = 1,
                modifier = Modifier.padding(8.dp)
            )
        }
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ){
            // Línea para la ubicación
            Text(
                text = "Ubicación",
                style = TextStyle(fontSize = 16.sp),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = evento.localizacion,
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
    Text(
        text = evento.descripcion,
        style = TextStyle(fontSize = 16.sp),
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun EstadisticasEvento(mainVM: MainVM, evento: Evento, apuntado: Boolean){
    Row (
        horizontalArrangement = Arrangement.Start,
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
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSecondary,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "1",
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
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSecondary,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = evento.numeroAsistentes.toString(),
                    color = Color.White,
                    style = TextStyle(fontSize = 24.sp)
                )
            }
        }
        Column (
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Bottom)
        ){
            if (apuntado){
                Button(onClick = { mainVM.desapuntarse(mainVM.currentUser.value!!, evento) }) {
                    Text(
                        text = "Desapuntarse",
                        style = TextStyle(fontSize = 17.sp)
                    )
                }
            }else{
                Button(onClick = { mainVM.apuntarse(mainVM.currentUser.value!!, evento) }) {
                    Text(
                        text = "Apuntarme",
                        style = TextStyle(fontSize = 17.sp)
                    )
                }
            }
        }
    }
}
