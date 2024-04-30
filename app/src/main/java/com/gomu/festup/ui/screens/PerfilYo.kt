package com.gomu.festup.ui.screens

import android.graphics.Bitmap
import android.graphics.Picture
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens


@Composable
fun PerfilYo(
    mainNavController: NavController,
    navController: NavController,
    username: String = "",
    accederPerfilCuadrilla: (String)-> Unit = {}
) {
    Column (
        modifier = Modifier
            .fillMaxWidth(),
//            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.tertiary)
                .weight(1f)
        ){
            TopProfile(username = username, email = "user@gmail.com", edad = 18, true)
        }
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.onTertiary)
                .weight(2f)
        ){
            SeguidoresYSeguidos(username = username)
            ListadoCuadrillas(
                username = username,
                cuadrillas = listOf(
                    Cuadrilla(nombre = "Pikito", descripcion = "The best", lugar = "Bilbao"),
                    Cuadrilla(nombre = "Wekaland", descripcion = "The best 2", lugar = "Bilbao"),
                    Cuadrilla(nombre = "BANBU", descripcion = "The best 4", lugar = "Bilbao")
                ),
                true,
                accederPerfilCuadrilla = accederPerfilCuadrilla
            )
            BotonesPerfil(
                navController= navController,
                mainNavController = mainNavController,
                username = username)
        }
    }
}

//@Preview
//@Composable
//fun PerfilPreview(){
//    PerfilYo(
//        username = "username",
//        mainNavController = rememberNavController(),
//        navController = rememberNavController(),
//        accederPerfilCuadrilla = {})
//}

@Composable
fun BotonesPerfil(mainNavController: NavController, navController: NavController, username: String){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = { /*TODO*/ }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.tertiary)
        }
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = { /*TODO*/ }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.edit),
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.tertiary)
        }
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = {
                navController.popBackStack()
                mainNavController.popBackStack()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logout),
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.tertiary)
        }
    }
}


@Composable
fun ListadoCuadrillas(
    username: String,
    cuadrillas: List<Cuadrilla>,
    yo: Boolean,
    accederPerfilCuadrilla:(String) -> Unit
){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ){
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Cuadrillas de $username:",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(3f)
            )
            if (yo){
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            1.dp,
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(6.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),onClick = { /*TODO*/ }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add), "",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

    }
    when{
        cuadrillas.isNotEmpty() ->
        {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(6.dp)
            ) {
                items(cuadrillas) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(3.dp)
                            .clickable { accederPerfilCuadrilla(it.nombre) },
                        shape = CardDefaults.elevatedShape,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.account),
                                contentDescription = "",
                                modifier = Modifier.size(60.dp).weight(1f)
                            )
                            Column(
                                Modifier.padding(vertical = 10.dp).weight(4f)
                            ) {
                                Text(
                                    text = it.nombre,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 5.dp)
                                )
                                Text(
                                    text = it.lugar,
                                    modifier = Modifier.padding(vertical = 5.dp)
                                )
                            }
                            if (yo){
                                var verificacion by rememberSaveable{ mutableStateOf(false) }
                                Button(
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent
                                    ),onClick = { verificacion = true }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.delete),
                                        "",
                                        tint = MaterialTheme.colorScheme.onTertiary
                                    )
                                }
                                EstasSeguro(
                                    show = verificacion,
                                    mensaje = "Si eliminas esta cuadrilla tendrás que volver a solicitar entrar.",
                                    onDismiss = {verificacion = false}) {
                                    verificacion = false
                                    // TODO eliminar
                                }
                            }
                        }
                    }
                }
            }
        }else -> {
            Text(text = "Empty")
        }
    }

}
@Composable
fun EstasSeguro(show: Boolean, mensaje: String, onDismiss:()->Unit, onConfirm:() -> Unit){
    if(show){
        AlertDialog(
            onDismissRequest = {},
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text(text = "No")
                }
            },
            confirmButton = {
                TextButton(onClick = { onConfirm() }) {
                    Text(text = "Si")
                }
            },
            title = {
                Text(text = "¿Estás seguro?") },
            text = {
                Text(text = mensaje)
            }
        )
    }
}
@Composable
fun SeguidoresYSeguidos(username: String){
    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ){
            Text(text = "Seguidores")
            TextButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Text(
                    text = "16",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ){
            Text(text = "Seguidos")
            TextButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Text(
                    text = "20",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
@Composable
fun TopProfile(
    username: String,
    email: String,
    edad: Int,
    yo: Boolean
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
        if(yo) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .clip(CircleShape)
                    .clickable(onClick = { /*TODO*/ })
            ) {
                //Añadir circle y edit
                Icon(
                    painterResource(id = R.drawable.circle),
                    contentDescription = null,
                    Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Icon(
                    painterResource(id = R.drawable.edit),
                    contentDescription = null,
                    Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        }

    }
    Text(
        text = username,
        style = TextStyle(
            color = MaterialTheme.colorScheme.onTertiary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    )
    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        Column (
            Modifier
                .weight(2f)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ){

            Text(
                text = "Email: $email",
                modifier = Modifier.padding(5.dp),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontSize = 15.sp
                )
            )
            Text(
                text = "Edad: $edad",
                modifier = Modifier.padding(5.dp),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontSize = 15.sp
                )
            )
        }
        if(!yo){
            Column (
                Modifier
                    .weight(1f)
                    .padding(vertical = 0.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                TextButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.onTertiary, shape = RoundedCornerShape(10.dp))
                ) {
                    Text(text = "Follow", color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }

}