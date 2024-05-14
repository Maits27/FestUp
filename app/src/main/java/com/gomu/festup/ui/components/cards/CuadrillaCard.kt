package com.gomu.festup.ui.components.cards

import android.net.Uri
import android.util.Log
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.R
import com.gomu.festup.alarmMng.AlarmItem
import com.gomu.festup.alarmMng.AndroidAlarmScheduler
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.components.Imagen
import com.gomu.festup.utils.getScheduleTime
import com.gomu.festup.vm.MainVM
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun CuadrillaCard(
    cuadrilla: Cuadrilla,
    mainVM: MainVM,
    navController: NavController,
) {

    val onCardClick: (Cuadrilla) -> Unit = {
        mainVM.cuadrillaMostrar.value = cuadrilla
        navController.navigate(AppScreens.PerfilCuadrilla.route)
    }

    val imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse("http://34.16.74.167/cuadrillaProfileImages/${cuadrilla.nombre}.png"))
    }
    val context = LocalContext.current

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
            Imagen(imageUri, context, R.drawable.no_cuadrilla, 50.dp) {}
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

@Composable
fun CuadrillaCardParaEventosAlert(
    cuadrilla: Cuadrilla,
    apuntado: Boolean,
    mainVM: MainVM,
    recibirNotificaciones: Boolean
) {
    val context = LocalContext.current

    val imageUri="http://34.16.74.167/cuadrillaProfileImages/${cuadrilla.nombre}.png"

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
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUri)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.DISABLED)  // Para que no la guarde en caché-RAM
                    .diskCachePolicy(CachePolicy.DISABLED)    // Para que no la guarde en caché-disco
                    .build(),
                contentDescription = stringResource(id = R.string.cuadrilla_imagen),
                placeholder = painterResource(id = R.drawable.no_cuadrilla),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                error = painterResource(id = R.drawable.no_cuadrilla)
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
            Switch(
                modifier = Modifier.scale(0.7f),
                checked = checkedSwitch,
                onCheckedChange = {
                    checkedSwitch = !checkedSwitch

                    if (checkedSwitch){
                        Log.d("RECNOTIF", recibirNotificaciones.toString())
                        if (recibirNotificaciones) {
                            scheduler.schedule(
                                AlarmItem(
                                    getScheduleTime(mainVM),
                                    mainVM.eventoMostrar.value!!.nombre,
                                    mainVM.eventoMostrar.value!!.localizacion,
                                    mainVM.eventoMostrar.value!!.id
                                )
                            )
                        }
                        mainVM.apuntarse(cuadrilla, mainVM.eventoMostrar.value!!)
                    }
                    else{
                        Log.d("RECNOTIF", recibirNotificaciones.toString())
                        if (recibirNotificaciones){
                            scheduler.cancel(
                                AlarmItem(
                                getScheduleTime(mainVM),
                                mainVM.eventoMostrar.value!!.nombre,
                                mainVM.eventoMostrar.value!!.localizacion,
                                mainVM.eventoMostrar.value!!.id
                                )
                            )
                        }
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
                } else {
                    null
                }
            )
        }
    }
}