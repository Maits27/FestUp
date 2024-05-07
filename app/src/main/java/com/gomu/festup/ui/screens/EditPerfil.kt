package com.gomu.festup.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.utils.toStringNuestro
import com.gomu.festup.vm.MainVM
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPerfil(
    navController: NavController,
    mainVM: MainVM
) {
    val context = LocalContext.current
    val currentUser = mainVM.currentUser.value!!

    var username by remember {
        mutableStateOf(currentUser.username)
    }

    var email by remember {
        mutableStateOf(currentUser.email)
    }

    var nombre by remember {
        mutableStateOf(currentUser.nombre)
    }

    var birthDate by remember {
        mutableStateOf(currentUser.fechaNacimiento.toStringNuestro())
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    // Birth date DatePikcer
    var datePickerState = rememberDatePickerState()
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse("http://34.16.74.167/userProfileImages/${currentUser.username}.png"))
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imageUri = uri
    }

    val onEditButtonClick: () -> Unit = {
        val correct = checkRegisterForm(context, username, email, nombre, password, confirmPassword)
        if (correct) {
            Log.d("TODO OK", "EDITAR USUARIO") //TODO EDITAR USUARIO
//            registration(imageUri, na, mainVM, identVM, context, username, password,
//                email, nombre, birthDate)
        }
    }

    val modifierForInputs = Modifier.padding(vertical = 10.dp)
    var visiblePasswordSingIn by rememberSaveable{ mutableStateOf(false) }
    var visiblePasswordSingInR by rememberSaveable{ mutableStateOf(false) }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 56.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile image
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(Modifier.padding(16.dp)) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "User image",
                        placeholder = painterResource(id = R.drawable.no_user),
                        contentScale = ContentScale.Crop,
                        onError = {
                            imageUri = Uri.parse("http://34.16.74.167/userProfileImages/no-user.png")
                        },
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }
                // Icono para editar imagen
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(bottom = 16.dp, end = 8.dp)
                        .clip(CircleShape)
                        .clickable(onClick = {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        })
                ) {
                    //Añadir circle y edit
                    Icon(
                        painterResource(id = R.drawable.circle),
                        contentDescription = null,
                        Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        painterResource(id = R.drawable.edit),
                        contentDescription = null,
                        Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.surface
                    )
                }
            }

            // Campo para añadir email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = modifierForInputs
            )
            // Campo para añadir nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text(text = "Nombre") },
                modifier = modifierForInputs
            )
            // Añadir fecha de nacimiento
            OutlinedTextField(
                value = birthDate,
                onValueChange = {  },
                label = { Text(text = "Fecha de nacimiento") },
                modifier = modifierForInputs.clickable { showDatePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                enabled = false
            )
            // Campo para añadir contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Contraseña") },
                trailingIcon = {
                    // Icono para alternar entre contraseña visible y oculta
                    val icon = if (visiblePasswordSingIn) painterResource(id = R.drawable.visible) else painterResource(id = R.drawable.no_visible)
                    IconButton(onClick = { visiblePasswordSingIn = !visiblePasswordSingIn }) {
                        Icon(icon, contentDescription = "Toggle password visibility")
                    }
                },
                visualTransformation = if (visiblePasswordSingIn) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = modifierForInputs
            )
            // Campo para repetir contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(text = "Repite la contraseña") },
                trailingIcon = {
                    // Icono para alternar entre contraseña visible y oculta
                    val icon = if (visiblePasswordSingInR) painterResource(id = R.drawable.visible) else painterResource(id = R.drawable.no_visible)
                    IconButton(onClick = { visiblePasswordSingInR = !visiblePasswordSingInR }) {
                        Icon(icon, contentDescription = "Toggle password visibility")
                    }
                },
                visualTransformation = if (visiblePasswordSingInR) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = modifierForInputs
            )
            Button(
                onClick = { onEditButtonClick() }
            ) {
                Text(text = "Editar perfil")
            }
        }
    }


    if (showDatePicker) {
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
                    Text(text = "Confirmar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Fecha de nacimiento",
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