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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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


@Composable
fun PerfilCuadrilla(
    navController: NavController,
    nombre: String="Pikito"
) {
    var formo_parte by rememberSaveable {mutableStateOf(true)}
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onTertiary),
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
                .weight(2f)
        ){
            TopProfileCuadrilla(
                nombre = nombre,
                lugar = "Bilbao",
                integrantes = 4,
                picture = null,
                formo_parte)
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ){
            Text(
                text = "Descripción de cuadrilla:",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "Suuuuuuuuuupeerrrrr descripción",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 15.sp
                )
            )
        }
        Divider(color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.width(200.dp))
        ListadoUsuarios(
            modifier = Modifier.weight(3f),
            nombre = nombre,
            usuarios = listOf(
                Usuario(username = "AingeruBeOr", nombre = "Aingeru", email = "1405bellido", password = "1"),
                Usuario(username = "Sergiom8", nombre = "Sergio", email = "sergiom8", password = "1"),
                Usuario(username = "NagoreGomez", nombre = "Nagore", email = "nagoregomez4", password = "1"),
            ),
            formo_parte
        )
        if (formo_parte){
            var verificacion by rememberSaveable{ mutableStateOf(false) }
            Row (
                modifier = Modifier.weight(1f),
            ){

                TextButton(
                    onClick = { verificacion = true },
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.onTertiary,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(6.dp)
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "Eliminar $nombre",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                EstasSeguro(
                    show = verificacion,
                    mensaje = "Si confirmas se eliminará la cuadrilla para todos los usuarios.",
                    onDismiss = { verificacion = false }) {
                    verificacion = false
                    // TODO eliminar
                }
            }

        }

    }
}

@Preview
@Composable
fun PerfilPreview(){
    PerfilCuadrilla(navController = rememberNavController())
}

@Composable
fun TopProfileCuadrilla(
    nombre: String,
    lugar: String,
    integrantes: Int,
    picture: Bitmap?,
    pertenezco: Boolean
){

    if (picture == null) {
        Icon(
            painter = painterResource(id = R.drawable.account),
            contentDescription = "",
            modifier = Modifier
                .size(100.dp)
        )
    } else {
        Image(
            bitmap = picture.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
        )
    }

    Text(
        text = nombre,
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
                text = "Lugar: $lugar",
                modifier = Modifier.padding(5.dp),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontSize = 15.sp
                )
            )
            Text(
                text = "Integrantes: $integrantes",
                modifier = Modifier.padding(5.dp),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontSize = 15.sp
                )
            )
        }
        if(!pertenezco){
            var show by rememberSaveable { mutableStateOf(false) }
            Column (
                Modifier
                    .weight(1f)
                    .padding(vertical = 0.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                TextButton(
                    onClick = { show = true },
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.onTertiary, shape = RoundedCornerShape(10.dp))
                ) {
                    Text(text = "Unirme", color = MaterialTheme.colorScheme.tertiary)
                }
            }
            Unirse(show){
                //TODO unirse
            }
        }
    }
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
    modifier: Modifier = Modifier,
    nombre: String,
    usuarios: List<Usuario>,
    pertenezco: Boolean
){
    Column (
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ){
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ){
            Text(
                text = "Integrantes de $nombre:",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(3f)
            )
            if (pertenezco){
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
                        painter = painterResource(id = R.drawable.send), "",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
        when{
            usuarios.isNotEmpty() ->
            {
                LazyColumn(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.padding(6.dp),
                    contentPadding = PaddingValues(bottom = 30.dp)
                ) {
                    items(usuarios) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(3.dp).clickable {  },
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
                                    Modifier
                                        .padding(16.dp)
                                        .weight(3f)
                                ) {
                                    Text(text = it.username, fontWeight = FontWeight.Bold)
                                    Text(text = it.nombre)
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

}