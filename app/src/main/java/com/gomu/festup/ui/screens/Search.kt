package com.gomu.festup.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.ui.components.cards.CuadrillaCard
import com.gomu.festup.ui.components.cards.EventoCard
import com.gomu.festup.ui.components.cards.UsuarioCard
import com.gomu.festup.vm.MainVM


@Composable
fun Search(
    navController: NavController,
    mainVM: MainVM
) {
    var searchText by remember { mutableStateOf("") }


    var usuario = mainVM.currentUser.value!!
    val usuarios = mainVM.getUsuariosMenosCurrent(usuario).collectAsState(initial = emptyList())
    val cuadrillas = mainVM.getCuadrillas().collectAsState(initial = emptyList())
    val eventos = mainVM.getEventos().collectAsState(initial = emptyList())

    // Tab seleccionado al principio
    var selectedTabIndex by remember { mutableStateOf(0) }

    val filteredPersonas = usuarios.value.filter {
        it.username.contains(searchText, ignoreCase = true) || it.nombre.contains(
            searchText,
            ignoreCase = true
        )
    }

    val filteredCuadrillas = cuadrillas.value.filter {
        it.nombre.contains(searchText, ignoreCase = true) || it.lugar.contains(
            searchText,
            ignoreCase = true
        )
    }

    // TODO fecha rara
    val filteredEventos = eventos.value.filter {
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
    if (elemento is Usuario) {
        UsuarioCard(usuario = elemento, mainVM = mainVM, navController = navController)
    }
    else if (elemento is Cuadrilla) {
        CuadrillaCard(
            cuadrilla = elemento,
            mainVM = mainVM, navController = navController,
            isRemoveAvailable = false
        )
    }
    else if (elemento is Evento) {
        EventoCard(evento = elemento, mainVM = mainVM, navController = navController)
    }
}


