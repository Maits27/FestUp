package com.gomu.festup.ui.screens

import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.components.CuadrillaCard
import com.gomu.festup.vm.MainVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun PerfilYo(
    mainNavController: NavController,
    navController: NavController,
    yo: Boolean = false,
    mainVM: MainVM
) {
    var usuario = mainVM.currentUser.value!!
    if (!yo) usuario = mainVM.usuarioMostrar.value!!

    val cuadrillas = mainVM.getCuadrillasUsuario(usuario).collectAsState(initial = emptyList())

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
        ) {
            TopProfile(
                mainVM = mainVM,
                username = usuario.username,
                email = usuario.email,
                edad = mainVM.calcularEdad(usuario),
                yo)
        }
        SeguidoresYSeguidos(yo, usuario, mainVM, navController)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            ListadoCuadrillas(
                cuadrillas = cuadrillas.value,
                yo,
                navController = navController,
                mainVM = mainVM
            )
        }
        if (yo) {
            BotonesPerfil(
                navController= navController,
                mainNavController = mainNavController,
                username = usuario.nombre)
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
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(cuadrillas) {
                CuadrillaCard(
                    cuadrilla = it,
                    mainVM = mainVM,
                    navController = navController,
                    isRemoveAvailable = yo
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
fun SeguidoresYSeguidos(yo: Boolean, usuario: Usuario, mainVM: MainVM, navController: NavController){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        val seguidores = mainVM.listaSeguidores(usuario).collectAsState(initial = emptyList())
        val seguidos = mainVM.listaSeguidos(usuario).collectAsState(initial = emptyList())
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 16.dp, end = 16.dp)
        ){
            Text(text = "Seguidores")
            TextButton(
                onClick = { navController.navigate(AppScreens.SeguidoresSeguidosList.route + "/0") },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
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
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ){
            Text(text = "Seguidos")
            TextButton(
                onClick = { navController.navigate(AppScreens.SeguidoresSeguidosList.route + "/1") },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
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
        if(!yo){
            Spacer(modifier = Modifier.weight(1f))
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .align(Alignment.Bottom)
            ){
                TextButton(
                    onClick = {
                            mainVM.newSiguiendo(usuario.username)
                    },
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Text(
                        text = "Follow",
                        style = TextStyle(
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
                contentDescription = "Settings")
        }
        IconButton(
            onClick = { /*TODO*/ },
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
    username: String,
    email: String,
    edad: Int,
    yo: Boolean
){
    val context = LocalContext.current
    var imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse("http://34.16.74.167/userProfileImages/$username.png"))
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        Log.d("IMAGEN", "0")
        if (uri!=null) {
            imageUri = uri
            mainVM.updateUserImage(context, username, uri)
        }
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

