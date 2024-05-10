package com.gomu.festup.ui.screens

import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.vm.MainVM
import androidx.compose.material3.FabPosition
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.gomu.festup.ui.components.EditImageIcon
import com.gomu.festup.ui.components.cards.EventoCard
import com.gomu.festup.ui.components.dialogs.EstasSeguroDialog
import com.gomu.festup.ui.components.cards.UsuarioMiniCard
import com.gomu.festup.utils.openTelegram
import com.gomu.festup.utils.openWhatsApp

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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            if (pertenezco){ EliminarCuadrilla(cuadrilla = cuadrilla, mainVM = mainVM) }
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
            TopProfileCuadrilla(
                mainVM =  mainVM,
                cuadrilla = cuadrilla,
                pertenezco = pertenezco
            )
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
@Composable
fun EliminarCuadrilla(cuadrilla: Cuadrilla, mainVM: MainVM) {
    var verificacion by rememberSaveable{ mutableStateOf(false) }
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
                text = "Abandonar ${cuadrilla.nombre}"
            )
        }
    )
    EstasSeguroDialog(
        show = verificacion,
        mensaje = "¿Estás seguro de que deseas abandonar la cuadrilla?",
        onDismiss = { verificacion = false }
    ) { mainVM.eliminarIntegrante(cuadrilla) ; verificacion = false  }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun TopProfileCuadrilla(
    mainVM: MainVM,
    cuadrilla: Cuadrilla,
    pertenezco: Boolean
){
    Row (
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        CuadrillaProfileImage(cuadrilla, pertenezco, mainVM)
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
    mainVM: MainVM
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
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.no_cuadrilla),
                onError = {
                    imageUri = Uri.parse("http://34.16.74.167/cuadrillaProfileImages/no-cuadrilla.png")
                },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
            )
        }
        // Icono para editar imagen
        if(pertenezco) EditImageIcon(singlePhotoPickerLauncher = singlePhotoPickerLauncher)
    }
}



@Composable
fun Compartir(show:Boolean, accessToken: String, nombreCuadrilla: String,  onDismiss: () -> Unit){
    if(show){
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {},
            title = {
                Text(text = "¿Estas seguro de que quieres compartir el codigo de la cuadrilla?") },
            text = {
                Column {
                    Text(text = "Recuerda que cualquier persona con el codigo podra unirse")
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
            confirmButton = {
                TextButton(onClick = {
                    if (input == accessToken){
                        onConfirm()
                    }
                    else {
                        Toast.makeText(
                            context,
                            "Código incorrecto. Por favor, inténtalo de nuevo.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text(text = "Unirme")
                }
            },
            title = {
                Text(text = "¿Quieres unirte a $nombreCuadrilla?") },
            text = {
                Column {
                    Text(text = "Introduce el codigo de la cuadrilla para unirte")
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        label = { Text("Codigo de la cuadrilla") },
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
            text = "Integrantes: $numeroIntegrantes",
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
            Modifier
                .padding(horizontal = 40.dp, vertical = 80.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "No hay usuarios",
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(
                    fontSize = 18.sp,
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

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Eventos",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        LazyColumn {
            items(eventos.value) { evento ->
                EventoCard(evento = evento, mainVM = mainVM, navController = navController)
            }
        }
    }
}