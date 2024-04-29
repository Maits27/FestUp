package com.gomu.festup.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.compose.FestUpTheme
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens



@Composable
fun Search(
    navController: NavController
) {
    var searchText by remember { mutableStateOf("") }

    val personas = listOf(
        Elemento("@nagoregomez", "Nagore Gomez"),
        Elemento("@maitaneurruela", "Maitane Urruela"),
    )
    val cuadrilla = listOf(
        Elemento("Pikito", "2"),
        Elemento("Pikito2", "5"),
    )
    val eventos = listOf(
        Elemento("Fiestas de Algorta", "25 de Abril"),
        Elemento("Fiestas de Getxo", "30 de Abril"),
    )

    val onItemClick: (Elemento) -> Unit = { elemento ->
        Log.d("Clickado", elemento.titulo)
    }

    // Tab seleccionado al principio
    var selectedTabIndex by remember { mutableStateOf(0) }

    val filteredPersonas = personas.filter {
        it.titulo.contains(searchText, ignoreCase = true) || it.subtitulo.contains(
            searchText,
            ignoreCase = true
        )
    }

    val filteredCuadrilla = cuadrilla.filter {
        it.titulo.contains(searchText, ignoreCase = true) || it.subtitulo.contains(
            searchText,
            ignoreCase = true
        )
    }

    val filteredEventos = eventos.filter {
        it.titulo.contains(searchText, ignoreCase = true) || it.subtitulo.contains(
            searchText,
            ignoreCase = true
        )
    }

    Column {


        // Tabs
        TabRow(
            selectedTabIndex
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Usuarios",
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Cuadrillas",
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Eventos",
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
        }
        // Barra de búsqueda
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            placeholder = { Text(text = "Buscar...") }
        )

        when (selectedTabIndex) {
            0 -> {
                ElementoList(filteredPersonas, onItemClick)
            }
            1 -> {
                ElementoList(filteredCuadrilla, onItemClick)
            }
            2 -> {
                ElementoList(filteredEventos, onItemClick)
            }
        }
    }
}


@Composable
fun ElementoList(eventos: List<Elemento>, onItemClick: (Elemento) -> Unit) {
    LazyColumn {
        items(eventos) { elemento ->
            EventoItem(elemento = elemento, onItemClick = { onItemClick(elemento) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoItem(elemento: Elemento, onItemClick: () -> Unit) {
    Card(
        onClick = onItemClick,
        modifier = Modifier.padding(16.dp)
    ){
    Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = elemento.titulo, style = MaterialTheme.typography.titleLarge)
                Text(text = elemento.subtitulo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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

data class Elemento(val titulo: String, val subtitulo: String)


@Preview(showBackground = true)
@Composable
fun SearchScreen() {
    FestUpTheme {
        Search(navController = rememberNavController())
    }


}