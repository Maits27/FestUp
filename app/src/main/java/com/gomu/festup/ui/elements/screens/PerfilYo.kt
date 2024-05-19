package com.gomu.festup.ui.elements.screens

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomu.festup.MainActivity
import com.gomu.festup.R
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.elements.components.FestUpButton
import com.gomu.festup.ui.elements.components.Imagen
import com.gomu.festup.ui.elements.components.cards.CuadrillaMiniCard
import com.gomu.festup.ui.elements.components.cards.EventoCard
import com.gomu.festup.ui.elements.components.cards.EventoMiniCard
import com.gomu.festup.ui.elements.components.dialogs.EstasSeguroDialog
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.ui.vm.PreferencesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Perfil con información del [Usuario] y sus [Cuadrillas] y [Eventos].
 * Puede tratarse del usuario registrado en el momento o del perfil de
 * otro usuario. En cada caso aparecerá información y funciones diferentes.
 * Formato en horizontal y en vertical.
 */
@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun PerfilYo(
    navController: NavController,
    preferencesViewModel: PreferencesViewModel,
    yo: Boolean = false,
    mainVM: MainVM
) {
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val context = LocalContext.current
    var usuario = mainVM.currentUser.value!!

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

    if (!yo) {
        usuario = if(mainVM.usuarioMostrar.isNotEmpty()) mainVM.usuarioMostrar.last()!! else Usuario("", "", "", Date(), "")
        mainVM.alreadySiguiendo(usuario.username)
    }

    val alreadySiguiendo = mainVM.alreadySiguiendo

    val cuadrillas = mainVM.getCuadrillasUsuario(usuario).collectAsState(initial = emptyList())

    var verificacion by rememberSaveable{ mutableStateOf(false) }

    if (isVertical){
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(
                    modifier = Modifier
                        .pullRefresh(refreshState)
                        .verticalScroll(
                            scrollState,
                        ),
                    contentAlignment = Alignment.TopCenter
                ) {

                    TopProfile(
                        mainVM = mainVM,
                        preferencesVM = preferencesViewModel,
                        edad = mainVM.calcularEdad(usuario) ,
                        yo = yo,
                        alreadySiguiendo = alreadySiguiendo,
                        usuario = usuario,
                        navController = navController
                    )
                    PullRefreshIndicator(
                        refreshing = refresh,
                        state = refreshState,
                        modifier = Modifier.align(
                            Alignment.TopCenter,
                        ),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                ListadoCuadrillas(
                    cuadrillas = cuadrillas.value,
                    yo,
                    navController = navController,
                    mainVM = mainVM
                )
                EventosUsuario(usuario = usuario, mainVM = mainVM, navController = navController)
            }
        }
    }
    else{
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .pullRefresh(refreshState)
                        .verticalScroll(scrollState),
                    contentAlignment = Alignment.Center
                ) {
                    TopProfile(
                        mainVM = mainVM,
                        preferencesVM = preferencesViewModel,
                        edad = mainVM.calcularEdad(usuario),
                        yo = yo,
                        alreadySiguiendo = alreadySiguiendo,
                        usuario = usuario,
                        navController = navController
                    )

                    PullRefreshIndicator(
                        refreshing = refresh,
                        state = refreshState,
                        modifier = Modifier.align(
                            Alignment.TopCenter,
                        ),
                    )
                }

            }
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp, 0.dp, 0.dp, 20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .weight(1f)
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                ListadoCuadrillas(
                    cuadrillas = cuadrillas.value,
                    yo,
                    navController = navController,
                    mainVM = mainVM
                )
                EventosUsuario(usuario = usuario, mainVM = mainVM, navController = navController)
            }
        }
    }

    EstasSeguroDialog(
        show = verificacion,
        mensaje = stringResource(R.string.est_s_seguro_de_que_deseas_cerrar_sesi_n),
        onDismiss = { verificacion = false }
    ) { CoroutineScope(Dispatchers.IO).launch {
        withContext(Dispatchers.IO) {
            preferencesViewModel.changeUser("")
        }
        mainVM.serverOk.value = false
        mainVM.actualizarWidget(context)

        withContext(Dispatchers.Main) {
            (context as? Activity)?.finish()
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }}

}

@Composable
fun ListadoCuadrillas(
    cuadrillas: List<Cuadrilla>,
    yo: Boolean,
    navController: NavController,
    mainVM: MainVM
){
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = if (isVertical) 16.dp else 8.dp)
    ){
        Text(
            text = stringResource(id = R.string.cuadrillas)+":",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.weight(3f)
        )
        if (yo){
            FestUpButton(
                modifier = Modifier
                    .weight(1f),
                onClick = { navController.navigate(AppScreens.AddCuadrilla.route) }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add), "",
                )
            }
        }
    }
    if (cuadrillas.isNotEmpty()) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(cuadrillas) {
                CuadrillaMiniCard(
                    cuadrilla = it,
                    mainVM = mainVM,
                    navController = navController
                )
            }
        }
    }
    else {
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(R.string.no_cuadrilla),
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    }
}

