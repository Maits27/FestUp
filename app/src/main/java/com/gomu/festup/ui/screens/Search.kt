package com.gomu.festup.ui.screens

import android.util.Log
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
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.vm.MainVM
import java.util.Date


@Composable
fun Search(
    navController: NavController,
    mainVM: MainVM
) {
    var searchText by remember { mutableStateOf("") }


    val personas = listOf(
        Usuario("@nagoregomez", "12345","nagore@gamil.com","Nagore Gomez", Date(), ""),
        Usuario("@maitane", "12345","nagore@gamil.com","Maitane Gomez", Date(), ""),
    )
    val cuadrilla = listOf(
        Cuadrilla("Pikito","Hola","Bilbao", ""),
        Cuadrilla("Pikito2","Hola","Getxo", ""),
    )
    val eventos = listOf(
        Evento("11","Fiestas de Algorta", Date(1000), 2, "Hola","Algorta, Bizkaia, ESpaña", "") ,
        Evento("11","Fiestas de Getxo", Date(2000), 2, "Hola","Algorta, Bizkaia, ESpaña", ""),
    )

    // Tab seleccionado al principio
    var selectedTabIndex by remember { mutableStateOf(0) }

    val filteredPersonas = personas.filter {
        it.username.contains(searchText, ignoreCase = true) || it.nombre.contains(
            searchText,
            ignoreCase = true
        )
    }

    val filteredCuadrillas = cuadrilla.filter {
        it.nombre.contains(searchText, ignoreCase = true) || it.lugar.contains(
            searchText,
            ignoreCase = true
        )
    }

    // TODO fecha rara
    val filteredEventos = eventos.filter {
        it.nombre.contains(searchText, ignoreCase = true) || it.fecha.toString().contains(
            searchText,
            ignoreCase = true
        )
    }


    Column (
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
        //.verticalScroll(rememberScrollState())
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {


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
                ElementoList(filteredPersonas, navController, mainVM)
            }
            1 -> {
                ElementoList(filteredCuadrillas, navController, mainVM)
            }
            2 -> {
                ElementoList(filteredEventos, navController, mainVM)
            }
        }
    }
}



@Composable
fun <T> ElementoList(
    elementos: List<T>,
    navController: NavController,
    mainVM: MainVM
) {
    LazyColumn {
        items(elementos) { elemento ->
            ElementoItem(elemento = elemento, navController, mainVM)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ElementoItem(
    elemento: T,
    navController: NavController,
    mainVM: MainVM
) {
    Card(
        onClick =  {
            if (elemento is Usuario){
                mainVM.usuarioMostrar.value=elemento
                navController.navigate(AppScreens.PerfilUser.route)
            }
            else if (elemento is Cuadrilla){
                mainVM.cuadrillaMostrar.value=elemento
                navController.navigate(AppScreens.PerfilCuadrilla.route)
            }
            else if(elemento is Evento){
                mainVM.eventoMostrar.value=elemento
                navController.navigate(AppScreens.Evento.route)
            }
        },
        modifier = Modifier.padding(8.dp)
    ){
    Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(15.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.size(50.dp)
            )
            Column(
                modifier = Modifier.weight(1f).padding(start = 10.dp)
            ) {
                if (elemento is Usuario){
                    Text(text = elemento.username, style = MaterialTheme.typography.titleLarge)
                    Text(text = elemento.nombre, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                else if (elemento is Cuadrilla){
                    Text(text = elemento.nombre, style = MaterialTheme.typography.titleLarge)
                    Text(text = elemento.lugar, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                else if(elemento is Evento){
                    Text(text = elemento.nombre, style = MaterialTheme.typography.titleLarge)
                    Text(text = elemento.fecha.toString(), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }


            }

        }
    }
}


