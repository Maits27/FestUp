package com.gomu.festup.ui.elements.components.cards

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
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
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.elements.components.Imagen
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.utils.getScheduleTime

@SuppressLint("ResourceType")
@Composable
fun UsuarioCard(
    usuario: Usuario,
    mainVM: MainVM,
    navController: NavController
) {

    val onCardClick: (Usuario) -> Unit = {
        mainVM.usuarioMostrar.add(usuario)
        if (mainVM.currentUser.value == mainVM.usuarioMostrar.last()){
            navController.navigate(AppScreens.PerfilYo.route)
        }
        else{
            navController.navigate(AppScreens.PerfilUser.route)
        }
    }

    val imageUri = Uri.parse("http://34.16.74.167/userProfileImages/${usuario.username}.png")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onCardClick(usuario) }
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Imagen(
                imageUri,
                R.drawable.no_user, 50.dp
            ) { onCardClick(usuario) }

            Column(
                modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)) {
                Text(
                    text = "@${usuario.username}",
                    style = MaterialTheme.typography.titleLarge,

                )
                Text(
                    text = usuario.nombre,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun UsuarioCardParaEventosAlert(
    usuario: Usuario,
    apuntado: Boolean,
    mainVM: MainVM
) {
    val context = LocalContext.current

    val imageUri = "http://34.16.74.167/userProfileImages/${usuario.username}.png"

    var checkedSwitch by remember { mutableStateOf(apuntado) }

    val scheduler = AndroidAlarmScheduler(context)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUri)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.DISABLED)  // Para que no la guarde en caché-RAM
                    .diskCachePolicy(CachePolicy.DISABLED)    // Para que no la guarde en caché-disco
                    .build(),
                contentDescription = stringResource(id = R.string.user_image),
                placeholder = painterResource(id = R.drawable.no_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                error = painterResource(id = R.drawable.no_user)
            )
            Column (
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = "@${usuario.username}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = usuario.nombre,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                modifier = Modifier.scale(0.7f),
                checked = checkedSwitch,
                onCheckedChange = {
                    checkedSwitch = !checkedSwitch
                    val eventoMostrar = mainVM.eventoMostrar.value!!

                    if (checkedSwitch){
                        scheduler.schedule(
                            AlarmItem(
                                getScheduleTime(eventoMostrar),
                                eventoMostrar.nombre,
                                eventoMostrar.localizacion,
                                eventoMostrar.id
                            )
                        )
                        mainVM.apuntarse(usuario, mainVM.eventoMostrar.value!!)
                    }
                    else {
                        scheduler.cancel(
                            AlarmItem(
                                getScheduleTime(eventoMostrar),
                                eventoMostrar.nombre,
                                eventoMostrar.localizacion,
                                eventoMostrar.id
                            )
                        )
                        mainVM.desapuntarse(usuario, mainVM.eventoMostrar.value!!)
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