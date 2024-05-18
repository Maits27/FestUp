package com.gomu.festup.ui.elements.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.vm.MainVM


/* EN ESTE FICHERO SE DEFINEN LOS COMPOSABLES QUE SON UTILIZADO EN MAS DE UNA PANTALLA */



// Botón flotante que sirve para añadir nuevos eventos
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


// Barra superior de la aplicación que se muestra en muchas de las pantallas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarMainView(
    navController: NavController,
    mainVM: MainVM,
){

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val routeWithoutArguments = currentDestination?.route?.split("/")?.get(0)

    var title by remember {
        mutableStateOf("")
    }

    var showPerfil by remember {
        mutableStateOf(false)
    }

    var showAmigos by remember {
        mutableStateOf(false)
    }

    var showTopBar by remember {
        mutableStateOf(false)
    }

    var showBackArrow by remember {
        mutableStateOf(false)
    }

    // En funcion de la ruta en la que el usuario este se deben mostrar unos elementos u otros en la barra
    // Este when gestiona todos los casos posibles y adapta la barra en funcion de la ruta donde se encuentre
    when (routeWithoutArguments) {
        AppScreens.AddEvento.route -> {
            showTopBar = true
            title = stringResource(id = R.string.add_event)
            showPerfil = false
            showAmigos = false
            showBackArrow = true
        }
        AppScreens.AddCuadrilla.route -> {
            showTopBar = true
            title = stringResource(id = R.string.add_cuadrilla)
            showPerfil = false
            showAmigos = false
            showBackArrow = true
        }
        AppScreens.PerfilYo.route -> {
            showTopBar = true
            showPerfil = false
            showAmigos = true
            title = mainVM.currentUser.value!!.username
            showBackArrow = true
        }
        AppScreens.PerfilCuadrilla.route -> {
            showTopBar = true
            showPerfil = false
            showAmigos = false
            showBackArrow = true
            title = mainVM.cuadrillaMostrar.value!!.nombre
        }
        AppScreens.PerfilUser.route -> {
            showTopBar = true
            showPerfil = false
            showAmigos = false
            title = mainVM.usuarioMostrar.last()!!.username
            showBackArrow = true
        }
        AppScreens.Evento.route -> {
            showTopBar = true
            showPerfil = false
            showAmigos = false
            title = mainVM.eventoMostrar.value!!.nombre
            showBackArrow = true
        }
        AppScreens.EditPerfil.route -> {
            showTopBar = true
            showPerfil = false
            showAmigos = false
            title = stringResource(id = R.string.edit_profile)
            showBackArrow = true
        }
        AppScreens.Ajustes.route -> {
            showTopBar = true
            showPerfil = false
            showAmigos = false
            title = stringResource(id = R.string.preferences, mainVM.currentUser.value!!.username)
            showBackArrow = true
        }
        AppScreens.SeguidoresSeguidosList.route -> {
            showTopBar = true
            showPerfil = false
            showAmigos = false
            title = mainVM.usuarioMostrar.last()!!.username
            showBackArrow = true
        }
        AppScreens.FullImageScreen.route -> {
            showTopBar = true
            showPerfil = false
            showAmigos = false
            val type = currentDestination.route?.split("/")?.get(1)
            when (type) {
                "user" -> title = mainVM.usuarioMostrar.last()!!.username
                "cuadrilla" -> title = mainVM.cuadrillaMostrar.value!!.nombre
                "evento" -> title = mainVM.eventoMostrar.value!!.nombre
            }
            showBackArrow = true
        }
        AppScreens.BuscarAmigos.route -> {
            showTopBar = true
            showPerfil = false
            showAmigos = false
            title = stringResource(id = R.string.buscar_amigos)
            showBackArrow = true
        }
        else -> {
            title = stringResource(id = R.string.app_name)
            showPerfil = true
            showAmigos = false
            showTopBar = true
            showBackArrow = false
        }
    }

    // Se define el elemento TopAppBar siguiendo los atributos que marque en funcion de la direccion actual
    TopAppBar(
        title = {
            if (title == stringResource(id = R.string.app_name)){
                Image(
                    painter = painterResource(id = R.drawable.festup),
                    contentDescription = "Logo-FestUp",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            } else Text(text = title)
        },
        actions = {
            if (showPerfil) {
                IconButton(onClick = {
                    mainVM.usuarioMostrar.add(mainVM.currentUser.value)
                    navController.navigate(AppScreens.PerfilYo.route)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.account),
                        contentDescription = "",
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
            else if (showAmigos) {
                IconButton(onClick = {
                    navController.navigate(AppScreens.BuscarAmigos.route)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_people_24),
                        contentDescription = "",
                        modifier = Modifier.size(27.dp)
                    )
                }
                IconButton(onClick = {
                    navController.navigate(AppScreens.Ajustes.route)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings),
                        contentDescription = "",
                        modifier = Modifier.size(27.dp)
                    )
                }
            }
        },
        navigationIcon = {
            if (showBackArrow) {
                IconButton(onClick = {
                    if(routeWithoutArguments == AppScreens.PerfilUser.route || routeWithoutArguments == AppScreens.PerfilYo.route){
                        if(mainVM.usuarioMostrar.isNotEmpty()){
                            mainVM.usuarioMostrar.removeAt(mainVM.usuarioMostrar.size-1)
                        }
                    }
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back arrow")
                }
            }
        }
    )
}

// Funcion que define la barra de navegacion inferior
@Composable
fun BottomBarMainView(
    navController: NavController
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Se definen las rutas en las que debe aparecer la barra de navegacion inferior junto con la animacion de entrada y salida
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

        // Se define el componente NavigationBar con todos los items que son necesarios para navegar por la aplicacion
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


// Funcion que define la barra de navegacion lateral que se emplea en la orientacion horizontal
@Composable
fun RailBarMainView(
    navController: NavController,mainVM: MainVM
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var showPerfil by remember {
        mutableStateOf(false)
    }
    val routeWithoutArguments = currentDestination?.route?.split("/")?.get(0)

    // Se definen cuales son los elementos que debe tener la barra en funcion de la direccion
    when (routeWithoutArguments) {
        AppScreens.AddEvento.route -> {
            showPerfil = false
        }
        AppScreens.AddCuadrilla.route -> {
            showPerfil = false
        }
        AppScreens.PerfilYo.route -> {
            showPerfil = false
        }
        AppScreens.PerfilCuadrilla.route -> {
            showPerfil = false
        }
        AppScreens.PerfilUser.route -> {
            showPerfil = false
        }
        AppScreens.Evento.route -> {
            showPerfil = false
        }
        AppScreens.EditPerfil.route -> {
            showPerfil = false
        }
        AppScreens.Ajustes.route -> {
            showPerfil = false
        }
        AppScreens.SeguidoresSeguidosList.route -> {
            showPerfil = false
        }
        AppScreens.FullImageScreen.route -> {
            showPerfil = false

        }
        AppScreens.BuscarAmigos.route -> {
            showPerfil = false
        }
        else -> {
            showPerfil = true
        }
    }

    AnimatedVisibility(
        visible = (currentDestination?.route in listOf(
            AppScreens.Feed.route,
            AppScreens.Search.route,
            AppScreens.EventsMap.route,
            AppScreens.EventsList.route
        )),
        enter = slideInHorizontally(initialOffsetX = { -it }) + expandHorizontally(),
        exit = slideOutHorizontally(targetOffsetX = { -it }) + shrinkHorizontally(),
        //modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
    ) {

        // Se define el elemento NavigationRail con los componentes definidos mediante el when
        NavigationRail(
            modifier = Modifier.width(80.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                if (showPerfil) {
                    NavigationRailItem(
                        selected = currentDestination?.route == AppScreens.PerfilYo.route,
                        onClick = { mainVM.usuarioMostrar.add(mainVM.currentUser.value)
                            navController.navigate(AppScreens.PerfilYo.route) },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.account),
                                contentDescription = "Perfil",
                                modifier = Modifier.size(28.dp)
                            )
                        },
                    )
                }
                Spacer(modifier = Modifier.weight(1f))


                NavigationRailItem(
                    modifier = Modifier.padding(vertical = 4.dp),
                    selected = currentDestination?.route == AppScreens.Feed.route,
                    onClick = { navController.navigate(AppScreens.Feed.route) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Home",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                )

                NavigationRailItem(
                    modifier = Modifier.padding(vertical = 4.dp),
                    selected = currentDestination?.route == AppScreens.Search.route,
                    onClick = { navController.navigate(AppScreens.Search.route) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.lupa),
                            contentDescription = "Search",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                )

                NavigationRailItem(
                    modifier = Modifier.padding(vertical = 4.dp),
                    selected = currentDestination?.route == AppScreens.EventsMap.route ||
                            currentDestination?.route == AppScreens.EventsList.route,
                    onClick = { navController.navigate(AppScreens.EventsMap.route) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.party),
                            contentDescription = "Events",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                )
            }
        }
    }
}