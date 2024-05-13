package com.gomu.festup.ui.components.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.R
import com.gomu.festup.utils.toStringNuestro
import com.gomu.festup.vm.MainVM

@Composable
fun EventoCard(
    evento: Evento,
    mainVM: MainVM,
    navController: NavController
) {
    val context = LocalContext.current


    val onCardClick: (Evento) -> Unit = { eventoClicked ->
        mainVM.eventoMostrar.value = eventoClicked
        navController.navigate(AppScreens.Evento.route)
    }

    val imageUri by remember {
        mutableStateOf("http://34.16.74.167/eventoImages/${evento.id}.png")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCardClick(evento) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(15.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUri)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)  // Para que la guarde en caché-RAM
                    .diskCachePolicy(CachePolicy.ENABLED)    // Para que la guarde en caché-disco
                    .build(),
                contentDescription = stringResource(id = R.string.cuadrilla_imagen),
                placeholder = painterResource(id = R.drawable.no_image),
                error = painterResource(id = R.drawable.no_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            ) {
                Text(text = evento.nombre, style = MaterialTheme.typography.titleLarge)
                Text(text = evento.fecha.toStringNuestro(), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(text = evento.localizacion, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}