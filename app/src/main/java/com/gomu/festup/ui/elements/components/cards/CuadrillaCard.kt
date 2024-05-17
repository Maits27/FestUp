package com.gomu.festup.ui.elements.components.cards

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.gomu.festup.R
import com.gomu.festup.alarmMng.AndroidAlarmScheduler
import com.gomu.festup.data.AlarmItem
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.elements.components.Imagen
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.utils.getScheduleTime

@Composable
fun CuadrillaCard(
    cuadrilla: Cuadrilla,
    mainVM: MainVM,
    navController: NavController,
) {

    // Acciones a realizar al hacer click en la tarjeta
    val onCardClick: (Cuadrilla) -> Unit = {
        mainVM.cuadrillaMostrar.value = cuadrilla
        navController.navigate(AppScreens.PerfilCuadrilla.route)
    }

    // Uri de la imagen a mostrar en la tarjeta (foto de cuadrilla)
    val imageUri = Uri.parse("http://34.71.128.243/cuadrillaProfileImages/${cuadrilla.nombre}.png")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onCardClick(cuadrilla) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Imagen(imageUri, R.drawable.no_cuadrilla, 50.dp) {  }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            ) {
                Text(
                    text = cuadrilla.nombre,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = cuadrilla.lugar,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

            }
        }
    }
}


// Tarjeta para mostrar en el dialogo de la apuntarse y desapuntarse de las cuadrillas a las que el usuario pertenece
@Composable
fun CuadrillaCardParaEventosAlert(
    cuadrilla: Cuadrilla,
    apuntado: Boolean,
    mainVM: MainVM
) {
    val context = LocalContext.current

    val imageUri = "http://34.71.128.243/cuadrillaProfileImages/${cuadrilla.nombre}.png"

    var checkedSwitch by remember { mutableStateOf(apuntado) }

    val scheduler = AndroidAlarmScheduler(context)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp)
        ) {

            // Imagen de la cuadrilla
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUri)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.DISABLED)  // Para que no la guarde en caché-RAM
                    .diskCachePolicy(CachePolicy.DISABLED)    // Para que no la guarde en caché-disco
                    .build(),
                contentDescription = stringResource(id = R.string.cuadrilla_imagen),
                placeholder = painterResource(id = R.drawable.no_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                error = painterResource(id = R.drawable.no_image)
            )
            Column(
                Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = cuadrilla.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = cuadrilla.lugar,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            // Switch para apuntarse o desapuntarse
            Switch(
                modifier = Modifier.scale(0.7f),
                checked = checkedSwitch,
                onCheckedChange = {
                    checkedSwitch = !checkedSwitch

                    if (checkedSwitch){
                        // Si se apunta programar una alarma para el evento y agregar a la cuadrilla a la lista de apuntados
                        scheduler.schedule(
                            AlarmItem(
                                getScheduleTime(mainVM.eventoMostrar.value!!),
                                mainVM.eventoMostrar.value!!.nombre,
                                mainVM.eventoMostrar.value!!.localizacion,
                                mainVM.eventoMostrar.value!!.id
                            )
                        )
                        mainVM.apuntarse(cuadrilla, mainVM.eventoMostrar.value!!)
                    }
                    else{
                        // Si se desapunta cancelar la alarma del evento y eliminar a la cuadrilla de la lista de apuntados
                        scheduler.cancel(
                            AlarmItem(
                            getScheduleTime(mainVM.eventoMostrar.value!!),
                            mainVM.eventoMostrar.value!!.nombre,
                            mainVM.eventoMostrar.value!!.localizacion,
                            mainVM.eventoMostrar.value!!.id
                            )
                        )
                        mainVM.desapuntarse(cuadrilla, mainVM.eventoMostrar.value!!)
                    }
                },
                thumbContent = if (checkedSwitch) {
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.check),
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else null
            )
        }
    }
}