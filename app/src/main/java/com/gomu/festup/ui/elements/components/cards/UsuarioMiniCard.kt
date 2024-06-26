package com.gomu.festup.ui.elements.components.cards

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomu.festup.R
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.elements.components.ImagenMiniConBorde
import com.gomu.festup.ui.vm.MainVM

// Tarjeta de usuario para las LazyRows (filas con iconos pequeños y el nombre debajo)
@Composable
fun UsuarioMiniCard(
    mainVM: MainVM,
    navController: NavController,
    usuario: Usuario
) {
    val onCardClick: () -> Unit = {
        mainVM.usuarioMostrar.add(usuario)
        if (mainVM.currentUser.value == mainVM.usuarioMostrar.last()){
            navController.navigate(AppScreens.PerfilYo.route)
        }
        else{
            navController.navigate(AppScreens.PerfilUser.route)
        }
    }

    val imageUri = Uri.parse("http://34.71.128.243/userProfileImages/${usuario.username}.png")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clickable { onCardClick() }
    ) {
        ImagenMiniConBorde(imageUri, R.drawable.no_user)
        Text(text = usuario.username, fontSize = 10.sp)
    }
}