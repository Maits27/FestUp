package com.gomu.festup.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.gomu.festup.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.currentCameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import java.text.SimpleDateFormat
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEvento(navController: NavController) {

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
            "Introduce una ubicación para el vento",
            Toast.LENGTH_SHORT
        ).show()
        else {
            // TODO mandar el viewModel
            navController.popBackStack()
        }
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imageUri = uri
    }

    var cameraPosition = LatLng(43.26331851716892, -2.9504032685158053)
    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cameraPosition, 8f)
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
                    .padding(start = 15.dp)
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(5.dp))
                    .clickable {
                        showDatePicker = true
                    }
            ) {
                Text(
                    text = fecha,
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 17.dp)
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
            onValueChange = { location = it },
            label = { Text(text = "Ubicación") },
            modifier = modifierForInputs
        )
        GoogleMap(
            properties = MapProperties(isMyLocationEnabled = true),
            // TODO estbalecer la cámara a la posición actual
            cameraPositionState = cameraPositionState,
            modifier = modifierForInputs
                .size(400.dp)
                .clip(RoundedCornerShape(15.dp))
        )
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
                        val formatter = SimpleDateFormat("dd/MM/yyyy")
                        fecha = formatter.format(Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text(text = "Confirmar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEventoPreview() {
    AddEvento(navController = rememberNavController())
}