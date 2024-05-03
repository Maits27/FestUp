package com.gomu.festup.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.vm.MainVM
import androidx.compose.material3.FabPosition
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import coil.compose.AsyncImage
import com.gomu.festup.ui.components.UsuarioCard
import java.util.Date

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
                    cuadrilla = cuadrilla,
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

@Composable
fun TopProfileCuadrilla(
    cuadrilla: Cuadrilla,
    pertenezco: Boolean
){


    var imageUri by remember {
        // TODO esto tendrá que ser cuadrilla.profileImagePath
        mutableStateOf<Uri?>(Uri.parse("http://34.16.74.167/cuadrillaProfileImages/no-cuadrilla.png"))
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imageUri = uri
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        Box(Modifier.padding(16.dp)) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
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
        text = "18",
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
fun Unirse(show:Boolean, onConfirm: (String) -> Unit){
    if(show){
        var token by rememberSaveable {mutableStateOf("")}
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { onConfirm(token) }) {
                    Text(text = "Unirme")
                }
                Button(onClick = { onConfirm(token)}) {
                    
                }
            },
            title = {
                Text(text = "Unirte a la cuadrilla") },
            text = {
                Text(text = "Introduce el token de cuadrilla " +
                        "(si no tienes uno, pide que te lo mande un miembro de esta).")
                OutlinedTextField(
                    value = token,
                    onValueChange = {token = it},
                    label = { Text("Token de la cuadrilla") },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
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
                onClick = { /*TODO boton enviar token*/ }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.send), "",
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
}