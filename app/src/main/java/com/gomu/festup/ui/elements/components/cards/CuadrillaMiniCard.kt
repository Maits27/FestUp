package com.gomu.festup.ui.elements.components.cards

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomu.festup.R
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.elements.components.ImagenMiniConBorde
import com.gomu.festup.ui.vm.MainVM


@Composable
fun CuadrillaMiniCard(
    cuadrilla: Cuadrilla,
    mainVM: MainVM,
    navController: NavController
) {
    val imageUri = Uri.parse("http://34.71.128.243/cuadrillaProfileImages/${cuadrilla.nombre}.png")
    val onCardClick: () -> Unit = {
        mainVM.cuadrillaMostrar.value = cuadrilla
        navController.navigate(AppScreens.PerfilCuadrilla.route)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clickable { onCardClick() }
    ) {
        ImagenMiniConBorde(imageUri, R.drawable.no_image)
        Text(text = cuadrilla.nombre, fontSize = 10.sp)
    }
}

