package com.gomu.festup.ui.components.cards

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.components.ImagenEventoMiniConBorde
import com.gomu.festup.ui.components.ImagenMiniConBorde
import com.gomu.festup.utils.toStringNuestro
import com.gomu.festup.vm.MainVM


@Composable
fun EventoMiniCard(
    evento: Evento,
    mainVM: MainVM,
    navController: NavController
) {
    val context = LocalContext.current

    val onCardClick: () -> Unit = {
        mainVM.eventoMostrar.value = evento
        navController.navigate(AppScreens.Evento.route)
    }

    val imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse("http://34.16.74.167/eventoImages/${evento.id}.png"))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clickable { onCardClick() }
    ) {
        ImagenEventoMiniConBorde(imageUri, context, R.drawable.no_image)
        Text(text = evento.nombre, fontSize = 10.sp)
        Text(text = evento.fecha.toStringNuestro(), fontSize = 8.sp)


    }
}