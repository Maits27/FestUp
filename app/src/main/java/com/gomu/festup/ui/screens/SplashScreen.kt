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

    LaunchedEffect(Unit) {
        // El siguiente bloque se hace porque en descargarUsuarios es donde se comprueba si el server esta ok
        if (!mainVM.serverOk.value){
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    mainVM.descargarUsuarios()
                }
                if (mainVM.serverOk.value && lastLoggedUser != "") {
                    descargarDatos(mainVM, preferencesVM, identVM, lastLoggedUser, context)
                    withContext(Dispatchers.Main) {
                        navController.navigate(AppScreens.App.route)
                    }
                }
                else {
                    withContext(Dispatchers.Main) {
                        navController.navigate(AppScreens.LoginPage.route)
                    }
                }
            }
        }
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
) {
    val lastLoggedUser = mainVM.actualizarCurrentUser(lastLoggedUserName)
    try {
        // withContext to wait descragarUsuarios() to finish
        withContext(Dispatchers.IO) {
            mainVM.descargarUsuarios()
        }
        nuestroLocationProvider(context, mainVM)
        mainVM.currentUser.value = lastLoggedUser
        identVM.recuperarSesion(preferencesVM.lastBearerToken, preferencesVM.lastRefreshToken)
        mainVM.descargarDatos()
    } catch (e: Exception) {
        Log.e("Excepcion al iniciar sesion", e.toString())
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SplashScreenPreview() {
//    FestUpTheme(darkTheme = true) {
//        SplashContent()
//    }
//}