package com.gomu.festup

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.FestUpTheme
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.screens.App
import com.gomu.festup.ui.screens.LoginPage
import com.gomu.festup.vm.IdentVM
import com.gomu.festup.vm.MainVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainVM by viewModels<MainVM>()
    private val identVM by viewModels<IdentVM>()


    override fun onCreate(savedInstanceState: Bundle?) {

        askLocationPermission()

        super.onCreate(savedInstanceState)
        setContent {
            FestUpTheme {
                // A surface container using the 'background' color from the theme
                Principal(mainVM, identVM)
            }
        }
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
            } else {
            }
        }

    fun askLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}

@Composable
fun Principal(mainVM: MainVM, identVM: IdentVM) {
    val mainNavController = rememberNavController()

    NavHost(
        navController = mainNavController,
        startDestination = AppScreens.LoginPage.route
    ) {
        composable(AppScreens.LoginPage.route) {
            LoginPage(mainNavController, mainVM, identVM)
        }
        composable(AppScreens.App.route) {
            App(mainNavController, mainVM)
        }
    }
}

