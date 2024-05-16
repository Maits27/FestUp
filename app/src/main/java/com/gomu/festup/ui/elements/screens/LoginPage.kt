package com.gomu.festup.ui.elements.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.elements.components.EditImageIcon
import com.gomu.festup.ui.elements.components.Imagen
import com.gomu.festup.ui.vm.IdentVM
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.ui.vm.PreferencesViewModel
import com.gomu.festup.utils.nuestroLocationProvider
import com.gomu.festup.utils.toStringNuestro
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun LoginPage(
    mainNavController: NavController,
    mainVM: MainVM,
    identVM: IdentVM,
    preferencesVM: PreferencesViewModel
) {



    Column(
        // TODO quitar esto para que se vea bien con el theme
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = identVM.selectedTabLogin.value
        ) {
            Tab(
                selected = identVM.selectedTabLogin.value == 0,
                onClick = { identVM.selectedTabLogin.value = 0 },
            ) {
                Text(text = "Iniciar sesión", modifier = Modifier.padding(vertical = 15.dp))
            }
            Tab(
                selected = identVM.selectedTabLogin.value == 1,
                onClick = { identVM.selectedTabLogin.value = 1 },
            ) {
                Text(text = "Registrarse", modifier = Modifier.padding(vertical = 15.dp))
            }
        }
        when (identVM.selectedTabLogin.value) {
            0 -> {
                LoginForm(mainNavController, mainVM, identVM, preferencesVM)
            }
            1 -> {
                RegistroForm(mainNavController, mainVM, identVM, preferencesVM)
            }
        }

    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LoginForm(
    mainNavController: NavController,
    mainVM: MainVM,
    identVM: IdentVM,
    preferencesVM: PreferencesViewModel,
) {
    var username by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    val context = LocalContext.current

    var showLoading by rememberSaveable {
        mutableStateOf(false)
    }

    val onLoginButtonClick: () -> Unit = {
        if (username == "") Toast.makeText(context,
            context.getString(R.string.introduce_un_nombre_de_usuario), Toast.LENGTH_SHORT).show()
        else if (password == "") Toast.makeText(context,
            context.getString(R.string.introduce_una_contrase_a), Toast.LENGTH_SHORT).show()
        else {
            showLoading = true
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val usuario = withContext(Dispatchers.IO) {
                        identVM.iniciarSesion(username, password)
                    }
                    if (usuario) {
                        withContext(Dispatchers.IO) {
                            mainVM.descargarDatos()
                        }
                        val currentUser = withContext(Dispatchers.IO) {
                            mainVM.actualizarCurrentUser(username)
                        }
                        nuestroLocationProvider(context, mainVM)
                        mainVM.currentUser.value = currentUser
                        withContext(Dispatchers.IO) {
                            preferencesVM.changeUser(currentUser.username)
                        }

                        withContext(Dispatchers.Main) {
                            mainNavController.navigate(AppScreens.App.route) {
                                popUpTo(0)
                            }
                            showLoading = false
                            mainVM.actualizarWidget(context)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            showLoading = false
                            Toast.makeText(context,
                                context.getString(R.string.la_informacion_no_es_correcta_int_ntalo_de_nuevo), Toast.LENGTH_LONG).show()
                            showLoading = false
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Excepcion al iniciar sesion", e.toString())
                }
            }
        }
    }



    val modifierForInputs = Modifier.padding(vertical = 10.dp)
    var visiblePasswordLogIn by rememberSaveable{mutableStateOf(false)}

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = painterResource(id = R.drawable.festuplogo2),
            alignment = Alignment.Center,
            contentDescription = "Logo-FestUp",
            modifier = Modifier
                .height(70.dp)
                .width(250.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        // TODO quitar esto para que se vea bien con el theme
        //Text(text = "¡Bienvenid@!", fontSize = 25.sp, color = MaterialTheme.colorScheme.onBackground)
        OutlinedTextField(
            value = username,
            onValueChange = { username = it.lowercase().replace(" ", "") },
            label = { Text(text = "Nombre de usuario") },
            modifier = modifierForInputs
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Contraseña") },
            trailingIcon = {
                // Icono para alternar entre contraseña visible y oculta
                val icon =
                    if (visiblePasswordLogIn) painterResource(id = R.drawable.visible) else painterResource(
                        id = R.drawable.no_visible
                    )
                IconButton(onClick = { visiblePasswordLogIn = !visiblePasswordLogIn }) {
                    Icon(icon, contentDescription = "Toggle password visibility")
                }
            },
            visualTransformation = if (visiblePasswordLogIn) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = modifierForInputs
        )
        if (!showLoading) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                if (!mainVM.serverOk.value) Icon(
                    painter = painterResource(id = R.drawable.no_wifi),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    onClick = { onLoginButtonClick() },
                    enabled = mainVM.serverOk.value,
                ) {
                    Text(text = stringResource(R.string.iniciar_sesi_n))
                }
            }
        } else CircularProgressIndicator()
    }



}


