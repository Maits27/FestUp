package com.gomu.festup.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.gomu.festup.ui.components.cards.UsuarioCard
import com.gomu.festup.utils.toStringNuestro
import com.gomu.festup.vm.MainVM
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Evento(
    navController: NavController,
    mainVM: MainVM
) {
    val evento = mainVM.eventoMostrar.value!!
    val numAsistentes = mainVM.numeroDeAsistentes(evento)
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
            .padding(top = 13.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Row (
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(6.dp)
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
                        .width(150.dp)
                        .padding(start = 6.dp)
                        .clip(RoundedCornerShape(35.dp))
                        .align(Alignment.Top)
                )
                Spacer(modifier = Modifier.size(16.dp))
                DatosEvento(evento, mainVM.calcularEdadMediaEvento(mainVM.eventoMostrar.value!!), apuntado, mainVM)
            }
        }
        Row (
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.info),
                contentDescription = null
            )
            Text(
                text = "Descripcion",
                style = TextStyle(fontSize = 20.sp),
                modifier = Modifier.padding(8.dp)
            )
        }
        Text(
            text = evento.descripcion,
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp)
                .align(Alignment.Start)
        )

        Divider(modifier = Modifier.padding(10.dp))

        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Asistentes: $numAsistentes",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(3f)
            )
        }

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
fun DatosEvento(evento: Evento, edadMedia: Int, apuntado: Boolean, mainVM: MainVM) {

    var apuntarse by remember { mutableStateOf(false) }

    Column (
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(vertical = 30.dp)
    ) {
        Row (
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = null
            )
            Text(
                text = evento.fecha.toStringNuestro(),
                style = TextStyle(fontSize = 16.sp),
                maxLines = 1,
                modifier = Modifier.padding(8.dp)
            )
        }
        Row (
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.location),
                contentDescription = null
            )
            Text(
                text = evento.localizacion,
                style = TextStyle(fontSize = 16.sp),
                maxLines = 1,
                modifier = Modifier.padding(8.dp)
            )
        }
        Row (
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = "Edad media",
                    style = TextStyle(fontSize = 16.sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
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
                        text = edadMedia.toString(),
                        style = TextStyle(fontSize = 24.sp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(5.dp))

            Button(
                onClick = { apuntarse = true },
                shape = RoundedCornerShape(70),
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(horizontal = 2.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = null,
                    modifier = Modifier.size(8.dp)
                )
            }
        }
    }
    Apuntarse(show = apuntarse, apuntado = apuntado,  mainVM = mainVM) { apuntarse = false }
}
