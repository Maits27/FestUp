package com.gomu.festup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.FestUpTheme
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.screens.App
import com.gomu.festup.ui.screens.LoginPage
import com.gomu.festup.vm.MainVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainVM by viewModels<MainVM>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FestUpTheme {
                // A surface container using the 'background' color from the theme
                Principal(mainVM)
            }
        }
    }
}

@Composable
fun Principal(mainVM: MainVM) {
    val mainNavController = rememberNavController()

    NavHost(
        navController = mainNavController,
        startDestination = AppScreens.LoginPage.route
    ) {
        composable(AppScreens.LoginPage.route) {
            LoginPage(mainNavController, mainVM)
        }
        composable(AppScreens.App.route) {
            App(mainNavController, mainVM)
        }
    }
}

