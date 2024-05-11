package com.gomu.festup.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.glance.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.vm.MainVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                text = stringResource(id = R.string.nuevo_evento)
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

    var showRefreshButton by remember {
        mutableStateOf(false)
    }

    when (currentDestination?.route) {
        AppScreens.AddEvento.route -> {
            showTopBar = true
            title = stringResource(id = R.string.add_event)
            showPerfil = false
            showBackArrow = true
            showRefreshButton = false
        }
        AppScreens.AddCuadrilla.route -> {
            showTopBar = true
            title = stringResource(id = R.string.add_cuadrilla)
            showPerfil = false
            showBackArrow = true
            showRefreshButton = false
        }
        AppScreens.PerfilYo.route -> {
            showTopBar = true
            showPerfil = false
            title = mainVM.usuarioMostrar.value!!.username
            showBackArrow = true
            showRefreshButton = true
        }
        AppScreens.PerfilCuadrilla.route -> {
            showTopBar = true
            showPerfil = false
            showBackArrow = true
            title = mainVM.cuadrillaMostrar.value!!.nombre
            showRefreshButton = true
        }
        AppScreens.PerfilUser.route -> {
            showTopBar = true
            showPerfil = false
            title = mainVM.usuarioMostrar.value!!.username
            showBackArrow = true
            showRefreshButton = true
        }
        AppScreens.Evento.route -> {
            showTopBar = true
            showPerfil = false
            title = mainVM.eventoMostrar.value!!.nombre
            showBackArrow = true
            showRefreshButton = true
        }
        AppScreens.EditPerfil.route -> {
            showTopBar = true
            showPerfil = false
            title = stringResource(id = R.string.edit_profile)
            showBackArrow = true
            showRefreshButton = false
        }
        AppScreens.Ajustes.route -> {
            showTopBar = true
            showPerfil = false
            title = stringResource(id = R.string.preferences, mainVM.usuarioMostrar.value!!.username)
            showBackArrow = true
            showRefreshButton = false
        }
        else -> {
            title = stringResource(id = R.string.app_name)
            showPerfil = true
            showTopBar = true
            showBackArrow = false
            showRefreshButton = false
        }
    }

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
            if (showRefreshButton) {
                IconButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch{
                            mainVM.actualizarDatos()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.refresh),
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
@Composable
fun BottomBarMainView(
    navController: NavController
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination


    AnimatedVisibility(
        visible = (currentDestination?.route in listOf(
            AppScreens.Feed.route,
            AppScreens.Search.route,
            AppScreens.EventsMap.route,
            AppScreens.EventsList.route
        )),
        enter = slideInVertically(initialOffsetY = { it }) + expandVertically(),
        exit = slideOutVertically(targetOffsetY = { it }) + shrinkVertically()
    ) {

        NavigationBar(
            modifier = Modifier.height(70.dp)
        ) {
            NavigationBarItem(
                selected = currentDestination?.route == AppScreens.Feed.route,
                onClick = { navController.navigate(AppScreens.Feed.route) },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.home),
                        contentDescription = "Home",
                        modifier = Modifier.size(30.dp)
                    )
                }
            )

            NavigationBarItem(
                selected = currentDestination?.route == AppScreens.Search.route,
                onClick = { navController.navigate(AppScreens.Search.route) },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.lupa),
                        contentDescription = "Search",
                        modifier = Modifier.size(30.dp)
                    )
                }
            )

            NavigationBarItem(
                selected = currentDestination?.route == AppScreens.EventsMap.route ||
                        currentDestination?.route == AppScreens.EventsList.route,
                onClick = { navController.navigate(AppScreens.EventsMap.route) },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.party),
                        contentDescription = "Events",
                        modifier = Modifier.size(30.dp)
                    )
                }
            )
        }
    }
}
