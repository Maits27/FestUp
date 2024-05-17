package com.gomu.festup.ui.elements.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomu.festup.R
import com.gomu.festup.alarmMng.AndroidAlarmScheduler
import com.gomu.festup.data.AlarmItem
import com.gomu.festup.data.localDatabase.Entities.Evento
import com.gomu.festup.ui.elements.components.EditImageIcon
import com.gomu.festup.ui.elements.components.FestUpButton
import com.gomu.festup.ui.elements.components.ImagenEvento
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.utils.addEventOnCalendar
import com.gomu.festup.utils.formatearFecha
import com.gomu.festup.utils.getLatLngFromAddress
import com.gomu.festup.utils.localUriToBitmap
import com.gomu.festup.utils.toStringNuestro
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date


@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEvento(
    navController: NavController,
    mainVM: MainVM
) {
    val context = LocalContext.current
    val scheduler = AndroidAlarmScheduler(context)

    var addOnCalendar by remember { mutableStateOf(false) }

    var eventName by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var location by remember {
        mutableStateOf("")
    }

    var fecha by remember {
        mutableStateOf(context.getString(R.string.fecha))
    }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember {
        mutableStateOf(false)
    }

    val modifierForInputs = Modifier
        .fillMaxWidth()
        .padding(top = 15.dp)


    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imageUri = uri
    }

    val onAddButtonClick: () -> Unit = {
        if (eventName == "") Toast.makeText(
            context,
            context.getString(R.string.insert_nombre_evento),
            Toast.LENGTH_SHORT
        ).show()
        else if (fecha == context.getString(R.string.fecha)) Toast.makeText(
            context,
            context.getString(R.string.insert_date_evento),
            Toast.LENGTH_SHORT
        ).show()
        else if (description == "") Toast.makeText(
            context,
            context.getString(R.string.insert_desc_event),
            Toast.LENGTH_SHORT
        ).show()
        else if (location == "") Toast.makeText(
            context,
            context.getString(R.string.insert_loc_evento),
            Toast.LENGTH_SHORT
        ).show()
        else {
            CoroutineScope(Dispatchers.IO).launch {
                val newEvento = Evento(
                    id = "",
                    nombre = eventName,
                    fecha = fecha.formatearFecha(),
                    descripcion = description,
                    localizacion = location,
                )
                val insertCorrecto = withContext(Dispatchers.IO) {
                    var imageBitmap: Bitmap? = null
                    if (imageUri != null) imageBitmap = context.localUriToBitmap(imageUri!!)
                    mainVM.insertarEvento(newEvento, imageBitmap)
                }
                if (insertCorrecto) {
                    withContext(Dispatchers.Main) {

                        val date = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(datePickerState.selectedDateMillis!!),
                            ZoneId.systemDefault()
                        ).toLocalDate()

                        val scheduleTime = LocalDateTime.of(date.year,
                            date.month,
                            date.minusDays(1).dayOfMonth,
                            LocalDateTime.now().hour,
                            LocalDateTime.now().minute + 1
                        )
                        Log.d("AlarmScheduler", "Alarma programada")
                        scheduler.schedule(AlarmItem(scheduleTime, newEvento.nombre, newEvento.localizacion, newEvento.id))

                        if( addOnCalendar ) {
                            addEventOnCalendar(
                                context,
                                newEvento.nombre,
                                datePickerState.selectedDateMillis!!
                            )
                        }
                        mainVM.actualizarWidget(context)
                        navController.popBackStack()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, context.getString(R.string.error_intentalo), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    val miLocalizacion = mainVM.localizacion.value
    val cameraPositionState = rememberCameraPositionState {
        position = if (miLocalizacion != null) {
            CameraPosition.fromLatLngZoom(LatLng(miLocalizacion.latitude, miLocalizacion.longitude), 10f)
        } else{
            CameraPosition.fromLatLngZoom(LatLng(1.0, 1.0), 10f)
        }
    }

    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = if (isVertical) 30.dp else 70.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (isVertical){
            Box (contentAlignment = Alignment.BottomEnd,) {
                ImagenEvento(imageUri, R.drawable.round_camera_alt_24, 150.dp ) {}
                EditImageIcon(singlePhotoPickerLauncher = singlePhotoPickerLauncher)
            }
            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text(text = context.getString(R.string.nombre)) },
                modifier = modifierForInputs
            )
            Row(
                modifier = modifierForInputs.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box (
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(5.dp))
                        .clickable {
                            showDatePicker = true
                        }
                ) {
                    Text(
                        text = fecha,
                        textAlign = TextAlign.Center,
                        fontSize = 17.sp,
                        modifier = Modifier
                            .padding(18.dp)
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.add_calendar),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Checkbox(
                        checked = addOnCalendar,
                        onCheckedChange = { addOnCalendar = it },
                        modifier = Modifier
                            .scale(0.7f)
                            .size(18.dp)
                    )
                }

            }
        }
        else{
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box (contentAlignment = Alignment.BottomEnd) {
                    ImagenEvento(imageUri, R.drawable.round_camera_alt_24, 150.dp ) {}
                    EditImageIcon(singlePhotoPickerLauncher = singlePhotoPickerLauncher)
                }

                Column(
                    modifier = Modifier.padding(start = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    OutlinedTextField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        label = { Text(text = context.getString(R.string.nombre)) },
                        modifier = modifierForInputs
                    )

                    Row(
                        modifier = modifierForInputs.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box (
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(top = 8.dp)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    showDatePicker = true
                                }
                        ) {
                            Text(
                                text = fecha,
                                textAlign = TextAlign.Center,
                                fontSize = 17.sp,
                                modifier = Modifier
                                    .padding(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                        Column (
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Icon(
                                painter = painterResource(id = R.drawable.add_calendar),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(5.dp))
                            Checkbox(
                                checked = addOnCalendar,
                                onCheckedChange = { addOnCalendar = it },
                                modifier = Modifier
                                    .scale(0.7f)
                                    .size(18.dp)
                            )
                        }

                    }
                }

            }
        }
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(text = context.getString(R.string.desc)) },
            maxLines = 10,
            minLines = 5,
            modifier = modifierForInputs
        )
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text(text = context.getString(R.string.loc)) },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.lupa),
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        val currentLoc = getLatLngFromAddress(context, location)
                        if (currentLoc != null && currentLoc != mainVM.localizacionAMostrar.value) {
                            mainVM.localizacionAMostrar.value = currentLoc
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                mainVM.localizacionAMostrar.value!!,
                                10f
                            )
                        }
                    })
                },
            modifier = modifierForInputs
        )
        GoogleMap(
            properties = MapProperties(isMyLocationEnabled = true),
            cameraPositionState = cameraPositionState,
            modifier = modifierForInputs
                .size(400.dp)
                .clip(RoundedCornerShape(15.dp))
        ){
            if (mainVM.localizacionAMostrar.value!=null){
                Marker(
                    state = MarkerState(position = mainVM.localizacionAMostrar.value!!),
                    title = eventName,
                    snippet = fecha
                )
            }
        }
        FestUpButton(
            onClick = { onAddButtonClick() },
            modifier = Modifier.padding(vertical = 15.dp)
        ) {
            Text(text = context.getString(R.string.add))
        }
    }

    if (showDatePicker) {
        if (isVertical) datePickerState.displayMode = DisplayMode.Picker
        else datePickerState.displayMode = DisplayMode.Input
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    millis?.let {
                        fecha = Date(millis).toStringNuestro()
                    }
                    showDatePicker = false
                }) {
                    Text(text = stringResource(R.string.confirmar))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = stringResource(R.string.fecha_del_evento),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp)
                    )
                },
                dateValidator = { utcTimeMillis ->
                    val dateToCheck = Calendar.getInstance().apply { timeInMillis = utcTimeMillis }
                    val today = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }

                    // If is today
                    if (dateToCheck.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        dateToCheck.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                        dateToCheck.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
                        ) {
                        return@DatePicker true
                    }
                    // If is past
                    else if (dateToCheck.before(today)) return@DatePicker false
                    // If is future
                    else return@DatePicker true
                }
            )
        }
    }
}


