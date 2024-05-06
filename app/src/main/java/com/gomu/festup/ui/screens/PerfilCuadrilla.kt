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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
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
import coil.compose.AsyncImage
import com.gomu.festup.ui.components.UsuarioCard
import com.gomu.festup.utils.openWhatsApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun PerfilCuadrilla(
    navController: NavController,
    mainVM: MainVM
) {
    val cuadrilla= mainVM.cuadrillaMostrar.value!!
    val usuariosCuadrilla = mainVM.usuariosCuadrilla().collectAsState(initial = emptyList())


    var integrante = mainVM.getIntegrante(cuadrilla, mainVM.currentUser.value!!).collectAsState(initial = emptyList())
    Log.d("AAAAAA", integrante.value.isEmpty().toString())
    var pertenezco = false
    if (integrante.value.isNotEmpty()){
        pertenezco=true
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            if (pertenezco){ EliminarCuadrilla(nombre = cuadrilla.nombre) }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ){
                TopProfileCuadrilla(
                    mainVM =  mainVM,
                    cuadrilla = cuadrilla,
                    numIntegrantes = usuariosCuadrilla.value.size,
                    pertenezco = pertenezco
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                ListadoUsuarios(
                    usuarios = usuariosCuadrilla.value,
                    pertenezco,
                    mainVM,
                    navController
                )
            }

        }
    }
}
@Composable
fun EliminarCuadrilla(nombre: String) {
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
                text = "Eliminar $nombre"
            )
        }
    )
    EstasSeguro(
        show = verificacion,
        mensaje = "Si confirmas se eliminará la cuadrilla para todos los usuarios.",
        onDismiss = { verificacion = false }) {
        verificacion = false
        // TODO eliminar
    }

}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun TopProfileCuadrilla(
    mainVM: MainVM,
    cuadrilla: Cuadrilla,
    numIntegrantes: Int,
    pertenezco: Boolean
){
    val context = LocalContext.current
    var imageUri by remember {
        // TODO esto tendrá que ser cuadrilla.profileImagePath
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
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
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
        if(pertenezco) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .clip(CircleShape)
                    .clickable(onClick = {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    })
            ) {
                //Añadir circle y edit
                Icon(painterResource(id = R.drawable.circle), contentDescription = null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                Icon(painterResource(id = R.drawable.edit), contentDescription = null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.surface)
            }
        }
    }
    Text(
        text = cuadrilla.nombre,
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    )
    Text(
        text = cuadrilla.lugar,
        modifier = Modifier.padding(5.dp),
        style = TextStyle(
            fontSize = 15.sp
        )
    )
    // TODO
    Text(
        text = "Integrantes: ${numIntegrantes}",
        modifier = Modifier.padding(5.dp),
        style = TextStyle(
            fontSize = 15.sp
        )
    )

    Text(
        text = cuadrilla.descripcion,
        style = TextStyle(
            fontSize = 15.sp
        )
    )

}



@Composable
fun Compartir(show:Boolean, accessToken: String, nombreCuadrilla: String,  onConfirm: () -> Unit){
    if(show){
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { onConfirm() },
            confirmButton = {},
            title = {
                Text(text = "¿Estas seguro de que quieres compartir el codigo de la cuadrilla?") },
            text = {
                Column {
                    Text(text = "Recuerda que cualquier persona con el codigo podra unirse")
                    Row {
                        IconButton(onClick = { openWhatsApp(accessToken, nombreCuadrilla, context) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.whatsapp),
                                contentDescription = null,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.message),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun Unirse(show:Boolean, accessToken: String, nombreCuadrilla: String,  onConfirm: () -> Unit){
    if(show){

        var input by rememberSaveable {mutableStateOf("")}
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { onConfirm() },
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
    navController: NavController
){
    var showShare by rememberSaveable { mutableStateOf(false) }
    var showJoin by rememberSaveable { mutableStateOf(false) }
    val token = mainVM.getCuadrillaAccessToken(mainVM.cuadrillaMostrar.value!!.nombre)

    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ){
        Text(
            text = "Integrantes:",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.weight(3f)
        )
        if (pertenezco){
            Button(
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    showShare = true
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.send), "",
                )
            }
        }
        else{
            Button(
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    showJoin = true
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.join), "",
                )
            }
        }
    }

    if (usuarios.isNotEmpty()){
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            contentPadding = PaddingValues(bottom=80.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            items(usuarios) {
                UsuarioCard(usuario = it, mainVM = mainVM, navController = navController)
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
    Unirse( showJoin, token, mainVM.cuadrillaMostrar.value!!.nombre) {
        mainVM.agregarIntegrante(mainVM.currentUser.value!!.username, mainVM.cuadrillaMostrar.value!!.nombre)
        showJoin = false
    }
}