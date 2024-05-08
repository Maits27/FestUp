package com.gomu.festup

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.gomu.festup.vm.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainVM by viewModels<MainVM>()
    private val identVM by viewModels<IdentVM>()
    private val preferencesVM by viewModels<PreferencesViewModel>()


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        //askLocationPermission()

        super.onCreate(savedInstanceState)

        val notificationChannel = NotificationChannel(MyNotificationChannels.NOTIFICATIONS_CHANNEL.name, "canal de notificaciones", NotificationManager.IMPORTANCE_LOW)
        notificationChannel.description = "descripcion canal notificaciones"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        setContent {
            FestUpTheme {
                AskPermissions()
                // A surface container using the 'background' color from the theme
                Principal(mainVM, identVM, preferencesVM)
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

    @OptIn(ExperimentalPermissionsApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun AskPermissions(){
        val permissions = arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        val permissionState = rememberMultiplePermissionsState(
            permissions = permissions.toList()

        )
        LaunchedEffect(true){
            permissionState.launchMultiplePermissionRequest()
        }
    }
}

enum class MyNotificationChannels {
    NOTIFICATIONS_CHANNEL
}
enum class NotificationID(val id: Int) {
    NOTIFICATIONS(0)
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun Principal(mainVM: MainVM, identVM: IdentVM, preferencesViewModel: PreferencesViewModel) {
    val mainNavController = rememberNavController()
    val dark by preferencesViewModel.darkTheme(mainVM.currentUser.value?.username?:"").collectAsState(initial = true)

    NavHost(
        navController = mainNavController,
        startDestination = AppScreens.LoginPage.route
    ) {
        composable(AppScreens.LoginPage.route) {
            LoginPage(mainNavController, mainVM, identVM, preferencesViewModel)
        }
        composable(AppScreens.App.route) {
            FestUpTheme(dark) {
                App(mainNavController, mainVM, preferencesViewModel)
            }
        }
    }
}

