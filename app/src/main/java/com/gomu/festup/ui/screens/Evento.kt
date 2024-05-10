package com.gomu.festup.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Repositories.ICuadrillaRepository
import com.gomu.festup.LocalDatabase.Repositories.IEventoRepository
import com.gomu.festup.LocalDatabase.Repositories.ILoginSettings
import com.gomu.festup.LocalDatabase.Repositories.IUserRepository
import com.gomu.festup.R
import com.gomu.festup.ui.components.dialogs.Apuntarse
import com.gomu.festup.ui.components.cards.CuadrillaCard
import com.gomu.festup.ui.components.cards.CuadrillaCardParaEventosAlert
import com.gomu.festup.ui.components.cards.UsuarioCard
import com.gomu.festup.ui.components.cards.UsuarioCardParaEventosAlert
import com.gomu.festup.utils.addEventOnCalendar
import com.gomu.festup.utils.toStringNuestro
import com.gomu.festup.vm.MainVM

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

    var apuntarse by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }

    val context = LocalContext.current

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
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.weight(1f)
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Event image",
                        error = painterResource(id = R.drawable.no_image),
                        placeholder = painterResource(id = R.drawable.no_image),
                        modifier = Modifier
                            .height(150.dp)
                            .width(150.dp)
                            .clip(RoundedCornerShape(35.dp))
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_calendar),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    addEventOnCalendar(
                                        context,
                                        evento.nombre,
                                        evento.fecha.time + 86400000
                                    )
                                    Toast
                                        .makeText(
                                            context,
                                            "Evento añadido al calendario correctamente",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.info),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { showInfo = true }
                        )
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                DatosEvento(
                    evento,
                    mainVM.calcularEdadMediaEvento(mainVM.eventoMostrar.value!!),
                    numAsistentes,
                    Modifier.padding(vertical = 1.dp).weight(1f)
                )
            }
        }
        Button(
            onClick = { apuntarse = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Apuntarse ")
        }
        Text(
            text = "Asistentes: ",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(10.dp).align(Alignment.Start)
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
    Apuntarse(show = apuntarse, apuntado = apuntado,  mainVM = mainVM) {
        apuntarse = false
        mainVM.actualizarWidget(context)
    }
    ShowInfo(show = showInfo, evento = evento) {
        showInfo = false
        mainVM.actualizarWidget(context)
    }
}

@Composable
fun DatosEvento(evento: Evento, edadMedia: Int, numAsistentes: Int, modifier: Modifier=Modifier) {

    Column (
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    ) {
        Row (
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 6.dp, horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
            Text(
                text = evento.fecha.toStringNuestro(),
                style = TextStyle(fontSize = 16.sp),
                maxLines = 1,
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Row (
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 6.dp, horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.location),
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
            Text(
                text = evento.localizacion,
                style = TextStyle(fontSize = 16.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = "Edad media",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.inverseOnSurface,
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
            Spacer(modifier = Modifier.size(16.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = "Asistentes",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp)
                        .wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = numAsistentes.toString(),
                        style = TextStyle(fontSize = 24.sp)
                    )
                }
            }
        }
    }
}


@Composable
fun ShowInfo(show: Boolean, evento: Evento, onDismiss: () -> Unit){
    if(show){
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = { onDismiss()}) {
                    Text(text = "Cerrar")
                }
            },
            title = {
                Text(text = "Información de ${evento.nombre}")
            },
            text = {
                Text(text = evento.descripcion)
            }
        )
    }
}