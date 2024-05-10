package com.gomu.festup.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.ui.components.cards.UsuarioCard
import com.gomu.festup.vm.MainVM

@Composable
fun SeguidoresSeguidosList(
    startPage: Int?,
    mainVM: MainVM,
    navController: NavController
) {
    var selectedTab by remember {
        mutableIntStateOf(startPage!!)
    }

    val usuario = mainVM.usuarioMostrar.value!!

    val seguidores = mainVM.listaSeguidores(usuario).collectAsState(initial = emptyList())
    val seguidos = mainVM.listaSeguidos(usuario).collectAsState(initial = emptyList())

    Column {
        TabRow(
            selectedTabIndex = selectedTab
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
            ) {
                Text(text = stringResource(id = R.string.seguidores), modifier = Modifier.padding(vertical = 15.dp))
            }
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
            ) {
                Text(text =  stringResource(id = R.string.seguidos), modifier = Modifier.padding(vertical = 15.dp))
            }
        }
        when (selectedTab) {
            0 -> {
                SeguidoresOrSeguidos(usuarios = seguidores, mainVM = mainVM, navController = navController)
            }
            1 -> {
                SeguidoresOrSeguidos(usuarios = seguidos, mainVM = mainVM, navController = navController)
            }
        }
    }
}

@Composable
fun SeguidoresOrSeguidos(usuarios: State<List<Usuario>>, mainVM: MainVM, navController: NavController) {
    LazyColumn {
        items(usuarios.value) { usuario ->
            UsuarioCard(usuario = usuario, mainVM = mainVM, navController = navController)
        }
    }
}
