package com.gomu.festup.ui.screens

import android.content.ContentResolver
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
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

    if (amigos.isNotEmpty()) {
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
    else {
        Text(
            text = stringResource(R.string.no_hay_amigos),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp),
        )
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