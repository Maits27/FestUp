package com.gomu.festup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.R
import java.util.Date

@Composable
fun EventsList(navController: NavController) {

    val eventoNuevo = Evento(nombre = "Fiestas de Basauri", descripcion = " ", fecha = Date(123), localizacion = "Basauri", numeroAsistentes = 100)
    val eventoNuevo2 = Evento(nombre = "Fiestas de Basauri", descripcion = " ", fecha = Date(123), localizacion = "Basauri", numeroAsistentes = 100)
    val events = arrayOf(eventoNuevo, eventoNuevo2)

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(bottom = 70.dp)
    ) {
        items(events) { evento ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {

                    }
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = evento.nombre,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                        Text(
                            text = "Fecha: ${evento.fecha} ",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text(
                            text = "Ubicación}: ${evento.localizacion}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.party),
                        contentDescription = null,
                        modifier = Modifier
                            .size(55.dp)
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = { navController.popBackStack() },
            shape = RoundedCornerShape(70),
            modifier = Modifier
                .padding(16.dp)
                .size(45.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.map),
                contentDescription = null
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun EventListPreview() {
    EventsList(navController = rememberNavController())
}