package com.gomu.festup.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.gomu.festup.LocalDatabase.Entities.Evento
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.utils.formatearFecha
import com.gomu.festup.utils.getLatLngFromAddress
import com.gomu.festup.utils.localUriToBitmap
import com.gomu.festup.utils.toStringNuestro
import com.gomu.festup.vm.MainVM
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
import java.text.SimpleDateFormat
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
    val coroutineScope = rememberCoroutineScope()

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
        mutableStateOf("Fecha")
    }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember {
        mutableStateOf(false)
    }

    val modifierForInputs = Modifier
        .fillMaxWidth()
        .padding(top = 15.dp)

    val context = LocalContext.current

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
            "Introduce un nombre para el evento",
            Toast.LENGTH_SHORT
        ).show()
        else if (fecha == "Fecha") Toast.makeText(
            context,
            "Introduce una fecha para el evento",
            Toast.LENGTH_SHORT
        ).show()
        else if (description == "") Toast.makeText(
            context,
            "Introduce una descripción para el evento",
            Toast.LENGTH_SHORT
        ).show()
        else if (location == "") Toast.makeText(
            context,
            "Introduce una ubicación para el evento",
            Toast.LENGTH_SHORT
        ).show()
        else {
            val fechaEvento = fecha.formatearFecha()
            /*coroutineScope.launch(Dispatchers.IO) {
                val insertCorrecto= mainVM.insertarEvento(Evento(
                    nombre = eventName,
                    fecha = fechaEvento,
                    descripcion = description,
                    localizacion = location,
                    numeroAsistentes = 1
                ))
                if (insertCorrecto){

                }
            }*/



            CoroutineScope(Dispatchers.IO).launch {
                val insertCorrecto = withContext(Dispatchers.IO) {
                    var imageBitmap: Bitmap? = null
                    if (imageUri != null) imageBitmap = context.localUriToBitmap(imageUri!!)
                    mainVM.insertarEvento(Evento(
                        id = "",
                        nombre = eventName,
                        fecha = fechaEvento,
                        descripcion = description,
                        localizacion = location,
                        numeroAsistentes = 1
                    ),
                        image = imageBitmap)
                }
                if (insertCorrecto) {
                    withContext(Dispatchers.Main) {
                        navController.popBackStack()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Ha ocurrido un error, inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }




    var miLocalizacion = mainVM.localizacion.value
    var cameraPositionState = rememberCameraPositionState {
        if (miLocalizacion != null) {
            position =
                CameraPosition.fromLatLngZoom(LatLng(miLocalizacion.latitude, miLocalizacion.longitude), 10f)
        }else{
            position = CameraPosition.fromLatLngZoom(LatLng(1.0, 1.0), 10f)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box (
            modifier = Modifier.clickable {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Event photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(150.dp)
                )
            }
            else {
                Image(
                    painter = painterResource(id = R.drawable.round_camera_alt_24),
                    contentDescription = "Event image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(150.dp)
                )
            }
            Icon(
                imageVector = Icons.Rounded.Edit,
                tint = MaterialTheme.colorScheme.surface,
                contentDescription = "Edit",
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.BottomEnd)
                    .size(25.dp)
            )
        }
        Row(
            modifier = modifierForInputs.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text(text = "Nombre") },
                modifier = Modifier.weight(1f)
            )
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp, start = 15.dp)
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
        }
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(text = "Descripción") },
            maxLines = 10,
            minLines = 5,
            modifier = modifierForInputs
        )
        OutlinedTextField(
            value = location,
            onValueChange = {
                location = it
                var currentLoc = getLatLngFromAddress(context, it)
                if(currentLoc != null && currentLoc != mainVM.localizacionAMostrar.value){
                    mainVM.localizacionAMostrar.value = currentLoc
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(mainVM.localizacionAMostrar.value!!.first, mainVM.localizacionAMostrar.value!!.second), 10f)
                }
            },
            label = { Text(text = "Ubicación") },
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
                    state = MarkerState(position = LatLng(mainVM.localizacionAMostrar.value?.first?:0.0, mainVM.localizacionAMostrar.value?.second?:0.0)),
                    title = eventName,
                    snippet = fecha
                )
            }
        }
        Button(
            onClick = { onAddButtonClick() },
            modifier = modifierForInputs.padding(bottom = 15.dp)
        ) {
            Text(text = "Añadir")
        }
    }

    if (showDatePicker) {
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
                    Text(text = "Confirmar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Fecha del evento",
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

//@Preview(showBackground = true)
//@Composable
//fun AddEventoPreview() {
//    AddEvento(navController = rememberNavController())
//}