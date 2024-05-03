package com.gomu.festup.ui.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.compose.FestUpTheme
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.vm.IdentVM
import com.gomu.festup.vm.MainVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun LoginPage(
    mainNavController: NavController,
    mainVM: MainVM,
    identVM: IdentVM
) {
    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var loginSelected by remember {
        mutableStateOf(false)
    }

    var registerSelected by remember {
        mutableStateOf(false)
    }

    Column(
        // TODO quitar esto para que se vea bien con el theme
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTab
        ) {
            Tab(selected = loginSelected, onClick = {
                selectedTab = 0
                loginSelected = true
                registerSelected = false
            },
                modifier = Modifier.padding(vertical = 15.dp)
            ) {
                Text(text = "Iniciar sesión")
            }
            Tab(selected = registerSelected, onClick = {
                selectedTab = 1
                loginSelected = false
                registerSelected = true
            },
                modifier = Modifier.padding(vertical = 15.dp)
            ) {
                Text(text = "Registrarse")
            }
        }
        if (selectedTab == 0) LoginForm(mainNavController, mainVM, identVM)
        else RegistroForm(mainNavController, mainVM, identVM)
    }
}

@Composable
fun LoginForm(
    mainNavController: NavController,
    mainVM: MainVM,
    identVM: IdentVM
) {
    var username by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    val onLoginButtonClick: () -> Unit = {
        if (username == "") Toast.makeText(context, "Introduce un nombre de usuario", Toast.LENGTH_SHORT).show()
        else if (password == "") Toast.makeText(context, "Introduce una contrseña", Toast.LENGTH_SHORT).show()
        else {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val usuario = withContext(Dispatchers.IO) {
                        identVM.inicarSesion(username, password)
                    }
                    if (usuario) {
                        val currentUser = withContext(Dispatchers.IO) {
                            mainVM.actualizarCurrentUser(username)
                        }
                        withContext(Dispatchers.Main) {
                            mainVM.currentUser.value = currentUser
                            mainNavController.navigate(AppScreens.App.route)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "La informacion no es correcta, inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Excepcion al crear usuario", e.toString())
                }
            }
        }
    }

    val modifierForInputs = Modifier.padding(vertical = 10.dp)

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.festuplogo),
            alignment = Alignment.Center,
            contentDescription = "Logo-FestUp",
            modifier = Modifier
                .fillMaxWidth()
                .size(150.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Text(text = "¡Bienvenid@!", fontSize = 25.sp)
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Nombre de usuario") },
            modifier = modifierForInputs
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = modifierForInputs
        )
        Button(
            onClick = { onLoginButtonClick() },
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 54.dp)
        ) {
            Text(text = "Iniciar sesión")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroForm(
    mainNavController: NavController,
    mainVM: MainVM,
    identVM: IdentVM
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
        mutableStateOf("Fecha de nacimiento")
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

    val onRegisterButtonClick: () -> Unit = {
        if (username == "") Toast.makeText(context, "Introduce un nombre de usuario", Toast.LENGTH_SHORT).show()
        else if (email == "") Toast.makeText(context, "Introduce un email", Toast.LENGTH_SHORT).show()
        else if (nombre == "") Toast.makeText(context, "Introduce un nombre", Toast.LENGTH_SHORT).show()
        else if (password == "") Toast.makeText(context, "Introduce una contraseña", Toast.LENGTH_SHORT).show()
        else if (confirmPassword == "") Toast.makeText(context, "Introduce una constraseña de confirmación", Toast.LENGTH_SHORT).show()
        else if (password != confirmPassword) Toast.makeText(context, "Ambas constraseñas deben conindicir", Toast.LENGTH_SHORT).show()
        else {

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val usuario = withContext(Dispatchers.IO) {
                        identVM.registrarUsuario(username, password, email, nombre, birthDate, "")
                    }
                    if (usuario != null) {
                        mainVM.currentUser.value= usuario
                        mainNavController.navigate(AppScreens.App.route)
                    } else {
                        Toast.makeText(context, "Ha ocurrido un error, inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Excepcion al crear usuario", e.toString())
                }
            }

        }
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse("http://34.16.74.167/userProfileImages/no-user.png"))
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imageUri = uri
    }

    val modifierForInputs = Modifier.padding(vertical = 10.dp)

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
                    placeholder = painterResource(id = R.drawable.ic_launcher_background),
                    contentScale = ContentScale.Crop,
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
            onValueChange = { username = it },
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
        Column (
            modifier = modifierForInputs
                .clickable {
                    showDatePicker = true
                }
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(5.dp))
        ) {
            Text(
                text = birthDate,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(start = 15.dp, top = 15.dp, bottom = 15.dp)
            )
        }
        // Campo para añadir contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Constraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = modifierForInputs
        )
        // Campo para repetir contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(text = "Repite la constraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = modifierForInputs
        )
        Button(
            onClick = { onRegisterButtonClick() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Registrarse")
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
                        birthDate = formatter.format(Date(millis))
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

/*
@Preview(showBackground = true)
@Composable
fun LoginPagePreivew() {
    Surface (
        modifier = Modifier.fillMaxSize()
    ) {
        LoginPage(mainNavController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun LoginFormPreview() {
    LoginForm(rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun RegisterFormPreview() {
    FestUpTheme {
        RegistroForm()
    }
}

 */