@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroForm(
    mainNavController: NavController,
    mainVM: MainVM,
    identVM: IdentVM,
    preferencesVM: PreferencesViewModel
) {

    val context = LocalContext.current

    var username by rememberSaveable {
        mutableStateOf("")
    }

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var telefono by rememberSaveable {
        mutableStateOf("")
    }

    var nombre by rememberSaveable {
        mutableStateOf("")
    }

    var birthDate by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var confirmPassword by rememberSaveable {
        mutableStateOf("")
    }

    // Birth date DatePikcer
    val datePickerState = rememberDatePickerState()
    var showDatePicker by rememberSaveable {
        mutableStateOf(false)
    }

    var showLoading by rememberSaveable {
        mutableStateOf(false)
    }

    var imageUri by rememberSaveable {
        mutableStateOf<Uri?>(Uri.parse("http://34.16.74.167/userProfileImages/no-user.png"))
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imageUri = uri
    }

    val onRegisterButtonClick: () -> Unit = {
        val correct = checkRegisterForm(context, username, email, telefono, nombre, password, confirmPassword)
        if (correct) {
            showLoading = true
            registration(imageUri, mainNavController, mainVM, identVM, preferencesVM, context,
                username, password, email, nombre, birthDate, telefono)
            showLoading = false
        }
    }

    val modifierForInputs = Modifier.padding(vertical = 10.dp)
    var visiblePasswordSingIn by rememberSaveable{mutableStateOf(false)}
    var visiblePasswordSingInR by rememberSaveable{mutableStateOf(false)}

    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT


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
                // Profile image

                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(Modifier.padding(16.dp)) {
                        Imagen(imageUri, context, R.drawable.no_user, 120.dp) {}
                    }
                    // Icono para editar imagen
                    EditImageIcon(singlePhotoPickerLauncher = singlePhotoPickerLauncher)
                }

                // Campo para añadir nombre de usuario
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it.lowercase().replace(" ", "").replace("\n", "") },
                    label = { Text(text = "Nombre de usuario") },
                    modifier = modifierForInputs
                )
                // Campo para añadir email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = modifierForInputs
                )
                // Campo para añadir el número de teléfono
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text(text = "Teléfono") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
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
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ){
                    if(!mainVM.serverOk.value) Icon(painter = painterResource(id = R.drawable.no_wifi), contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                    Button(
                        onClick = { onRegisterButtonClick() },
                        enabled = mainVM.serverOk.value,
                        modifier = Modifier
                            .padding(10.dp)
                    ) {
                        Text(text = stringResource(R.string.registrarse))
                    }
                }

            }
            if (showLoading) CircularProgressIndicator(modifier = Modifier.size(100.dp))
        }
    }
    else{
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
                    .fillMaxSize()
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(Modifier.padding(16.dp)) {
                        Imagen(imageUri, context, R.drawable.no_user, 140.dp) {}
                    }
                    // Icono para editar imagen
                    EditImageIcon(singlePhotoPickerLauncher = singlePhotoPickerLauncher)
                }

                // Campo para añadir nombre de usuario
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it.lowercase().replace(" ", "").replace("\n", "") },
                    label = { Text(text = "Nombre de usuario") },
                    modifier = modifierForInputs
                )
                // Campo para añadir email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = modifierForInputs
                )
                // Campo para añadir el número de teléfono
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text(text = "Teléfono") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = modifierForInputs
                )
            }
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
                    .fillMaxSize()
            ) {

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
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
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
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ){
                    if(!mainVM.serverOk.value) Icon(painter = painterResource(id = R.drawable.no_wifi), contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                    Button(
                        onClick = { onRegisterButtonClick() },
                        enabled = mainVM.serverOk.value,
                        modifier = Modifier
                            .padding(top = 10.dp)
                    ) {
                        Text(text = stringResource(R.string.registrarse))
                    }
                }
            }
        }
        if (showLoading) CircularProgressIndicator(modifier = Modifier.size(100.dp))
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

