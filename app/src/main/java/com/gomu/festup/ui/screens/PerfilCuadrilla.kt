package com.gomu.festup.ui.screens

import android.graphics.Bitmap
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
import java.util.Date

@Composable
fun PerfilCuadrilla(
    navController: NavController,
    mainVM: MainVM
) {
    val cuadrilla= mainVM.cuadrillaMostrar.value!!
    var usuariosCuadrilla = mainVM.usuariosCuadrilla()
    if (usuariosCuadrilla.isEmpty()){
        usuariosCuadrilla = listOf(
            Usuario(username = "AingeruBeOr", nombre = "Aingeru", email = "1405bellido", password = "1", fechaNacimiento = Date(), profileImagePath = ""),
            Usuario(username = "Sergiom8", nombre = "Sergio", email = "sergiom8", password = "1", fechaNacimiento = Date(), profileImagePath = ""),
            Usuario(username = "NagoreGomez", nombre = "Nagore", email = "nagoregomez4", password = "1", fechaNacimiento = Date(), profileImagePath = ""),
            Usuario(username = "NagoreGomez", nombre = "Nagore", email = "nagoregomez4", password = "1", fechaNacimiento = Date(), profileImagePath = ""),
            Usuario(username = "NagoreGomez", nombre = "Nagore", email = "nagoregomez4", password = "1", fechaNacimiento = Date(), profileImagePath = ""),
            Usuario(username = "NagoreGomez", nombre = "Nagore", email = "nagoregomez4", password = "1", fechaNacimiento = Date(), profileImagePath = "")
        )
    }
    var pertenezco by rememberSaveable {mutableStateOf(true)}

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            EliminarCuadrilla(nombre = cuadrilla.nombre)
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
                    picture = null,
                    pertenezco = pertenezco)
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                ListadoUsuarios(
                    usuarios = usuariosCuadrilla,
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
    picture: Bitmap?,
    pertenezco: Boolean
){

    val profilePicture = painterResource(id = R.drawable.ic_launcher_background)
    Box(contentAlignment = Alignment.BottomEnd) {
        Box(Modifier.padding(16.dp)) {
            // Mientras no este la imagen mostrar una "cargando"
            if (profilePicture == null) {
                LoadingImagePlaceholder(size = 120.dp)
            } else {
                Image(
                    //bitmap = profilePicture.asImageBitmap(),
                    painter = profilePicture,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                )
                // Imagen redonda o cuadrada??
            }
        }
        // Icono para editar imagen
        if(pertenezco) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .clip(CircleShape)
                    .clickable(onClick = { /*TODO*/ })
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable {
                            mainVM.usuarioMostrar.value = it
                            navController.navigate(AppScreens.PerfilUser.route)
                        },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(13.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.account),
                            contentDescription = "",
                            modifier = Modifier.size(50.dp)
                        )
                        Column(
                            Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
                        ) {
                            Text(
                                text = it.username,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = it.nombre,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))

                    }
                }
            }
        }
    }
    else{
        Text(text = "Empty")
    }
}