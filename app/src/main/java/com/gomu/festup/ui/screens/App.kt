package com.gomu.festup.ui.screens

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.components.BottomBarMainView
import com.gomu.festup.ui.components.FloatButton
import com.gomu.festup.ui.components.TopBarMainView
import com.gomu.festup.vm.MainVM
import com.gomu.festup.vm.PreferencesViewModel


@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    mainNavController: NavController,
    mainVM: MainVM,
    preferencesVM: PreferencesViewModel,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val routeWithoutArguments = currentDestination?.route?.split("/")?.get(0)
    val goBack:() -> Unit = {
        Log.d("ON BACK", "ME VOY PARA ATRAS")
        if(routeWithoutArguments == AppScreens.PerfilUser.route || routeWithoutArguments == AppScreens.PerfilYo.route){
            if(mainVM.usuarioMostrar.isNotEmpty()){
                mainVM.usuarioMostrar.removeAt(mainVM.usuarioMostrar.size-1)
            }
        }
        navController.popBackStack()
    }
    BackHandler(onBack = goBack)

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    preferencesVM.restartLang(
        preferencesVM.idioma(mainVM.currentUser.value!!.username).collectAsState(
            initial = preferencesVM.currentSetLang).value)
    Log.d("LAST LOGGED USER DENTRO", preferencesVM.lastLoggedUser)
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
                mainVM = mainVM,
                goBack = goBack
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
        val showAge by preferencesVM.mostrarEdad(mainVM.currentUser.value!!.username).collectAsState(initial = false)
        val showAgeOther by preferencesVM.mostrarEdad(if (mainVM.usuarioMostrar.isEmpty()) "" else mainVM.usuarioMostrar.last()?.username?:"").collectAsState(initial = false)

        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = AppScreens.Feed.route
        ) {

            composable(AppScreens.Feed.route,
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }
            ) { Feed(navController, mainVM) }

            composable(AppScreens.Search.route,
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }
            ) { Search(navController, mainVM) }

            composable(AppScreens.EventsMap.route,
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }
            ) { EventsMap(navController, mainVM) }

            composable(AppScreens.EventsList.route,
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }
            ) { EventsList(navController, mainVM) }

            composable(AppScreens.Evento.route,
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }
            ) { Evento(navController, mainVM, receiveNotifications) }

            composable(AppScreens.AddCuadrilla.route,
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }
            ) { AddCuadrilla(navController, mainVM) }

            composable(AppScreens.AddEvento.route,
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }
            ) { AddEvento(navController, mainVM) }

            composable(AppScreens.PerfilYo.route,
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }
            ) { PerfilYo(mainNavController, navController, preferencesVM, yo = true,
                receiveNotifications, showAge, mainVM = mainVM) }

            composable(AppScreens.PerfilUser.route,
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }
            ) { PerfilYo(mainNavController, navController, preferencesVM, yo = false,
                receiveNotifications, showAgeOther, mainVM = mainVM) }

            composable(AppScreens.PerfilCuadrilla.route,
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }
            ) { PerfilCuadrilla(navController, mainVM = mainVM) }

            composable(
                AppScreens.SeguidoresSeguidosList.route + "/{startPage}",
                arguments = listOf(
                    navArgument(name = "startPage") { type = NavType.IntType }
                ),
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }

            ) {
                SeguidoresSeguidosList(startPage = it.arguments?.getInt("startPage"), mainVM = mainVM, navController = navController)
            }

            composable(AppScreens.Ajustes.route,
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }
            ) { Ajustes(preferencesVM, mainVM, idioma, dark, receiveNotifications, showAge) }

            composable(AppScreens.EditPerfil.route,
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }
            ) { EditPerfil(navController, mainVM) }

            composable(AppScreens.FullImageScreen.route + "/{type}/{filename}",
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }
            ) { backStackEntry ->
                val type = backStackEntry.arguments?.getString("type")
                val filename = backStackEntry.arguments?.getString("filename")
                if (filename != null && type != null) {
                    FullImageScreen(type, filename)
                }
            }
            composable(AppScreens.BuscarAmigos.route,
                enterTransition = { fadeIn(animationSpec = tween(1000)) },
                exitTransition = { fadeOut(animationSpec = tween(1000)) }
            ) {
                BuscarAmigos(mainVM, navController)
            }
        }
    }
}

