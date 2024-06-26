package com.gomu.festup.ui.elements.screens

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomu.festup.R
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.Evento
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.elements.components.FestUpButton
import com.gomu.festup.ui.elements.components.ImagenEvento
import com.gomu.festup.ui.elements.components.cards.CuadrillaCard
import com.gomu.festup.ui.elements.components.cards.UsuarioCard
import com.gomu.festup.ui.elements.components.dialogs.Apuntarse
import com.gomu.festup.ui.elements.components.dialogs.EstasSeguroDialog
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.utils.addEventOnCalendar
import com.gomu.festup.utils.toStringNuestro
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Pantalla con la información de un [Evento],
 * sus [CuadrillasAsistentes] y [UsuariosAsistentes].
 * Aparece su visualización en horizontal y vertical.
 */
@Composable
fun Evento(
    navController: NavController,
    mainVM: MainVM
) {
    val context = LocalContext.current
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    val evento = mainVM.eventoMostrar.value!!
    val numAsistentes = mainVM.numeroDeAsistentes(evento)
    val users = mainVM.getUsuariosEvento(evento).collectAsState(initial = emptyList()).value
    val cuadrillas = mainVM.getCuadrillasEvento(evento).collectAsState(initial = emptyList()).value
    val apuntado = mainVM.estaApuntado(mainVM.currentUser.value!!, evento.id)

    var apuntarse by remember { mutableStateOf(false) }

    if(isVertical){
        EventoVertical(context = context, navController = navController, mainVM = mainVM, evento = evento, numAsistentes = numAsistentes, users = users, cuadrillas = cuadrillas
        ) {
            apuntarse = true
        }
    }else{
        EventoHorizontal(context = context, navController = navController, mainVM = mainVM, evento = evento, numAsistentes = numAsistentes, users = users, cuadrillas = cuadrillas
        ) {
            apuntarse = true
        }
    }

    Apuntarse(show = apuntarse, apuntado = apuntado,  mainVM = mainVM) {
        apuntarse = false
        mainVM.actualizarWidget(context)
    }

}

/**
 * Estructuras principales de la pantalla (vertical y horizontal)
 */
