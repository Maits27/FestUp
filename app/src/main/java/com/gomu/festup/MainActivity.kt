package com.gomu.festup

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.glance.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.FestUpTheme
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.elements.screens.App
import com.gomu.festup.ui.elements.screens.LoginPage
import com.gomu.festup.ui.elements.screens.SplashScreen
import com.gomu.festup.ui.vm.IdentVM
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.ui.vm.PreferencesViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    /* DEPRECATED
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                // Aquí obtienes el texto del código QR escaneado
                val scannedCode = result.contents
                Log.d("Codigo escaneado", scannedCode)
                val token = mainVM.getCuadrillaAccessToken(mainVM.cuadrillaMostrar.value!!.nombre)
                if (scannedCode == token){
                    mainVM.agregarIntegrante(mainVM.currentUser.value!!.username, mainVM.cuadrillaMostrar.value!!.nombre)
                }
                else{
                    Toast.makeText(this, "QR incorrecto, intentalo de nuevo!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else{
            Toast.makeText(this, "Ha ocurrrido un problema al escanear el código QR, intentalo de nuevo!", Toast.LENGTH_SHORT).show()
        }
    }
     */

    private val mainVM by viewModels<MainVM>()
    private val identVM by viewModels<IdentVM>()
    private val preferencesVM by viewModels<PreferencesViewModel>()

    // Set a CHANNEL_ID
    companion object{
        const val CHANNEL_ID = "FestUpNotifChannel"
    }

    // Para manejar el botón de atrás de Android
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mainVM.retrocesoForzado.value = true
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
    @SuppressLint("CoroutineCreationDuringComposition")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        createNotificationChannel()
        super.onCreate(savedInstanceState)

        setContent {
            BackHandler(onBack = { Log.d("HE VUELTO ATRAS MAL", "MAL MAL MAL") })

            FestUpTheme {
                AskPermissions()
                Principal(mainVM, identVM, preferencesVM)
            }
        }
    }
//    override fun onBackPressed() {
//        super.onBackPressed()
//            if(mainVM.usuarioMostrar.isNotEmpty()){
//                mainVM.usuarioMostrar.removeAt(mainVM.usuarioMostrar.size-1)
//            }
//    }

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
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CAMERA
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

