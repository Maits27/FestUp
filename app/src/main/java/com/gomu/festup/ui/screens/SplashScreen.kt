package com.gomu.festup.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose.FestUpTheme
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.utils.nuestroLocationProvider
import com.gomu.festup.vm.IdentVM
import com.gomu.festup.vm.MainVM
import com.gomu.festup.vm.PreferencesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SplashScreen(
    navController: NavController,
    mainVM: MainVM,
    preferencesVM: PreferencesViewModel,
    identVM: IdentVM
) {
    val context = LocalContext.current
    val lastLoggedUser = preferencesVM.lastLoggedUser
    if (lastLoggedUser != "") {
        /* TODO
        * 1. descargar datos
        * 2. navController.navigate(AppScreens.App.route)
        */
        LaunchedEffect(Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                descargarDatos(mainVM, preferencesVM, identVM, lastLoggedUser, context)
                withContext(Dispatchers.Main) {
                    navController.navigate(AppScreens.App.route)
                }
            }
        }

        // TODO quitar la siguiente linea cuando funcione
        //navController.navigate(AppScreens.LoginPage.route)
    }
    else {
        navController.navigate(AppScreens.LoginPage.route)
    }

    SplashContent()
}

@Composable
fun SplashContent() {
    // Surface to use the theme
    Surface {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.festup),
                contentDescription = "Logo",
                modifier = Modifier.padding(horizontal = 40.dp)
            )
            CircularProgressIndicator()
        }
    }
}


suspend fun descargarDatos(
    mainVM: MainVM,
    preferencesVM: PreferencesViewModel,
    identVM: IdentVM,
    lastLoggedUserName: String,
    context: Context
)
{
    val lastLoggedUser = mainVM.actualizarCurrentUser(lastLoggedUserName)
    if (!mainVM.serverOk.value){
        Log.d("He pasado por aqui", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        Log.d("LAST LOGGED USER", lastLoggedUser.username ?:"null")
        Log.d("LAST LOGGED USER-name", lastLoggedUserName ?:"null")
        try {
            withContext(Dispatchers.IO) {
                mainVM.descargarUsuarios()
            }
            if (lastLoggedUser!= null) {
                nuestroLocationProvider(context, mainVM)
                mainVM.currentUser.value = lastLoggedUser
                identVM.recuperarSesion(preferencesVM.lastBearerToken, preferencesVM.lastRefreshToken)
                withContext(Dispatchers.IO) {
                    mainVM.descargarDatos()
                }
            }
            Log.d("Current user", mainVM.currentUser.value!!.username)
        } catch (e: Exception) {
            Log.e("Excepcion al iniciar sesion", e.toString())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    FestUpTheme(darkTheme = true) {
        SplashContent()
    }
}