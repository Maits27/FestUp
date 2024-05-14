package com.gomu.festup

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.FestUpTheme
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.screens.App
import com.gomu.festup.ui.screens.LoginPage
import com.gomu.festup.ui.screens.SplashScreen
import com.gomu.festup.utils.nuestroLocationProvider
import com.gomu.festup.vm.IdentVM
import com.gomu.festup.vm.MainVM
import com.gomu.festup.vm.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainVM by viewModels<MainVM>()
    private val identVM by viewModels<IdentVM>()
    private val preferencesVM by viewModels<PreferencesViewModel>()

    // Set a CHANNEL_ID
    companion object{
        const val CHANNEL_ID = "FestUpNotifChannel"
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        createNotificationChannel()
        super.onCreate(savedInstanceState)

        setContent {
            FestUpTheme {
                AskPermissions()
                Principal(mainVM, identVM, preferencesVM)
            }
        }
    }

    // Function to create a local notification channel
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "FestUpNotificationChannel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Canal de notificaciones para FestUp"
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    @OptIn(ExperimentalPermissionsApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun AskPermissions(){
        val permissions = arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CONTACTS
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

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun Principal(
    mainVM: MainVM,
    identVM: IdentVM,
    preferencesVM: PreferencesViewModel
) {
    val mainNavController = rememberNavController()
    val dark by preferencesVM.darkTheme(mainVM.currentUser.value?.username?:"").collectAsState(initial = true)

    NavHost(
        navController = mainNavController,
        startDestination = AppScreens.SplashScreen.route
    ) {
        composable(AppScreens.SplashScreen.route,
            enterTransition = { fadeIn(animationSpec = tween(1000)) },
            exitTransition = { fadeOut(animationSpec = tween(1000)) }
        ) {
            FestUpTheme(dark) {
                SplashScreen(mainNavController, mainVM, preferencesVM, identVM)
            }
        }
        composable(AppScreens.LoginPage.route,
            enterTransition = { fadeIn(animationSpec = tween(1000)) },
            exitTransition = { fadeOut(animationSpec = tween(1000)) }
        ) {
            LoginPage(mainNavController, mainVM, identVM, preferencesVM)
        }
        composable(AppScreens.App.route,
            enterTransition = { fadeIn(animationSpec = tween(1000)) },
            exitTransition = { fadeOut(animationSpec = tween(1000)) }
        ) {
            FestUpTheme(dark) {
                App(mainNavController, mainVM, preferencesVM)
            }
        }
    }
}

