package com.gomu.festup.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gomu.festup.R
import com.gomu.festup.data.Diseño

@Composable
fun FloatButton(onClick: () -> Unit){
    FloatingActionButton(
        onClick = { onClick() }
    ) {
        Icon(painterResource(id = R.drawable.add), "")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarMainView(
    navController: NavController,
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // TODO: Cambiar dependiendo de la ruta
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        actions = {
            IconButton(onClick = { navController.navigate(AppScreens.PerfilYo.route) }) {
                Icon(
                    painter = painterResource(id = R.drawable.account),
                    contentDescription = ""
                )
            }
        },
    )

}
@Composable
fun BottomBarMainView(
    navController: NavController
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    // TODO: Cambiar dependiendo de la ruta
    NavigationBar {
        val items = listOf(
            Diseño(AppScreens.Feed, painterResource(id = R.drawable.home)),
            Diseño(AppScreens.Search, painterResource(id = R.drawable.lupa)),
            Diseño(AppScreens.EventsMap, painterResource(id = R.drawable.party)),
        )

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icono, contentDescription = null, tint = Color.White) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.pantalla.route } == true,
                onClick = {
                    navController.navigate(screen.pantalla.route)
                }
            )
        }

    }
}

