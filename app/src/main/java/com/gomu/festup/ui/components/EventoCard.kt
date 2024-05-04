package com.gomu.festup.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.utils.toStringNuestro
import com.gomu.festup.vm.MainVM

@Composable
fun EventoCard(
    evento: Evento,
    mainVM: MainVM,
    navController: NavController
) {

    val onCardClick: (Evento) -> Unit = { eventoClicked ->
        mainVM.eventoMostrar.value = eventoClicked
        navController.navigate(AppScreens.Evento.route)
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
                // TODO aquí irá evento.image
                model = "http://34.16.74.167/eventoImages/no-image.png",
                contentDescription = "cuadrillaImage",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.size(50.dp)
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