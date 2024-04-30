package com.gomu.festup.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.compose.FestUpTheme
import com.gomu.festup.R


@Composable
fun Feed(
    navController: NavController
    ) {

    val eventos = listOf(
        Evento("Fiestas de Algorta", "25 de Abril", "Algorta, Bizkaia, España"),
        Evento("Fiestas de Getxo", "30 de Abril", "Algorta, Bizkaia, España"),
    )

    val seguidos = listOf(
        Evento("Fiestas de Barakaldo", "28 de Abril", "Algorta, Bizkaia, España"),
        Evento("Fiestas de Bilbao", "3 de Mayo", "Algorta, Bizkaia, España"),
    )
    val onItemClick: (Evento) -> Unit = { evento ->
        Log.d("Clickado", evento.titulo)
    }

    // Tab seleccionado al principio
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column (
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            //.verticalScroll(rememberScrollState())
                ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    )
    {
        TabRow(
            selectedTabIndex

        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Eventos",
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Seguidos",
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
        }

        when (selectedTabIndex) {
            0 -> {
                EventosList(eventos, onItemClick)
            }
            1 -> {
                EventosList(seguidos, onItemClick)
            }
        }
    }

}

@Composable
fun EventosList(eventos: List<Evento>, onItemClick: (Evento) -> Unit) {
    LazyColumn {
        items(eventos) { evento ->
            EventoItem(evento = evento, onItemClick = { onItemClick(evento) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoItem(evento: Evento, onItemClick: () -> Unit) {
    Card(
        onClick = onItemClick,
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(15.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = evento.titulo, style = MaterialTheme.typography.titleLarge)
                Text(text = evento.fecha, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(text = evento.ubicacion, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

data class Evento(val titulo: String, val fecha: String, val ubicacion: String)

@Preview(showBackground = true)
@Composable
fun FeedScreen() {
    FestUpTheme {
        Feed(navController = rememberNavController())
    }


}