@Composable
fun EventosUsuario(
    mainVM: MainVM,
    usuario: Usuario,
    navController: NavController
) {
    val eventos = mainVM.eventosUsuario(usuario).collectAsState(initial = emptyList())
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Text(
        text = stringResource(id = R.string.eventos)+":",
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(16.dp)
    )

    if (eventos.value.isNotEmpty()) {
        if (isVertical){
            LazyColumn {
                items(eventos.value) { evento ->
                    EventoCard(evento = evento, mainVM = mainVM, navController = navController)
                }
            }
        }
        else{
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(eventos.value) {
                    EventoMiniCard(
                        evento = it,
                        mainVM = mainVM,
                        navController = navController
                    )

                }
            }
        }

    }
    else {
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(id = R.string.no_events),
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    }

}


/**
 * Seguidores y seguidos en el perfil del usuario (conteo de estos)
 */
@Composable
fun SeguidoresYSeguidos(
    yo: Boolean,
    usuario: Usuario,
    mainVM: MainVM,
    preferencesVM: PreferencesViewModel,
    navController: NavController,
    alreadySiguiendo: MutableState<Boolean?>,
    modifier: Modifier = Modifier
){
    val seguidores = mainVM.listaSeguidores(usuario).collectAsState(initial = emptyList())
    val seguidos = mainVM.listaSeguidos(usuario).collectAsState(initial = emptyList())

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Seguidores(navController = navController, seguidores = seguidores, modifier = Modifier
                .weight(1f)
                .padding(end = 5.dp))
            Seguidos(navController = navController, seguidos = seguidos, modifier = Modifier
                .weight(1f)
                .padding(end = 5.dp))
        }
        if(!yo){
            FollowButton(
                alreadySiguiendo = alreadySiguiendo,
                mainVM = mainVM,
                preferencesVM = preferencesVM,
                usuario = usuario
            )
        }
    }
}

@Composable
fun Seguidores(navController: NavController, seguidores: State<List<Usuario>>, modifier: Modifier=Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(text = stringResource(id = R.string.seguidores)+":")
        TextButton(
            onClick = { navController.navigate(AppScreens.SeguidoresSeguidosList.route + "/0") },
            modifier = Modifier
                .padding(top = 16.dp)
                .background(
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Text(
                text = seguidores.value.size.toString(),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun Seguidos(navController: NavController, seguidos: State<List<Usuario>>, modifier: Modifier=Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(text = stringResource(id = R.string.seguidos)+":")
        TextButton(
            onClick = { navController.navigate(AppScreens.SeguidoresSeguidosList.route + "/1") },
            modifier = Modifier
                .padding(top = 16.dp)
                .background(
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Text(
                text = seguidos.value.size.toString(),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun TopProfile(
    mainVM: MainVM,
    preferencesVM: PreferencesViewModel,
    usuario: Usuario,
    navController: NavController,
    alreadySiguiendo: MutableState<Boolean?>,
    edad: Int,
    yo: Boolean
){
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 5.dp)
        ) {
            ProfileImage(usuario = usuario, yo = yo, navController = navController)
            Text(
                text = usuario.nombre,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = context.getString(R.string.age, edad.toString()),
                modifier = Modifier.padding(5.dp),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

        }

        SeguidoresYSeguidos(
            yo = yo,
            usuario = usuario,
            mainVM = mainVM,
            preferencesVM = preferencesVM,
            navController = navController,
            alreadySiguiendo = alreadySiguiendo,
            modifier = Modifier
                .weight(1.5f)
                .padding(0.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ProfileImage(
    usuario: Usuario,
    yo: Boolean,
    navController: NavController
) {
    val imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse("http://34.71.128.243/userProfileImages/${usuario.username}.png"))
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        Box(Modifier.padding(16.dp)) {
            Imagen(imageUri, R.drawable.no_user, 120.dp) {
                navController.navigate(
                    AppScreens.FullImageScreen.route + "/" +
                            "user" + "/" +
                            usuario.username
                )
            }
        }
        if(yo) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .clip(CircleShape)
                    .clickable(onClick = { navController.navigate(AppScreens.EditPerfil.route) })
            ) {
                //Añadir circle y edit
                Icon(painterResource(id = R.drawable.circle), contentDescription = null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                Icon(painterResource(id = R.drawable.edit), contentDescription = null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.surface)
            }
        }
    }
}

@Composable
fun FollowButton(
    alreadySiguiendo: MutableState<Boolean?>,
    mainVM: MainVM,
    preferencesVM: PreferencesViewModel,
    usuario: Usuario
) {
    val notificaciones = preferencesVM.receiveNotifications(mainVM.currentUser.value!!.username).collectAsState(initial = true).value

    val onClickFollow: () -> Unit = {
        mainVM.newSiguiendo(usuario.username)
        if (notificaciones){
            mainVM.subscribeToUser(mainVM.usuarioMostrar.last()!!.username)
        }
    }

    val onClickUnfollow: () -> Unit = {
        mainVM.unfollow(usuario.username)
        mainVM.unsubscribeFromUser(mainVM.usuarioMostrar.last()!!.username)
    }

    val buttonText = if (alreadySiguiendo.value != null && !alreadySiguiendo.value!!) stringResource(
        id = R.string.follow
    ) else stringResource(id = R.string.unfollow)

    val onClick = if (alreadySiguiendo.value != null && !alreadySiguiendo.value!!) onClickFollow else onClickUnfollow

    TextButton(
        onClick = { onClick() },
        modifier = Modifier
            .padding(top = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Text(
            text = buttonText,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
