package com.gomu.festup.ui.elements.screens

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomu.festup.R
import com.gomu.festup.ui.elements.components.EditImageIcon
import com.gomu.festup.ui.elements.components.FestUpButton
import com.gomu.festup.ui.elements.components.Imagen
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.utils.formatearFecha
import com.gomu.festup.utils.toStringNuestro
import java.util.Date

/**
 * Pantalla para editar el perfil de un [Usuario] en la aplicación:
 *      - Nombre
 *      - Correo electrónico
 *      - Teléfono
 *      - Fecha de nacimiento
 *      - Foto de perfil
 * (aparece su visualización tanto en horizontal como en vertical).
 */

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPerfil(
    navController: NavController,
    mainVM: MainVM
) {
    val context = LocalContext.current
    val currentUser = mainVM.currentUser.value!!
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    var email by remember {
        mutableStateOf(currentUser.email)
    }

    var nombre by remember {
        mutableStateOf(currentUser.nombre)
    }

    var birthDate by remember {
        mutableStateOf(currentUser.fechaNacimiento.toStringNuestro())
    }

    var telefono by remember {
        mutableStateOf(currentUser.telefono)
    }

    // DatePikcer para fecha de nacimiento
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse("http://34.71.128.243/userProfileImages/${currentUser.username}.png"))
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null){
            imageUri = uri
        }
    }

    val onEditButtonClick: () -> Unit = {
        val correct = checkEditPerfil(context, email, nombre)
        if (correct) {
            mainVM.editUsuario(currentUser.username, email, nombre, birthDate.formatearFecha(), telefono)
            if (imageUri != Uri.parse("http://34.71.128.243/userProfileImages/${currentUser.username}.png")){
                mainVM.updateUserImage(context, currentUser.username, imageUri)
            }
            navController.popBackStack()
        }
    }

    val modifierForInputs = Modifier.padding(bottom = if (isVertical) 16.dp else 10.dp)

    if (isVertical){
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 56.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Imagen de perfil
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(Modifier.padding(16.dp)) {
                        Imagen(imageUri, R.drawable.no_user, 120.dp) {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    }
                    EditImageIcon(singlePhotoPickerLauncher = singlePhotoPickerLauncher)
                }

                // Campo para editar nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(text = context.getString(R.string.nombre)) },
                    modifier = modifierForInputs
                )
                // Campo para editar email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = context.getString(R.string.email)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = modifierForInputs
                )
                // Campo para editar número de telefono
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text(text = context.getString(R.string.telefono)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = modifierForInputs
                )
                // Añadir fecha de nacimiento
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = {  },
                    label = { Text(text = context.getString(R.string.fecha_nacimiento)) },
                    modifier = modifierForInputs.clickable { showDatePicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    enabled = false
                )
                FestUpButton(
                    onClick = { onEditButtonClick() },
                ) {
                    Text(text = context.getString(R.string.edit_profile))
                }
            }
        }
    }
    else{
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Profile image
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(Modifier.padding(16.dp)) {
                        Imagen(imageUri, R.drawable.no_user, 120.dp) {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    }
                    EditImageIcon(singlePhotoPickerLauncher = singlePhotoPickerLauncher)
                }
            }

            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 40.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Campo para añadir email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = context.getString(R.string.email)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = modifierForInputs.fillMaxWidth()
                )
                // Campo para añadir nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(text = context.getString(R.string.nombre)) },
                    modifier = modifierForInputs.fillMaxWidth()
                )
                // Campo para el telefono
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text(text = context.getString(R.string.telefono)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = modifierForInputs.fillMaxWidth()
                )
                // Añadir fecha de nacimiento
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = {  },
                    label = { Text(text = context.getString(R.string.fecha_nacimiento)) },
                    modifier = modifierForInputs
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    enabled = false
                )
                FestUpButton(
                    onClick = { onEditButtonClick() },
                ) {
                    Text(text = context.getString(R.string.edit_profile))
                }
            }
        }
    }


    // DatePicker con diferentes formatos según la orientación
    if (showDatePicker) {
        if (isVertical) datePickerState.displayMode = DisplayMode.Picker
        else datePickerState.displayMode = DisplayMode.Input
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    millis?.let {
                        birthDate = Date(millis).toStringNuestro()
                    }
                    showDatePicker = false
                }) {
                    Text(text = context.getString(R.string.aceptar))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = context.getString(R.string.fecha_nacimiento),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp)
                    )
                },
                dateValidator = { utcTimeMillis ->
                    if (utcTimeMillis > System.currentTimeMillis()) return@DatePicker false
                    else return@DatePicker true
                }
            )
        }
    }
}

/**
 * Función para comprobar si los cambios en la edición del perfil son correctos
 */
fun checkEditPerfil(
    context: Context,
    email: String,
    nombre: String
) : Boolean {
    var correct = false
    val emailRegex = Regex("""\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Z|a-z]{2,}\b""")

    if (email == "") formValidatorError(context, context.getString(R.string.insert_email))
    else if (!email.matches(emailRegex)) formValidatorError(context,  context.getString(R.string.email_incorrecto))
    else if (nombre == "") formValidatorError(context, context.getString(R.string.insert_nombre))
    else correct = true

    return correct
}