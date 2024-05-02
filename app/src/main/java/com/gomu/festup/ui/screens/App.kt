package com.gomu.festup.ui.screens

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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.BottomBarMainView
import com.gomu.festup.ui.FloatButton
import com.gomu.festup.ui.TopBarMainView
import com.gomu.festup.vm.MainVM


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    mainNavController: NavController,
    mainVM: MainVM
) {
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
            composable(AppScreens.Feed.route) { Feed(navController) }
            composable(AppScreens.Search.route) { Search(navController, mainVM) }
            composable(AppScreens.EventsMap.route) { EventsMap(navController) }
            composable(AppScreens.EventsList.route) { EventsList(navController, mainVM) }
            composable(AppScreens.Evento.route) { Evento(navController, mainVM) }

            composable(AppScreens.AddCuadrilla.route) { AddCuadrilla(navController, mainVM) }
            composable(AppScreens.AddEvento.route) { AddEvento(navController) }

            composable(AppScreens.PerfilYo.route) { PerfilYo(mainNavController, navController, yo = true, mainVM = mainVM) }
            composable(AppScreens.PerfilUser.route) { PerfilYo(mainNavController, navController, yo = false,mainVM = mainVM) }
            composable(AppScreens.PerfilCuadrilla.route) { PerfilCuadrilla(navController, mainVM = mainVM) }

//            composable(AppScreens.Ajustes.route) { Ajustes(navController) }
//            composable(AppScreens.EditPerfil.route) { EditPerfil(navController) }
        }



    }
}