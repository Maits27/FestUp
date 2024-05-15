package com.gomu.festup.ui.elements.screens

import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.ui.vm.MainVM
import androidx.compose.material3.FabPosition
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.gomu.festup.ui.elements.components.EditImageIcon
import com.gomu.festup.ui.elements.components.cards.EventoCard
import com.gomu.festup.ui.elements.components.dialogs.EstasSeguroDialog
import com.gomu.festup.ui.elements.components.cards.UsuarioMiniCard
import com.gomu.festup.utils.openTelegram
import com.gomu.festup.utils.openWhatsApp
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.ui.platform.LocalConfiguration
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.elements.components.Imagen
import com.gomu.festup.ui.elements.components.cards.EventoMiniCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun PerfilCuadrilla(
    navController: NavController,
    mainVM: MainVM
) {
    val cuadrilla= mainVM.cuadrillaMostrar.value!!
    val usuariosCuadrilla = mainVM.usuariosCuadrilla().collectAsState(initial = emptyList())

    val integrante = mainVM.getIntegrante(cuadrilla, mainVM.currentUser.value!!).collectAsState(initial = emptyList())
    val pertenezco = integrante.value.isNotEmpty()

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

    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    if (isVertical){
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                if (pertenezco){ EliminarCuadrilla(navController, mainVM = mainVM, cuadrilla = cuadrilla) }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { padding ->
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(
                    modifier = Modifier
                        .pullRefresh(refreshState)
                        .verticalScroll(
                            scrollState,
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    TopProfileCuadrilla(
                        mainVM =  mainVM,
                        cuadrilla = cuadrilla,
                        pertenezco = pertenezco,
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
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    ListadoUsuarios(
                        usuarios = usuariosCuadrilla.value,
                        pertenezco,
                        mainVM,
                        navController,
                        numeroIntegrantes = usuariosCuadrilla.value.size
                    )
                    EventosCuadrilla(
                        mainVM = mainVM,
                        navController = navController,
                        cuadrilla = cuadrilla
                    )
                }
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
                    .fillMaxSize()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Box(
                    modifier = Modifier
                        .pullRefresh(refreshState)
                        .verticalScroll(scrollState),
                    contentAlignment = Alignment.Center
                ) {

                    TopProfileCuadrilla(
                        mainVM = mainVM,
                        cuadrilla = cuadrilla,
                        pertenezco = pertenezco,
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

                Spacer(modifier = Modifier.height(20.dp))

                if (pertenezco) {
                    EliminarCuadrilla(navController, mainVM = mainVM, cuadrilla = cuadrilla)
                }
            }
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp, 0.dp, 0.dp, 20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .weight(1f)
                    .fillMaxSize(),
            ) {
                ListadoUsuarios(
                    usuarios = usuariosCuadrilla.value,
                    pertenezco,
                    mainVM,
                    navController,
                    numeroIntegrantes = usuariosCuadrilla.value.size
                )
                EventosCuadrilla(
                    mainVM = mainVM,
                    navController = navController,
                    cuadrilla = cuadrilla
                )
            }
        }
    }
}


@Composable
fun EliminarCuadrilla(navController: NavController, mainVM: MainVM, cuadrilla: Cuadrilla) {
    var verificacion by rememberSaveable{ mutableStateOf(false) }
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    ExtendedFloatingActionButton(
        onClick = {verificacion=true},
        icon = {
            Icon(
                painterResource(id = R.drawable.delete),
                contentDescription = null
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.abandonar, cuadrilla.nombre)
            )
        },
        containerColor =  if (isVertical) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimary
    )
    EstasSeguroDialog(
        show = verificacion,
        mensaje = stringResource(R.string.est_s_seguro_de_que_deseas_abandonar_la_cuadrilla),
        onDismiss = { verificacion = false }
    ) { mainVM.eliminarIntegrante(cuadrilla) ; verificacion = false ; navController.popBackStack() }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun TopProfileCuadrilla(
    mainVM: MainVM,
    cuadrilla: Cuadrilla,
    pertenezco: Boolean,
    navController: NavController
){
    Row (
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        CuadrillaProfileImage(cuadrilla, pertenezco, mainVM, navController = navController)
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(15.dp),
        ) {
            Text(
                text = cuadrilla.descripcion,
                style = TextStyle(
                    fontSize = 15.sp
                ),
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(5.dp)
            )
            Row {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location")
                Text(
                    text = cuadrilla.lugar,
                    modifier = Modifier.padding(5.dp),
                    style = TextStyle(
                        fontSize = 15.sp
                    )
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CuadrillaProfileImage(
    cuadrilla: Cuadrilla,
    pertenezco: Boolean,
    mainVM: MainVM,
    navController: NavController
) {
    val context = LocalContext.current

    var imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse("http://34.16.74.167/cuadrillaProfileImages/${cuadrilla.nombre}.png"))
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri!=null) {
            imageUri = uri
            mainVM.updateCuadrillaImage(context, cuadrilla.nombre, imageUri)
        }
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        Box(Modifier.padding(16.dp)) {
            Imagen(imageUri, context, R.drawable.no_cuadrilla, 120.dp) {
                navController.navigate(
                    AppScreens.FullImageScreen.route + "/" +
                            "cuadrilla" + "/" +
                            cuadrilla.nombre
                )
            }
        }
        if(pertenezco) EditImageIcon(singlePhotoPickerLauncher = singlePhotoPickerLauncher)
    }


}



@Composable
fun Compartir(
    show:Boolean,
    accessToken: String,
    nombreCuadrilla: String,
    onDismiss: () -> Unit
){
    if(show){
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {},
            title = {
                Text(text = context.getString(R.string.token_conf)) },
            text = {
                Column {
                    Text(text = context.getString(R.string.recordatorio_token))
                    Spacer(modifier = Modifier.size(10.dp))
                    Row {
                        IconButton(onClick = { openWhatsApp(accessToken, nombreCuadrilla, context) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.whatsapp),
                                contentDescription = null,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                        IconButton(onClick = { openTelegram(accessToken, nombreCuadrilla, context) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.telegram),
                                contentDescription = null,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun Unirse(show:Boolean, accessToken: String, nombreCuadrilla: String,  onDismiss: () -> Unit, onConfirm: () -> Unit){
    if(show){

        var input by rememberSaveable {mutableStateOf("")}
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { onDismiss() },
            dismissButton = {
                TextButton(onClick = { onDismiss() }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (input == accessToken){
                        onConfirm()
                    }
                    else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.incorrect_token),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text(text = context.getString(R.string.unirse))
                }
            },
            title = {
                Text(text = context.getString(R.string.pregunta_unirse, nombreCuadrilla)) },
            text = {
                Column {
                    Text(text = context.getString(R.string.insert_token))
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        label = { Text(context.getString(R.string.token)) },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                }
            }
        )
    }
}
@Composable
fun ListadoUsuarios(
    usuarios: List<Usuario>,
    pertenezco: Boolean,
    mainVM: MainVM,
    navController: NavController,
    numeroIntegrantes: Int
){
    var showShare by rememberSaveable { mutableStateOf(false) }
    var showJoin by rememberSaveable { mutableStateOf(false) }
    val token = mainVM.getCuadrillaAccessToken(mainVM.cuadrillaMostrar.value!!.nombre)
    Log.d("TOKEN", token)

    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ){
        Text(
            text = stringResource(id = R.string.integrantes, numeroIntegrantes),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.weight(3f)
        )
        if (pertenezco){
            Button( modifier = Modifier.weight(1f), onClick = { showShare = true }) {
                Icon(painter = painterResource(id = R.drawable.send), "")
            }
        }
        else{
            Button( modifier = Modifier.weight(1f), onClick = { showJoin = true }) {
                Icon(painter = painterResource(id = R.drawable.join), "")
            }
        }
    }

    if (usuarios.isNotEmpty()){
        LazyRow(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            items(usuarios) {
                UsuarioMiniCard(usuario = it, mainVM = mainVM, navController = navController)
            }
        }
    }
    else{
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(id = R.string.no_users),
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }

    }
    Compartir(showShare, token, mainVM.cuadrillaMostrar.value!!.nombre) { showShare = false }
    Unirse( showJoin, token, mainVM.cuadrillaMostrar.value!!.nombre, onDismiss = {showJoin=false}) {
        mainVM.agregarIntegrante(mainVM.currentUser.value!!.username, mainVM.cuadrillaMostrar.value!!.nombre)
        showJoin = false
    }
}

@Composable
fun EventosCuadrilla(
    mainVM: MainVM,
    navController: NavController,
    cuadrilla: Cuadrilla
) {
    val eventos = mainVM.eventosCuadrilla(cuadrilla).collectAsState(initial = emptyList())
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
        if (isVertical) {
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
    else{
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "No hay eventos",
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    }
}