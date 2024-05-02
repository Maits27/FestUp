package com.gomu.festup.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.compose.FestUpTheme
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.vm.MainVM
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun LoginPage(
    mainNavController: NavController,
    mainVM: MainVM
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
        if (selectedTab == 0) LoginForm(mainNavController, mainVM)
        else RegistroForm(mainNavController, mainVM)
    }
}

@Composable
fun LoginForm(
    mainNavController: NavController,
    mainVM: MainVM
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
            // TODO COGER LOS DATOS DE LA DB
            mainVM.usuarioMostrar.value= Usuario("nagoregomez","12345","nagore@gmail.com","Nagore Gomez", Date(), "")
            mainVM.currentUser.value= Usuario("maitane","12345","maitane@gmail.com","Nagore Gomez", Date(), "")
            mainNavController.navigate(AppScreens.App.route)
        }
    }

    val modifierForInputs = Modifier.padding(vertical = 10.dp)

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.whatsapp_image_2024_04_29_at_17_09_44),
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
    mainVM: MainVM
) {

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
        mutableStateOf("(haga click para introducir)")
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
            // TODO COGER LOS DATOS DE LA DB
            mainVM.usuarioMostrar.value= Usuario("nagoregomez","12345","nagore@gmail.com","Nagore Gomez", Date(), "")
            mainNavController.navigate(AppScreens.App.route)
        }
    }

    val modifierForInputs = Modifier.padding(vertical = 10.dp)

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 56.dp)
    ) {

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Nombre de usuario") },
            modifier = modifierForInputs
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = modifierForInputs
        )
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text(text = "Nombre") },
            modifier = modifierForInputs
        )
        Column (
            modifier = modifierForInputs
                .clickable {
                    showDatePicker = true
                }
                .fillMaxWidth()
                .padding(start = 14.dp)
        ) {
            Text(
                text = "Fecha de nacimiento:",
                fontSize = 17.sp
            )
            Text(
                text = birthDate,
                fontSize = 17.sp,
            )
        }
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Constraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = modifierForInputs
        )
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