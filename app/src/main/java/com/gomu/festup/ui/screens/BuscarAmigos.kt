package com.gomu.festup.ui.screens

import android.content.ContentResolver
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.ui.components.cards.UsuarioCard
import com.gomu.festup.vm.MainVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

@Composable
fun BuscarAmigos(
    mainVM: MainVM,
    navController: NavController
) {
    val context = LocalContext.current

    val contactos = mainVM.listaContactos(context)
    val usuarios by mainVM.getUsuariosMenosCurrent(mainVM.currentUser.value!!).collectAsState(initial = emptyList())

    val amigos by remember {
         derivedStateOf {
            val amigosList = mutableListOf<Usuario>()

            usuarios.map { usuario ->
                val estaEnContactos = contactos.any { formatPhone(it.telefono) == usuario.telefono }
                if (estaEnContactos) {
                    amigosList.add(usuario)
                }
            }
            amigosList.toList() // Devolver la lista de amigos
        }
    }

    LazyColumn {
        items(amigos) {amigo ->
            UsuarioCard(
                usuario = amigo,
                mainVM = mainVM,
                navController = navController
            )
        }
    }
}

fun formatPhone(number: String): String {

    return if (number.startsWith("+34")) {
        number.substring(4).replace(" ", "")
    }
    else{
        number.replace(" ", "")
    }


}