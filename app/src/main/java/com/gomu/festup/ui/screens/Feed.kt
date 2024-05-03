package com.gomu.festup.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.ui.components.EventoCard
import com.gomu.festup.vm.MainVM
import java.util.Date


@Composable
fun Feed(
    navController: NavController,
    mainVM: MainVM
    ) {

    val eventos = mainVM.eventosUsuario(mainVM.currentUser.value!!).collectAsState(initial = emptyList())
    val seguidos = mainVM.eventosSeguidos(mainVM.currentUser.value!!).collectAsState(initial = emptyList())
//    val eventos = listOf(
//        Evento(
//            id = "adbhay4",
//            nombre = "Fiestas de Algorta",
//            fecha = Date(),
//            numeroAsistentes =  4,
//            descripcion = "Las mejores fiestas de Bilbao",
//            localizacion = "Algorta, Bizkaia, España",
//            eventoImagePath = ""
//        ),
//        Evento(
//            id = "asdfjio4",
//            nombre = "Fiestas de Getxo",
//            fecha = Date(),
//            numeroAsistentes =  4,
//            descripcion = "Las mejores fiestas de Bilbao",
//            localizacion = "Algorta, Bizkaia, España",
//            eventoImagePath = ""
//        ),
//    )

//    val seguidos = listOf(
//        Evento(
//            id = "123423",
//            nombre = "Fiestas de Barakaldo",
//            fecha = Date(),
//            numeroAsistentes =  4,
//            descripcion = "Las mejores fiestas de Bilbao",
//            localizacion = "Algorta, Bizkaia, España",
//            eventoImagePath = ""
//        ),
//        Evento(
//            id = "12234234",
//            nombre = "Fiestas de Bilbao",
//            fecha = Date(),
//            numeroAsistentes = 4,
//            descripcion = "3 de Mayo",
//            localizacion = "Algorta, Bizkaia, España",
//            eventoImagePath = ""
//        ),
//    )

    // Tab seleccionado al principio
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column (
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
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
                EventosList(eventos.value, mainVM, navController)
            }
            1 -> {
                EventosList(seguidos.value, mainVM, navController)
            }
        }
    }

}

@Composable
fun EventosList(eventos: List<Evento>, mainVM: MainVM, navController: NavController) {
    LazyColumn {
        items(eventos) { evento ->
            EventoCard(evento = evento, mainVM, navController)
        }
    }
}

