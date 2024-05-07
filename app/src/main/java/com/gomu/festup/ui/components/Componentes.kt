package com.gomu.festup.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

    var showBackArrow by remember {
        mutableStateOf(false)
    }

    when (currentDestination?.route) {
        AppScreens.AddEvento.route -> {
            showTopBar = true
            title = "Añadir evento"
            showPerfil = false
            showBackArrow = true
        }
        AppScreens.AddCuadrilla.route -> {
            showTopBar = true
            title = "Añadir cuadrilla"
            showPerfil = false
            showBackArrow = true
        }
        AppScreens.PerfilYo.route -> {
            showTopBar = true
            showPerfil = false
            title = mainVM.usuarioMostrar.value!!.username
            showBackArrow = true
        }
        AppScreens.PerfilCuadrilla.route -> {
            showTopBar = true
            showPerfil = false
            showBackArrow = true
            title = mainVM.cuadrillaMostrar.value!!.nombre
        }
        AppScreens.PerfilUser.route -> {
            showTopBar = true
            showPerfil = false
            title = mainVM.usuarioMostrar.value!!.username
            showBackArrow = true
        }
        AppScreens.Evento.route -> {
            showTopBar = true
            showPerfil = false
            title = mainVM.eventoMostrar.value!!.nombre
            showBackArrow = true
        }
        AppScreens.EditPerfil.route -> {
            showTopBar = true
            showPerfil = false
            title = "Editar perfil"
            showBackArrow = true
        }
        else -> {
            title = stringResource(id = R.string.app_name)
            showPerfil = true
            showTopBar = true
            showBackArrow = false
        }
    }

    if (showTopBar) {
        TopAppBar(
            title = {
                Text(text = title)
            },
            actions = {
                if (showPerfil) {
                    IconButton(onClick = { mainVM.usuarioMostrar.value=mainVM.currentUser.value;navController.navigate(AppScreens.PerfilYo.route) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.account),
                            contentDescription = "",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
            },
            navigationIcon = {
                if (showBackArrow) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back arrow")
                    }
                }
            }
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
