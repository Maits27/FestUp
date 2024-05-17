package com.gomu.festup.ui.elements.screens

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.elements.components.BottomBarMainView
import com.gomu.festup.ui.elements.components.FloatButton
import com.gomu.festup.ui.elements.components.RailBarMainView
import com.gomu.festup.ui.elements.components.TopBarMainView
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.ui.vm.PreferencesViewModel


@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    mainNavController: NavController,
    mainVM: MainVM,
    preferencesVM: PreferencesViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val routeWithoutArguments = currentDestination?.route?.split("/")?.get(0)

    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    preferencesVM.restartLang(
        preferencesVM.idioma(mainVM.currentUser.value!!.username).collectAsState(
            initial = preferencesVM.currentSetLang).value)

    if (mainVM.retrocesoForzado.value){
        if(routeWithoutArguments == AppScreens.PerfilUser.route || routeWithoutArguments == AppScreens.PerfilYo.route){
            if(mainVM.usuarioMostrar.isNotEmpty()){
                mainVM.usuarioMostrar.removeAt(mainVM.usuarioMostrar.size-1)
            }
        }
        if(routeWithoutArguments == AppScreens.Feed.route) mainNavController.popBackStack()
        else navController.popBackStack()

        mainVM.retrocesoForzado.value = false
    }
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            if (navBackStackEntry?.destination?.route == AppScreens.EventsMap.route ||
                navBackStackEntry?.destination?.route == AppScreens.EventsList.route) {
                FloatButton{
                    navController.navigate(AppScreens.AddEvento.route)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        topBar = {
            if (isVertical || (!isVertical && navBackStackEntry?.destination?.route !in listOf(
                    AppScreens.Feed.route,
                    AppScreens.Search.route,
                    AppScreens.EventsList.route,
                    AppScreens.EventsMap.route))
                ) {
                TopBarMainView(
                    navController = navController,
                    mainVM = mainVM
                )
            }



        },
        bottomBar = {
            if (isVertical){
                BottomBarMainView(
                    navController = navController
                )
            }


        },

        ){ innerPadding ->

        val idioma by preferencesVM.idioma(mainVM.currentUser.value!!.username).collectAsState(initial = preferencesVM.currentSetLang)
        val dark by preferencesVM.darkTheme(mainVM.currentUser.value!!.username).collectAsState(initial = true)
        val receiveNotifications by preferencesVM.receiveNotifications(mainVM.currentUser.value!!.username).collectAsState(initial = true)

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxSize()
        ) {
            if (!isVertical){
                RailBarMainView(navController = navController, mainVM)
            }

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
                ) { Evento(navController, mainVM) }

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
                ) { PerfilYo(navController, preferencesVM, yo = true, mainVM = mainVM) }

                composable(AppScreens.PerfilUser.route,
                    enterTransition = { fadeIn(animationSpec = tween(1000)) },
                    exitTransition = { fadeOut(animationSpec = tween(1000)) }
                ) { PerfilYo(navController, preferencesVM, yo = false,  mainVM = mainVM) }

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
                ) { Ajustes(preferencesVM, mainVM, idioma, dark, receiveNotifications) }

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
}