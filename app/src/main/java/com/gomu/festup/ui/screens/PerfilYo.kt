package com.gomu.festup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
    username: String = "Root",
    yo: Boolean = false
) {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiary)
                .fillMaxWidth()
        ) {
            TopProfile(username = username, email = "user@gmail.com", edad = 18, yo)
        }
        SeguidoresYSeguidos(yo)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            ListadoCuadrillas(
                cuadrillas = listOf(
                    Cuadrilla(nombre = "Pikito", descripcion = "The best", lugar = "Bilbao"),
                    Cuadrilla(nombre = "Wekaland", descripcion = "The best 2", lugar = "Bilbao"),
                    Cuadrilla(nombre = "BANBU", descripcion = "The best 4", lugar = "Bilbao"),
                    Cuadrilla(nombre = "Wekaland", descripcion = "The best 2", lugar = "Bilbao"),
                    Cuadrilla(nombre = "BANBU", descripcion = "The best 4", lugar = "Bilbao"),
                ),
                yo,
                navController = navController
            )
        }
        if (yo) {
            BotonesPerfil(
                navController= navController,
                mainNavController = mainNavController,
                username = username)
        }
    }
}


@Composable
fun ListadoCuadrillas(
    cuadrillas: List<Cuadrilla>,
    yo: Boolean,
    navController: NavController
){
    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ){
        Text(
            text = "Cuadrillas",
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
                ),
                onClick = { navController.navigate(AppScreens.AddCuadrilla.route) }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add), "",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
    if (cuadrillas.isNotEmpty()) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(cuadrillas) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable { navController.navigate(AppScreens.PerfilCuadrilla.route) },
                    shape = CardDefaults.elevatedShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
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
                                text = it.nombre,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = it.lugar,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        if (yo) {
                            var verificacion by rememberSaveable { mutableStateOf(false) }
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent
                                ), onClick = { verificacion = true }
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
                                onDismiss = { verificacion = false }) {
                                verificacion = false
                                // TODO eliminar
                            }
                        }
                    }
                }
            }
        }
    }
    else {
        Text(text = "Empty")
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
fun SeguidoresYSeguidos(yo: Boolean){
    Row (
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 16.dp, end = 16.dp)
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
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
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
        if(!yo){
            Spacer(modifier = Modifier.weight(1f))
            Column (
                Modifier
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .align(Alignment.Bottom)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(10.dp)
                    ),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                TextButton(
                    onClick = { /*TODO*/ },
                ) {
                    Text(
                        text = "Follow",
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
}

@Composable
fun BotonesPerfil(mainNavController: NavController, navController: NavController, username: String){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.tertiary)
        }
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.edit),
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.tertiary)
        }
        IconButton(
            onClick = {
                navController.popBackStack()
                mainNavController.popBackStack()
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logout),
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.tertiary)
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
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    )
    Text(
        text = email,
        modifier = Modifier.padding(5.dp),
        style = TextStyle(
            fontSize = 15.sp
        )
    )
    Text(
        text = "$edad años",
        modifier = Modifier.padding(5.dp),
        style = TextStyle(
            fontSize = 15.sp
        )
    )

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PerfilYoPreviewYo() {
    PerfilYo(
        mainNavController = rememberNavController(),
        navController = rememberNavController(),
        yo = true
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PerfilYoPreviewUser() {
    PerfilYo(
        mainNavController = rememberNavController(),
        navController = rememberNavController(),
        yo = false
    )
}