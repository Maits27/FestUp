package com.gomu.festup.ui.elements.components.cards

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gomu.festup.R
import com.gomu.festup.data.UserCuadrillaAndEvent
import com.gomu.festup.data.localDatabase.Entities.Evento
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.elements.components.ImagenEvento
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.utils.toStringNuestro

@Composable
fun EventoCard(
    evento: Evento,
    mainVM: MainVM,
    navController: NavController
) {
    val context = LocalContext.current

    val onCardClick: (Evento) -> Unit = { eventoClicked ->
        mainVM.eventoMostrar.value = eventoClicked
        navController.navigate(AppScreens.Evento.route)
    }

    val imageUri = Uri.parse("http://34.71.128.243/eventoImages/${evento.id}.png")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onCardClick(evento) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            ImagenEvento(imageUri, R.drawable.no_image, 50.dp) {}
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            ) {
                Text(text = evento.nombre, style = MaterialTheme.typography.titleLarge)
                Text(text = evento.fecha.toStringNuestro(), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(text = evento.localizacion, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}



@Composable
fun EventoCardConUser(
    eventoUser: UserCuadrillaAndEvent,
    mainVM: MainVM,
    navController: NavController
) {
    val evento = eventoUser.evento
    val cuadrilla = eventoUser.nombreCuadrilla
    val usuario = eventoUser.username

    val onCardClick: (Evento) -> Unit = { eventoClicked ->
        mainVM.eventoMostrar.value = eventoClicked
        navController.navigate(AppScreens.Evento.route)
    }

    val imageUri = Uri.parse("http://34.71.128.243/eventoImages/${evento.id}.png")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onCardClick(evento) }
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ){
            if(usuario != mainVM.currentUser.value!!.username){
                if(cuadrilla==""){
                    Box(
                        Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(2.dp)
                            )
                            .fillMaxWidth()
                    ){
                        Text(text = stringResource(id = R.string.user_participa, usuario),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 13.dp))
                    }
                }else{
                    Box(
                        Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .fillMaxWidth()
                    ){
                        Text(text = stringResource(id = R.string.cuadrilla_participa, cuadrilla),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 13.dp))
                    }
                }

            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(13.dp)
            ) {
                ImagenEvento(imageUri, R.drawable.no_image, 50.dp) {}
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                ) {
                    Text(text = evento.nombre, style = MaterialTheme.typography.titleLarge)
                    Text(text = evento.fecha.toStringNuestro(), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(text = evento.localizacion, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}