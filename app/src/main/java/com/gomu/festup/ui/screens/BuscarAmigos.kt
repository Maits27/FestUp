package com.gomu.festup.ui.screens

import android.content.ContentResolver
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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

    val context = LocalContext.current
    mainVM.listaAmigos(context)

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