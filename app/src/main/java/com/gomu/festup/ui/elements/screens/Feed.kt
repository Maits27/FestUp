package com.gomu.festup.ui.elements.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomu.festup.R
import com.gomu.festup.data.UserCuadrillaAndEvent
import com.gomu.festup.ui.elements.components.cards.EventoCardConUser
import com.gomu.festup.ui.vm.MainVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Pantalla con la información relativa al usuario. Cuenta con dos partes:
 *      - Eventos del usuario
 *      - Eventos de las personas que sigue el usuario y sus cuadrillas.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Feed(
    navController: NavController,
    mainVM: MainVM
    ) {

    val eventos = mainVM.eventosUsuarioConUser(mainVM.currentUser.value!!).collectAsState(initial = emptyList())
    val seguidos = mainVM.eventosSeguidos(mainVM.currentUser.value!!).collectAsState(initial = emptyList())

    var refresh by remember{ mutableStateOf(false) }

    val refreshState = rememberPullRefreshState(
        refreshing = refresh,
        onRefresh = {
            CoroutineScope(Dispatchers.IO).launch{
                refresh = true
                mainVM.actualizarDatos()
                refresh = false
            }
        }
    )

    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Column (
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    )
    {
        TabRow(
            mainVM.selectedTabFeed.value

        ) {
            Tab(
                selected = mainVM.selectedTabFeed.value == 0,
                onClick = { mainVM.selectedTabFeed.value = 0 },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.mis_eventos),
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
            Tab(
                selected = mainVM.selectedTabFeed.value == 1,
                onClick = { mainVM.selectedTabFeed.value = 1 },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.interesarte),
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .pullRefresh(refreshState).padding(horizontal = if (isVertical) 0.dp else 60.dp),
            contentAlignment = Alignment.Center,
        ) {


            when (mainVM.selectedTabFeed.value) {
                0 -> {
                    EventosList(eventos.value, mainVM, navController)
                }

                1 -> {
                    EventosList(seguidos.value, mainVM, navController)
                }
            }

            PullRefreshIndicator(
                refreshing = refresh,
                state = refreshState,
                modifier = Modifier.align(
                    Alignment.TopCenter,
                ),
            )
        }
    }
}

/**
 * Estructura del listado de los eventos
 */
@Composable
fun EventosList(eventos: List<UserCuadrillaAndEvent>, mainVM: MainVM, navController: NavController) {
    if (eventos.isNotEmpty()) {
        LazyColumn {
            items(eventos) { evento ->
                EventoCardConUser(eventoUser = evento, mainVM, navController)
            }
        }
    }
    else {
        Text(
            text = stringResource(id = R.string.no_events),
            modifier = Modifier.padding(top = 20.dp),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        )
    }
}

