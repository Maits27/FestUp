package com.gomu.festup.ui.screens

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Seguidores
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.components.cards.CuadrillaMiniCard
import com.gomu.festup.ui.components.cards.EventoCard
import com.gomu.festup.vm.MainVM


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun PerfilYo(
    mainNavController: NavController,
    navController: NavController,
    yo: Boolean = false,
    recibirNotificaciones: Boolean,
    mainVM: MainVM
) {
    var usuario = mainVM.currentUser.value!!

    if (!yo) {
        usuario = mainVM.usuarioMostrar.value!!
        mainVM.alreadySiguiendo(usuario.username)
    }

    val alreadySiguiendo = mainVM.alreadySiguiendo

    val cuadrillas = mainVM.getCuadrillasUsuario(usuario).collectAsState(initial = emptyList())

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
            TopProfile(
                mainVM = mainVM,
                edad = mainVM.calcularEdad(usuario),
                yo = yo,
                recibirNotificaciones = recibirNotificaciones,
                alreadySiguiendo = alreadySiguiendo,
                usuario = usuario,
                navController = navController
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp))
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
        if (yo) {
            BotonesPerfil(
                navController= navController,
                mainNavController = mainNavController
            )
        }
    }
}


@Composable
fun ListadoCuadrillas(
    cuadrillas: List<Cuadrilla>,
    yo: Boolean,
    navController: NavController,
    mainVM: MainVM
){
    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ){
        Text(
            text = "Cuadrillas",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.weight(3f)
        )
        if (yo){
            Button(
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
            Modifier
                .padding(horizontal = 40.dp, vertical = 80.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "No hay cuadrillas",
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(
                    fontSize = 18.sp,
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


@Composable
fun SeguidoresYSeguidos(
    yo: Boolean,
    recibirNotificaciones: Boolean,
    usuario: Usuario,
    mainVM: MainVM,
    navController: NavController,
    alreadySiguiendo: MutableState<Boolean?>
){
    val seguidores = mainVM.listaSeguidores(usuario).collectAsState(initial = emptyList())
    val seguidos = mainVM.listaSeguidos(usuario).collectAsState(initial = emptyList())

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Seguidores(navController = navController, seguidores = seguidores)
            Seguidos(navController = navController, seguidos = seguidos)
        }
        if(!yo){
            FollowButton(
                alreadySiguiendo = alreadySiguiendo,
                mainVM = mainVM,
                recibirNotificaciones = recibirNotificaciones,
                usuario = usuario
            )
        }
    }
}

@Composable
fun Seguidores(navController: NavController, seguidores: State<List<Usuario>>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = 16.dp, end = 16.dp)
    ) {
        Text(text = "Seguidores")
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
fun Seguidos(navController: NavController, seguidos: State<List<Usuario>>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = 16.dp, start = 16.dp)
    ) {
        Text(text = "Seguidos")
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

@Composable
fun BotonesPerfil(
    mainNavController: NavController,
    navController: NavController
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ){
        IconButton(
            onClick = { navController.navigate(AppScreens.Ajustes.route) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "Settings")
        }
        IconButton(
            onClick = { navController.navigate(AppScreens.EditPerfil.route) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.edit),
                contentDescription = "Edit")
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
                contentDescription = "Logout")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun TopProfile(
    mainVM: MainVM,
    usuario: Usuario,
    navController: NavController,
    alreadySiguiendo: MutableState<Boolean?>,
    recibirNotificaciones: Boolean,
    edad: Int,
    yo: Boolean
){
    val context = LocalContext.current
    var imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse("http://34.16.74.167/userProfileImages/${usuario.username}.png"))
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        Log.d("IMAGEN", "0")
        if (uri!=null) {
            imageUri = uri
            mainVM.updateUserImage(context, usuario.username, uri)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImage(username = usuario.username, yo = yo, singlePhotoPickerLauncher = singlePhotoPickerLauncher)
            Text(
                text = usuario.nombre,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "$edad años",
                modifier = Modifier.padding(5.dp),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
        SeguidoresYSeguidos(
            yo = yo,
            recibirNotificaciones = recibirNotificaciones,
            usuario = usuario,
            mainVM = mainVM,
            navController = navController,
            alreadySiguiendo = alreadySiguiendo
        )
    }
}

@Composable
fun ProfileImage(
    username: String,
    yo: Boolean,
    singlePhotoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>,
) {
    var imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse("http://34.16.74.167/userProfileImages/$username.png"))
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        Box(Modifier.padding(16.dp)) {
            AsyncImage(
                model = imageUri,
                contentDescription = "User image",
                onError = {
                    imageUri = Uri.parse("http://34.16.74.167/userProfileImages/no-user.png")
                },
                placeholder = painterResource(id = R.drawable.no_user),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        }
        // Icono para editar imagen
        if(yo) {
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
}

@Composable
fun FollowButton(
    alreadySiguiendo: MutableState<Boolean?>,
    mainVM: MainVM,
    recibirNotificaciones: Boolean,
    usuario: Usuario
) {

    val onClickFollow: () -> Unit = {
        mainVM.newSiguiendo(usuario.username)
        if(recibirNotificaciones){
            mainVM.subscribeToUser(mainVM.usuarioMostrar.value!!.username)
        }
    }

    val onClickUnfollow: () -> Unit = {
        mainVM.unfollow(usuario.username)
        mainVM.unsubscribeFromUser(mainVM.usuarioMostrar.value!!.username)
    }

    val buttonText = if (alreadySiguiendo.value != null && !alreadySiguiendo.value!!) "Follow" else "Unfollow"

    val onClick = if (alreadySiguiendo.value != null && !alreadySiguiendo.value!!) onClickFollow else onClickUnfollow

    TextButton(
        onClick = { onClick() },
        modifier = Modifier
            .padding(vertical = 16.dp)
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