fun checkRegisterForm(
    context: Context,
    username: String,
    email: String,
    telefono: String,
    nombre: String,
    password: String,
    confirmPassword: String
) : Boolean {
    var correct = false
    val emailRegex = Regex("""\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Z|a-z]{2,}\b""")

    if (username == "") formValidatorError(context, "Introduce un nombre de usuario")
    else if (username.length > 30) formValidatorError(context, "El nombre de usuario no puede tener más de 30 caracteres")
    else if (email == "") formValidatorError(context, "Introduce un email")
    else if (!email.matches(emailRegex)) formValidatorError(context,  "El formato del email no es correcto")
    else if (telefono == "") formValidatorError(context, "Introduce un número de teléfono")
    else if (nombre == "") formValidatorError(context,  "Introduce un nombre")
    else if (password == "") formValidatorError(context,  "Introduce una contraseña")
    else if (password.length < 6) formValidatorError(context,  "La contraseña debe contener al menos 6 caracteres")
    else if (confirmPassword == "") formValidatorError(context,  "Introduce una constraseña de confirmación")
    else if (password != confirmPassword) formValidatorError(context,  "Ambas constraseñas deben conindicir")
    else correct = true

    return correct
}

fun formValidatorError(context: Context, textToShow: String) {
    Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show()
}

@RequiresApi(Build.VERSION_CODES.P)
fun registration(
    imageUri: Uri?,
    mainNavController: NavController,
    mainVM: MainVM,
    identVM: IdentVM,
    preferencesVM: PreferencesViewModel,
    context: Context,
    username: String,
    password: String,
    email: String,
    nombre: String,
    birthDate: String,
    telefono: String
) {
    CoroutineScope(Dispatchers.IO).launch {
        Log.d("IMAGE", "Image uri: ${imageUri.toString()}")
        try {
            val usuario = withContext(Dispatchers.IO) {
                identVM.registrarUsuario(
                    context,
                    username,
                    password,
                    email,
                    nombre,
                    birthDate,
                    telefono,
                    imageUri
                )
            }
            if (usuario != null) {
                withContext(Dispatchers.IO) {
                    mainVM.descargarDatos()
                }
                mainVM.currentUser.value = usuario
                preferencesVM.changeUser(usuario.username)
                withContext(Dispatchers.Main) {
                    mainVM.actualizarWidget(context) // Necesario repetirlo dos veces
                    mainNavController.navigate(AppScreens.App.route) {
                        popUpTo(0)
                    }
                    mainVM.actualizarWidget(context)
                }
            } else {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        context,
                        context.getString(R.string.ese_nombre_de_usuario_ya_est_registrado_int_ntalo_de_nuevo),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } catch (e: HttpRequestTimeoutException) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    context,
                    "No se ha podido conectar con el server.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Log.e("Excepcion al crear usuario", e.toString())
        }
    }
}