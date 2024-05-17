package com.gomu.festup.ui.elements.screens

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gomu.festup.MainActivity
import com.gomu.festup.R
import com.gomu.festup.alarmMng.AndroidAlarmScheduler
import com.gomu.festup.data.AppLanguage
import com.gomu.festup.ui.elements.components.SwitchDarkMode
import com.gomu.festup.ui.elements.components.SwitchTik
import com.gomu.festup.ui.elements.components.dialogs.EstasSeguroDialog
import com.gomu.festup.ui.elements.components.dialogs.LanguageSelection
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.ui.vm.PreferencesViewModel
import com.gomu.festup.utils.getScheduleTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Ajustes(
    preferencesVM: PreferencesViewModel,
    mainVM: MainVM,
    idioma: AppLanguage,
    dark: Boolean,
    receiveNotifications: Boolean,
//    mostrarEdad: Boolean
) {
//    val idioma by preferencesVM.idioma(mainVM.currentUser.value!!.username).collectAsState(initial = preferencesVM.currentSetLang)
//    val dark by preferencesVM.darkTheme(mainVM.currentUser.value!!.username).collectAsState(initial = true)
//    val receiveNotifications by preferencesVM.receiveNotifications(mainVM.currentUser.value!!.username).collectAsState(initial = true)
//    val showAge by preferencesVM.mostrarEdad(mainVM.currentUser.value!!.username).collectAsState(initial = false)
//    val showAgeOther by preferencesVM.mostrarEdad(if (mainVM.usuarioMostrar.isEmpty()) "" else mainVM.usuarioMostrar.last()?.username?:"").collectAsState(initial = false)

    Log.d("APP", "Idioma: $idioma")
    Log.d("APP", "Dark: $dark")
    Log.d("APP", "receiveNotifications: $receiveNotifications")

    var showIdiomas by remember { mutableStateOf(false) }
    var logoutVerification by remember { mutableStateOf(false) }
    var seguidos = mainVM.listaSeguidos(mainVM.currentUser.value!!).collectAsState(initial = emptyList())

    val scheduler = AndroidAlarmScheduler(LocalContext.current)
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    if (isVertical){
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ){
            Text(
                stringResource(id = R.string.visualizacion),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            //---------------- Idioma ----------------//
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { showIdiomas = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                Icon(
                    painter = painterResource(id = R.drawable.language),
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp))
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(stringResource(id = R.string.idioma))
                    Text(
                        text = if (idioma.code == "es") "Castellano" else "Euskera",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
            LanguageSelection(showIdiomas, idioma, preferencesVM) {showIdiomas = false}
            //Tema de colores
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                Icon(
                    painter = painterResource(id = R.drawable.color),
                    contentDescription = null,
                    modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.weight(3f)
                ) {
                    Text(stringResource(id = R.string.tema))
                    Text(
                        text = if (dark) stringResource(id = R.string.dark) else stringResource(id = R.string.light),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                SwitchDarkMode(preferencesVM, dark, Modifier.weight(2f))
            }
//            Row (
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Start
//            ){
//                Icon(
//                    painter = painterResource(id = R.drawable.visible),
//                    contentDescription = null,
//                    modifier = Modifier.weight(1f))
//                Column(
//                    horizontalAlignment = Alignment.Start,
//                    modifier = Modifier.weight(3f)
//                ) {
//                    Text(stringResource(id = R.string.edad))
//                    Text(
//                        text = if (mostrarEdad)  stringResource(id = R.string.mostrar) else stringResource(id = R.string.no_mostrar),
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//                SwitchTik(mostrarEdad, Modifier.weight(2f)){ preferencesVM.changeVisualizarEdad() }
//            }
            Divider(Modifier.padding(vertical = 10.dp))
            Text(
                stringResource(id = R.string.sistema),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                Icon(
                    painter = painterResource(id = R.drawable.notifications_active),
                    contentDescription = null,
                    modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.weight(3f)
                ) {
                    Text(stringResource(id = R.string.notificaciones))
                    Text(
                        text = if (receiveNotifications)  stringResource(id = R.string.recibir) else stringResource(id = R.string.no_recibir),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                SwitchTik(receiveNotifications, Modifier.weight(2f)){
                    preferencesVM.changeReceiveNotifications()
                    if (it){
                        mainVM.subscribeUser()
                        mainVM.suscribirASeguidos(seguidos.value)
                    }else{
                        mainVM.unSubscribeUser()
                        mainVM.unSuscribeASeguidos(seguidos.value)
                    }
                }
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { logoutVerification = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                Icon(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp))
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text("Logout")
                    Text(
                        text = stringResource(id = R.string.cerrar_sesion_de, mainVM.currentUser.value!!.username),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
        }
    }else{
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ){
            Column (
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ){

                Text(
                    stringResource(id = R.string.visualizacion),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                //---------------- Idioma ----------------//
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { showIdiomas = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.language),
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp))
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(stringResource(id = R.string.idioma))
                        Text(
                            text = if (idioma.code == "es") "Castellano" else "Euskera",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                }
                LanguageSelection(showIdiomas, idioma, preferencesVM) {showIdiomas = false}
                //Tema de colores
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.color),
                        contentDescription = null,
                        modifier = Modifier.weight(1f))
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(3f)
                    ) {
                        Text(stringResource(id = R.string.tema))
                        Text(
                            text = if (dark) stringResource(id = R.string.dark) else stringResource(id = R.string.light),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    SwitchDarkMode(preferencesVM, dark, Modifier.weight(2f))
                }
//                Row (
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Start
//                ){
//                    Icon(
//                        painter = painterResource(id = R.drawable.visible),
//                        contentDescription = null,
//                        modifier = Modifier.weight(1f))
//                    Column(
//                        horizontalAlignment = Alignment.Start,
//                        modifier = Modifier.weight(3f)
//                    ) {
//                        Text(stringResource(id = R.string.edad))
//                        Text(
//                            text = if (mostrarEdad)  stringResource(id = R.string.mostrar) else stringResource(id = R.string.no_mostrar),
//                            style = MaterialTheme.typography.bodySmall
//                        )
//                    }
//                    SwitchTik(mostrarEdad, Modifier.weight(2f)){ preferencesVM.changeVisualizarEdad() }
//                }
            }
            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .height(320.dp)
                    .padding(vertical = 10.dp)
            )

            Column (
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    stringResource(id = R.string.sistema),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.notifications_active),
                        contentDescription = null,
                        modifier = Modifier.weight(1f))
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(3f)
                    ) {
                        Text(stringResource(id = R.string.notificaciones))
                        Text(
                            text = if (receiveNotifications)  stringResource(id = R.string.recibir) else stringResource(id = R.string.no_recibir),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    SwitchTik(receiveNotifications, Modifier.weight(2f)){
                        preferencesVM.changeReceiveNotifications()
                        if (it){
                            mainVM.subscribeUser()
                            mainVM.suscribirASeguidos(seguidos.value)
//                            CoroutineScope(Dispatchers.Main).launch {
//                                val eventos = mainVM.eventosUsuario(mainVM.currentUser.value!!).first()
//                                eventos.map { evento ->
//                                    scheduler.schedule(AlarmItem(getScheduleTime(evento), evento.nombre, evento.localizacion, evento.id))
//                                }
//                            }
                        }else{
                            mainVM.unSubscribeUser()
                            mainVM.unSuscribeASeguidos(seguidos.value)
//                            CoroutineScope(Dispatchers.Main).launch {
//                                val eventos = mainVM.eventosUsuario(mainVM.currentUser.value!!).first()
//                                eventos.map { evento ->
//                                    scheduler.cancel(AlarmItem(getScheduleTime(evento), evento.nombre, evento.localizacion, evento.id))
//                                }
//                            }
//                            MainActivity.CURRENT_CHANNEL = MainActivity.NO_CHANNEL_ID
                        }
                    }
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { logoutVerification = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp))
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text("Logout")
                        Text(
                            text = stringResource(id = R.string.cerrar_sesion_de, mainVM.currentUser.value!!.username),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                }

            }
        }
    }
    val context = LocalContext.current
    EstasSeguroDialog(
        show = logoutVerification,
        mensaje = stringResource(id = R.string.est_s_seguro_de_que_deseas_cerrar_sesi_n),
        onDismiss = { logoutVerification = false }
    ) { CoroutineScope(Dispatchers.IO).launch {
        withContext(Dispatchers.IO) {
            preferencesVM.changeUser("")
        }
        //Log.d("FestUpWidget", "DataStore username ${preferencesVM}")
        mainVM.serverOk.value = false
        mainVM.actualizarWidget(context)

        withContext(Dispatchers.Main) {
            //mainNavController.popBackStack()
            (context as? Activity)?.finishAffinity()
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    } }
}

