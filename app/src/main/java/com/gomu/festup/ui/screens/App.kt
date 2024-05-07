package com.gomu.festup.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gomu.festup.MainActivity
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.components.BottomBarMainView
import com.gomu.festup.ui.components.FloatButton
import com.gomu.festup.ui.components.TopBarMainView
import com.gomu.festup.utils.nuestroLocationProvider
import com.gomu.festup.vm.MainVM
import com.google.android.gms.location.LocationServices


@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    mainNavController: NavController,
    mainVM: MainVM
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            if (navBackStackEntry?.destination?.route == AppScreens.EventsMap.route ||
                navBackStackEntry?.destination?.route == AppScreens.EventsList.route) {
                FloatButton{
                    navController.navigate(AppScreens.AddEvento.route)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        topBar = {
            TopBarMainView(
                navController = navController,
                mainVM = mainVM
            )
        },
        bottomBar = {
            BottomBarMainView(
                navController = navController
            )
        }
    ){ innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = AppScreens.Feed.route
        ) {

            composable(AppScreens.Feed.route) { Feed(navController, mainVM) }
            composable(AppScreens.Search.route) { Search(navController, mainVM) }
            composable(AppScreens.EventsMap.route) { EventsMap(navController, mainVM) }
            composable(AppScreens.EventsList.route) { EventsList(navController, mainVM) }
            composable(AppScreens.Evento.route) { Evento(navController, mainVM) }

            composable(AppScreens.AddCuadrilla.route) { AddCuadrilla(navController, mainVM) }
            composable(AppScreens.AddEvento.route) { AddEvento(navController, mainVM) }
            composable(AppScreens.PerfilYo.route) { PerfilYo(mainNavController, navController, yo = true, mainVM = mainVM) }
            composable(AppScreens.PerfilUser.route) { PerfilYo(mainNavController, navController, yo = false,mainVM = mainVM) }
            composable(AppScreens.PerfilCuadrilla.route) { PerfilCuadrilla(navController, mainVM = mainVM) }
            composable(
                AppScreens.SeguidoresSeguidosList.route + "/{startPage}",
                arguments = listOf(
                    navArgument(name = "startPage") { type = NavType.IntType }
                )
            ) {
                SeguidoresSeguidosList(startPage = it.arguments?.getInt("startPage"), mainVM = mainVM, navController = navController)
            }
//            composable(AppScreens.Ajustes.route) { Ajustes(navController) }
            composable(AppScreens.EditPerfil.route) { EditPerfil(navController, mainVM) }
        }
    }
}

