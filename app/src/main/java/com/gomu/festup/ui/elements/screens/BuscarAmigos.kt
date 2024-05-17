package com.gomu.festup.ui.elements.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomu.festup.R
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.ui.elements.components.cards.UsuarioCard
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.utils.formatPhone


/**
 * Pantalla con los contactos del teléfono que se pueden encontrar en la aplicación.
 */
@Composable
fun BuscarAmigos(
    mainVM: MainVM,
    navController: NavController
) {
    val context = LocalContext.current

    val contactos = mainVM.listaContactos(context)
    val usuarios by mainVM.getUsuariosMenosCurrent(mainVM.currentUser.value!!).collectAsState(initial = emptyList())
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

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
        LazyColumn(
            modifier = Modifier.padding(horizontal = if (isVertical) 0.dp else 60.dp)
        ) {
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

