package com.gomu.festup.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.utils.nuestroLocationProvider
import com.gomu.festup.utils.toStringNuestro
import com.gomu.festup.vm.IdentVM
import com.gomu.festup.vm.MainVM
import com.gomu.festup.vm.PreferencesViewModel
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
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
    preferencesVM: PreferencesViewModel,
    lastLoggedUser: Usuario?
) {
    val context = LocalContext.current

    if (!mainVM.serverOk.value){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.IO) {
                    mainVM.descargarUsuarios()
                }
                if (lastLoggedUser!= null) {
                    withContext(Dispatchers.IO) {
                        mainVM.descargarDatos()
                    }
                    nuestroLocationProvider(context, mainVM)
                    mainVM.currentUser.value = lastLoggedUser
                    preferencesVM.changeUser(lastLoggedUser.username)
                    identVM.recuperarSesion(preferencesVM.lastBearerToken,preferencesVM.lastRefreshToken)
                    if (mainVM.serverOk.value){
                        withContext(Dispatchers.Main) {
                            mainNavController.navigate(AppScreens.App.route)
                        }
                    }

                }
            } catch (e: Exception) {
                Log.e("Excepcion al iniciar sesion", e.toString())
            }
        }
    }

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    Column(
        // TODO quitar esto para que se vea bien con el theme
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTab
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
            ) {
                Text(text = "Iniciar sesión", modifier = Modifier.padding(vertical = 15.dp))
            }
            Tab(
                selected = selectedTab==1,
                onClick = { selectedTab = 1 },
            ) {
                Text(text = "Registrarse", modifier = Modifier.padding(vertical = 15.dp))
            }
        }
        when (selectedTab) {
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
    var username by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    var showLoading by remember {
        mutableStateOf(false)
    }

    val onLoginButtonClick: () -> Unit = {
        if (username == "") Toast.makeText(context, "Introduce un nombre de usuario", Toast.LENGTH_SHORT).show()
        else if (password == "") Toast.makeText(context, "Introduce una contrseña", Toast.LENGTH_SHORT).show()
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
                        mainVM.actualizarWidget(context)
                        nuestroLocationProvider(context, mainVM)
                        mainVM.currentUser.value = currentUser
                        preferencesVM.changeUser(currentUser.username)

                        withContext(Dispatchers.Main) {
                            mainNavController.navigate(AppScreens.App.route)
                            showLoading = false
                        }

                    } else {
                        withContext(Dispatchers.Main) {
                            showLoading = false
                            Toast.makeText(context, "La informacion no es correcta, inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
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
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.festup),
            alignment = Alignment.Center,
            contentDescription = "Logo-FestUp",
            modifier = Modifier
                .fillMaxWidth()
                .size(250.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        // TODO quitar esto para que se vea bien con el theme
        Text(text = "¡Bienvenid@!", fontSize = 25.sp, color = MaterialTheme.colorScheme.onBackground)
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
                val icon = if (visiblePasswordLogIn) painterResource(id = R.drawable.visible) else painterResource(id = R.drawable.no_visible)
                IconButton(onClick = { visiblePasswordLogIn = !visiblePasswordLogIn }) {
                    Icon(icon, contentDescription = "Toggle password visibility")
                }
            },
            visualTransformation = if (visiblePasswordLogIn) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = modifierForInputs
        )
        if (!showLoading) {
            Button(
                onClick = { onLoginButtonClick() },
                enabled = mainVM.serverOk.value,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 54.dp)
            ) {
                Text(text = "Iniciar sesión")
            }
        }
        else CircularProgressIndicator(modifier = Modifier
            .align(Alignment.End)
            .padding(end = 54.dp))
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
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    var username by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var nombre by remember {
        mutableStateOf("")
    }

    var birthDate by remember {
        mutableStateOf("")
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

    var showLoading by remember {
        mutableStateOf(false)
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse("http://34.16.74.167/userProfileImages/no-user.png"))
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imageUri = uri
    }

    val onRegisterButtonClick: () -> Unit = {
        val correct = checkRegisterForm(context, username, email, nombre, password, confirmPassword)
        if (correct) {
            showLoading = true
            registration(imageUri, mainNavController, mainVM, identVM, preferencesVM, context, username, password,
                email, nombre, birthDate)
            showLoading = false
        }
    }

    val modifierForInputs = Modifier.padding(vertical = 10.dp)
    var visiblePasswordSingIn by rememberSaveable{mutableStateOf(false)}
    var visiblePasswordSingInR by rememberSaveable{mutableStateOf(false)}

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
                        error = painterResource(id = R.drawable.no_user),
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
            // Campo para añadir nombre de usuario
            OutlinedTextField(
                value = username,
                onValueChange = { username = it.lowercase().replace(" ", "") },
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
            Button(
                onClick = { onRegisterButtonClick() },
                enabled = mainVM.serverOk.value
            ) {
                Text(text = "Registrarse")
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
    birthDate: String
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
                    mainNavController.navigate(AppScreens.App.route)
                }
            } else {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        context,
                        "Ha ocurrido un error, inténtalo de nuevo.",
                        Toast.LENGTH_SHORT
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