@Composable
fun EventoVertical(
    context: Context, navController: NavController,
    mainVM: MainVM, evento: Evento, numAsistentes: Int,
    users: List<Usuario>, cuadrillas: List<Cuadrilla>,
    onApuntarse : () -> Unit
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 13.dp)
    ) {
        CardVertical(context, mainVM, evento, numAsistentes, navController = navController)
        ColumnaAsistentes(
            context = context,
            navController = navController,
            mainVM = mainVM, users = users,
            cuadrillas = cuadrillas,
            modifier = Modifier.fillMaxWidth()
        ) {
            onApuntarse()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EventoHorizontal(
    context: Context,
    navController: NavController,
    mainVM: MainVM,
    evento: Evento,
    numAsistentes: Int,
    users: List<Usuario>,
    cuadrillas: List<Cuadrilla>,
    onApuntarse : () -> Unit
){
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        var refresh by remember{ mutableStateOf(false) }
        val scrollState = rememberScrollState()
        val refreshState = rememberPullRefreshState(
            refreshing = refresh,
            onRefresh = {
                CoroutineScope(Dispatchers.IO).launch{
                    refresh = true
                    mainVM.actualizarDatos()
                    refresh = false
                }
            },
        )
        Box(
            modifier = Modifier
                .pullRefresh(refreshState)
                .verticalScroll(
                    scrollState,
                )
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            CardHorizontal(context, mainVM, evento, numAsistentes, navController)
            PullRefreshIndicator(
                refreshing = refresh,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }

        ColumnaAsistentes(
            context = context, navController = navController, mainVM = mainVM,
            users = users, cuadrillas = cuadrillas, modifier = Modifier.weight(1f)) {
            onApuntarse()
        }

    }
}

/**
 * Información general del evento en la card (estructuras en vertical y horizontal)
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardVertical(context: Context, mainVM: MainVM, evento: Evento, numAsistentes: Int, navController: NavController){
    var refresh by remember{ mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val refreshState = rememberPullRefreshState(
        refreshing = refresh,
        onRefresh = {
            CoroutineScope(Dispatchers.IO).launch{
                refresh = true
                mainVM.actualizarDatos()
                refresh = false
            }
        },
    )
    Box(
        modifier = Modifier
            .padding(bottom = 14.dp)
            .pullRefresh(refreshState)
            .verticalScroll(
                scrollState,
            ),
        contentAlignment = Alignment.Center
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
                val imageUri by remember {
                    mutableStateOf<Uri?>(Uri.parse("http://34.71.128.243/eventoImages/${evento.id}.png"))
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 3.dp)
                ) {

                    ImagenEvento(imageUri, R.drawable.no_image, 150.dp ) {
                        navController.navigate(
                            AppScreens.FullImageScreen.route + "/" +
                                    "evento" + "/" +
                                    evento.id
                        )
                    }
                    IconosEvento(context = context, mainVM = mainVM, evento = evento, modifier = Modifier.padding(top = 12.dp))
                }
                DatosEvento(evento, mainVM.calcularEdadMediaEvento(mainVM.eventoMostrar.value!!), numAsistentes,
                    Modifier
                        .padding(5.dp)
                        .weight(1.5f))
            }
        }
        PullRefreshIndicator(
            refreshing = refresh,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}


@Composable
fun CardHorizontal(context: Context, mainVM: MainVM, evento: Evento, numAsistentes: Int, navController: NavController, modifier: Modifier = Modifier){
    val imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse("http://34.71.128.243/eventoImages/${evento.id}.png"))
    }
    val edadMedia = mainVM.calcularEdadMediaEvento(mainVM.eventoMostrar.value!!)

    Column(
        modifier = modifier
            .fillMaxHeight(),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Row(
                modifier = Modifier.padding(start = 6.dp, bottom = 6.dp, top = 20.dp)
            ) {
                ImagenEvento(imageUri, R.drawable.no_image, 110.dp ) {
                    navController.navigate(
                        AppScreens.FullImageScreen.route + "/" +
                                "evento" + "/" +
                                evento.id
                    )
                }


                DatosEvento(evento, edadMedia, numAsistentes,
                    Modifier.padding(vertical = 5.dp, horizontal = 10.dp))

            }
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier =  Modifier.padding(start = 6.dp, bottom = 16.dp, top = 10.dp, end = 6.dp)
            ) {
                EdadMediaYAsistentes(edadMedia = edadMedia, numAsistentes = numAsistentes, Modifier.weight(2f))
                IconosEvento(context = context, mainVM = mainVM, evento = evento, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun IconosEvento(context: Context, mainVM: MainVM, evento: Evento, modifier: Modifier = Modifier){
    var showInfo by remember { mutableStateOf(false) }
    var showAddCalendar by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.add_calendar),
            contentDescription = null,
            modifier = Modifier
                .size(25.dp)
                .weight(1f)
                .clickable {
                    showAddCalendar = true
                }
        )
        Icon(
            painter = painterResource(id = R.drawable.info),
            contentDescription = null,
            modifier = Modifier
                .size(25.dp)
                .weight(1f)
                .clickable { showInfo = true }
        )
    }
    ShowInfo(show = showInfo, evento = evento) {
        showInfo = false
        mainVM.actualizarWidget(context)
    }
    EstasSeguroDialog(show = showAddCalendar, mensaje = context.getString(R.string.estas_seguro_calendar, evento.nombre), onDismiss = { showAddCalendar=false }) {
        addEventOnCalendar(context = context, title = evento.nombre, evento.fecha.time + 86400000)
        Toast
            .makeText(
                context,
                context.getString(R.string.evento_add_calendar_ok),
                Toast.LENGTH_SHORT
            )
            .show()
        showAddCalendar = false
    }
}

/**
 * Información de asistentes
 */

@Composable
fun ColumnaAsistentes(
    context: Context, navController: NavController, mainVM: MainVM,
    users: List<Usuario>, cuadrillas: List<Cuadrilla>,
    modifier: Modifier=Modifier, onApuntarse: () -> Unit
){
    Column (
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        FestUpButton(
            onClick = { onApuntarse() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 14.dp)
        ) {
            Text(text = context.getString(R.string.apuntarse))
        }
        Text(
            text = context.getString(R.string.asistentes),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                .align(Alignment.Start)
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
                CuadrillaCard(cuadrilla = cuadrilla, mainVM = mainVM, navController = navController)
            }
        }
    }
}


@Composable
fun DatosEvento(
    evento: Evento, edadMedia: Int,
    numAsistentes: Int, modifier: Modifier=Modifier
) {
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
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

        if (isVertical){
            EdadMediaYAsistentes(edadMedia = edadMedia, numAsistentes = numAsistentes,
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp))
        }

    }
}

@Composable
fun EdadMediaYAsistentes(edadMedia: Int, numAsistentes: Int, modifier: Modifier = Modifier){
    val context = LocalContext.current
    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top,
        modifier = modifier.padding(start = 5.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ){
            Text(
                text = context.getString(R.string.edad_media),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally)
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
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ){
            Text(
                text = context.getString(R.string.asistentes),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally)
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

@Composable
fun ShowInfo(show: Boolean, evento: Evento, onDismiss: () -> Unit){
    if(show){
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = { onDismiss()}) {
                    Text(text = stringResource(id = R.string.cerrar))
                }
            },
            title = {
                Text(text = stringResource(id = R.string.info_evento, evento.nombre))
            },
            text = {
                Text(text = evento.descripcion)
            }
        )
    }
}
