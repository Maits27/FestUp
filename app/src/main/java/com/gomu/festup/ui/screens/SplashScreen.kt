package com.gomu.festup.ui.screens

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
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose.FestUpTheme
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.vm.MainVM
import com.gomu.festup.vm.PreferencesViewModel

@Composable
fun SplashScreen(navController: NavController, mainVM: MainVM, preferencesVM: PreferencesViewModel) {
    val lastLoggedUser = preferencesVM.lastLoggedUser
    if (lastLoggedUser != "") {
        /* TODO
        * 1. descargar datos
        * 2. navController.navigate(AppScreens.App.route) ()
        */
        navController.navigate(AppScreens.LoginPage.route)
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

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    FestUpTheme(darkTheme = true) {
        SplashContent()
    }
}