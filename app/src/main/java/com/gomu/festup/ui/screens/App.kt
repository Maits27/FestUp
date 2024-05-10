package com.gomu.festup.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.gomu.festup.vm.PreferencesViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun App(
    mainNavController: NavController,
    mainVM: MainVM,
    preferencesVM: PreferencesViewModel
) {
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    preferencesVM.restartLang(
        preferencesVM.idioma(mainVM.currentUser.value!!.username).collectAsState(
            initial = preferencesVM.currentSetLang).value)
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
        val idioma by preferencesVM.idioma(mainVM.currentUser.value!!.username).collectAsState(initial = preferencesVM.currentSetLang)
        val dark by preferencesVM.darkTheme(mainVM.currentUser.value!!.username).collectAsState(initial = true)
        val receiveNotifications by preferencesVM.receiveNotifications(mainVM.currentUser.value!!.username).collectAsState(initial = false)

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
            composable(AppScreens.PerfilYo.route) { PerfilYo(mainNavController, navController, preferencesVM, yo = true, receiveNotifications, mainVM = mainVM) }
            composable(AppScreens.PerfilUser.route) { PerfilYo(mainNavController, navController, preferencesVM, yo = false, receiveNotifications, mainVM = mainVM) }
            composable(AppScreens.PerfilCuadrilla.route) { PerfilCuadrilla(navController, mainVM = mainVM) }
            composable(
                AppScreens.SeguidoresSeguidosList.route + "/{startPage}",
                arguments = listOf(
                    navArgument(name = "startPage") { type = NavType.IntType }
                )
            ) {
                SeguidoresSeguidosList(startPage = it.arguments?.getInt("startPage"), mainVM = mainVM, navController = navController)
            }
            composable(AppScreens.Ajustes.route) { Ajustes(preferencesVM, mainVM, idioma, dark, receiveNotifications) }
            composable(AppScreens.EditPerfil.route) { EditPerfil(navController, mainVM) }

        }
    }
}

