package com.gomu.festup.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gomu.festup.MainActivity
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.utils.getLatLngFromAddress
import com.gomu.festup.utils.toStringNuestro
import com.gomu.festup.vm.MainVM
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun EventsMap(
    navController: NavController,
    mainVM: MainVM
) {
    val context = LocalContext.current
    val eventos = mainVM.getEventos().collectAsState(initial = emptyList())
    var miLocalizacion = mainVM.localizacion.value

    // TODO: Si se usa elvis no funciona bien
    val cameraPositionState = rememberCameraPositionState {
        if (miLocalizacion != null) {
            position =
                CameraPosition.fromLatLngZoom(LatLng(miLocalizacion.latitude, miLocalizacion.longitude), 10f)
        }else{
            position = CameraPosition.fromLatLngZoom(LatLng(1.0, 1.0), 2f)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            eventos.value.map {
                getLatLngFromAddress(context, it.localizacion)?.let { (latitude, longitude) ->
                    Marker(
                        state = MarkerState(position = LatLng(latitude, longitude)),
                        title = it.nombre,
                        snippet = it.fecha.toStringNuestro()
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate(AppScreens.EventsList.route) },
            shape = RoundedCornerShape(70),
            modifier = Modifier
                .padding(16.dp)
                .size(45.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.list),
                contentDescription = null
            )
        }
    }
}