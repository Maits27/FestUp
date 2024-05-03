package com.gomu.festup.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.vm.MainVM

@Composable
fun FloatButton(onClick: () -> Unit){
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = {
            Icon(
                painterResource(id = R.drawable.add),
                contentDescription = null
            )
        },
        text = {
            Text(
                text = "Nuevo evento"
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarMainView(
    navController: NavController,
    mainVM: MainVM
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    // TODO: Cambiar dependiendo de la ruta

    var title by remember {
        mutableStateOf("")
    }

    var showPerfil by remember {
        mutableStateOf(false)
    }

    var showTopBar by remember {
        mutableStateOf(false)
    }

    when (currentDestination?.route) {
        AppScreens.AddEvento.route -> {
            showTopBar = true
            title = "Añadir evento"
            showPerfil = false
        }
        AppScreens.AddCuadrilla.route -> {
            showTopBar = true
            title = "Añadir cuadrilla"
            showPerfil = false
        }
        AppScreens.PerfilYo.route -> {
            showTopBar = false
        }
        AppScreens.PerfilCuadrilla.route -> {
            showTopBar = false
        }
        AppScreens.PerfilUser.route -> {
            showTopBar = false
        }
        AppScreens.Evento.route -> {
            showTopBar = true
            showPerfil = false
            title = mainVM.eventoMostrar.value!!.nombre
        }
        else -> {
            title = stringResource(id = R.string.app_name)
            showPerfil = true
            showTopBar = true
        }
    }

    if (showTopBar) {
        TopAppBar(
            title = {
                Text(text = title)
            },
            actions = {
                if (showPerfil) {
                    IconButton(onClick = { navController.navigate(AppScreens.PerfilYo.route) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.account),
                            contentDescription = "",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
            },
        )
    }
}
@Composable
fun BottomBarMainView(
    navController: NavController
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    // TODO: Cambiar dependiendo de la ruta

    if (
        currentDestination?.route == AppScreens.Feed.route ||
        currentDestination?.route == AppScreens.Search.route ||
        currentDestination?.route == AppScreens.EventsMap.route ||
        currentDestination?.route == AppScreens.EventsList.route) {
        NavigationBar(
            modifier = Modifier.height(70.dp)
        ) {
            NavigationBarItem(
                selected = currentDestination.route == AppScreens.Feed.route,
                onClick = { navController.navigate(AppScreens.Feed.route) },
                icon = { Icon(painter = painterResource(id = R.drawable.home), contentDescription = "Home", modifier = Modifier.size(30.dp)) }
            )

            NavigationBarItem(
                selected = currentDestination.route == AppScreens.Search.route,
                onClick = { navController.navigate(AppScreens.Search.route) },
                icon = { Icon(painter = painterResource(id = R.drawable.lupa), contentDescription = "Search", modifier = Modifier.size(30.dp)) }
            )

            NavigationBarItem(
                selected = currentDestination.route == AppScreens.EventsMap.route ||
                           currentDestination.route == AppScreens.EventsList.route,
                onClick = { navController.navigate(AppScreens.EventsMap.route) },
                icon = { Icon(painter = painterResource(id = R.drawable.party), contentDescription = "Events", modifier = Modifier.size(30.dp)) }
            )
        }
    }
}
