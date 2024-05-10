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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.vm.MainVM


@Composable
fun CuadrillaMiniCard(
    cuadrilla: Cuadrilla,
    mainVM: MainVM,
    navController: NavController
) {

    val onCardClick: () -> Unit = {
        mainVM.cuadrillaMostrar.value = cuadrilla
        navController.navigate(AppScreens.PerfilCuadrilla.route)
    }

    var imageUri by remember {
        mutableStateOf("http://34.16.74.167/cuadrillaProfileImages/${cuadrilla.nombre}.png")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clickable { onCardClick() }
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = stringResource(id = R.string.cuadrilla_imagen),
            error = painterResource(id = R.drawable.no_cuadrilla),
            modifier = Modifier
                .border(2.dp, color = MaterialTheme.colorScheme.primary, CircleShape)
                .size(50.dp)
                .clip(CircleShape)
        )
        Text(text = cuadrilla.nombre, fontSize = 10.sp)
    }
}
