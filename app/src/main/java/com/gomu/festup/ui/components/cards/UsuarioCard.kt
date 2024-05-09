package com.gomu.festup.ui.components.cards

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.vm.MainVM

@Composable
fun UsuarioCard(
    usuario: Usuario,
    mainVM: MainVM,
    navController: NavController
) {

    val onCardClick: (Usuario) -> Unit = {
        mainVM.usuarioMostrar.value = usuario
        if (mainVM.currentUser.value== mainVM.usuarioMostrar.value){
            navController.navigate(AppScreens.PerfilYo.route)
        }
        else{
            navController.navigate(AppScreens.PerfilUser.route)
        }
    }

    var imageUri by remember {
        mutableStateOf("http://34.16.74.167/userProfileImages/${usuario.username}.png")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .clickable { onCardClick(usuario) }
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            AsyncImage(
                model = imageUri,
                contentDescription = "User image",
                onError = {
                    imageUri = "http://34.16.74.167/userProfileImages/no-user.png"
                },
                placeholder = painterResource(id = R.drawable.no_user),
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.size(15.dp))
            Column {
                Text(
                    text = "@${usuario.username}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = usuario.nombre,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun UsuarioCardParaEventosAlert(
    usuario: Usuario,
    apuntado: Boolean,
    mainVM: MainVM,
    onClick:()-> Unit
) {

    var imageUri by remember {
        mutableStateOf("http://34.16.74.167/userProfileImages/${usuario.username}.png")
    }

    var checkedSwitch by remember { mutableStateOf(apuntado) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            AsyncImage(
                model = imageUri,
                contentDescription = "User image",
                onError = {
                    imageUri = "http://34.16.74.167/cuadrillaProfileImages/no-user.png"
                },
                placeholder = painterResource(id = R.drawable.no_image),
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Column (
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = "@${usuario.username}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = usuario.nombre,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                modifier = Modifier.scale(0.7f),
                checked = checkedSwitch,
                onCheckedChange = {
                    checkedSwitch = !checkedSwitch
                    if (checkedSwitch){
                        mainVM.apuntarse(usuario, mainVM.eventoMostrar.value!!)
                    }
                    else{
                        mainVM.desapuntarse(usuario, mainVM.eventoMostrar.value!!)
                    }
                },
                thumbContent = if (checkedSwitch) {
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.check),
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                }
            )
        }
    }
}