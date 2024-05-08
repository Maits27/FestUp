package com.gomu.festup.ui.components.cards

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.vm.MainVM

@Composable
fun UsuarioMiniCard(
    mainVM: MainVM,
    navController: NavController,
    usuario: Usuario
) {
    val onCardClick: () -> Unit = {
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clickable { onCardClick() }
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "Usuario profile image",
            onError = {
                imageUri = "http://34.16.74.167/userProfileImages/no-user.png"
            },
            modifier = Modifier
                .border(2.dp, color = MaterialTheme.colorScheme.primary, CircleShape)
                .size(50.dp)
                .clip(CircleShape)
        )
        Text(text = usuario.username, fontSize = 10.sp)
    }
}