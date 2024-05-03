package com.gomu.festup.ui.screens

import android.content.Context
import android.location.Geocoder
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.vm.MainVM
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


// Function to get latitude and longitude coordinates from a given address (String)
fun getLatLngFromAddress(context: Context, mAddress: String): Pair<Double, Double>? {
    val coder = Geocoder(context)
    try {
        val addressList = coder.getFromLocationName(mAddress, 1)
        if (addressList.isNullOrEmpty()) {
            return null
        }
        val location = addressList[0]
        return Pair(location.latitude, location.longitude)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}
@Composable
fun EventsMap(
    navController: NavController,
    mainVM: MainVM
) {
    val context = LocalContext.current
    val eventos = mainVM.getEventos().collectAsState(initial = emptyList())
//
//    val latitude = 0.00
//    val longitude = 0.00

//    val cameraPositionState = rememberCameraPositionState {
//        position =
//            CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 10f)
//    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
//            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            eventos.value.map {
                getLatLngFromAddress(context, it.localizacion)?.let { (latitude, longitude) ->
                    Marker(
                        state = MarkerState(position = LatLng(latitude, longitude)),
                        title = it.nombre,
                        snippet = it.fecha.toString()
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