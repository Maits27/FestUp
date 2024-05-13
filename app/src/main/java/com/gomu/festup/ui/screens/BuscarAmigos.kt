package com.gomu.festup.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.ui.components.cards.UsuarioCard
import com.gomu.festup.vm.MainVM
import java.time.Instant
import java.util.Date

@Composable
fun BuscarAmigos(
    mainVM: MainVM,
    navController: NavController
) {
    LazyColumn {
        item {
            UsuarioCard(
                usuario = Usuario(username = "pepe", "a@a", "Pepe", Date.from(Instant.now())),
                mainVM = mainVM,
                navController = navController
            )
        }
    }
}