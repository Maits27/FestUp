package com.gomu.festup.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun EventsMap(navController: NavController) {

    val latitude = 0.00
    val longitude = 0.00

    val cameraPositionState = rememberCameraPositionState {
        position =
            CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 10f)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false)
        ) {
            Marker(
                state = MarkerState(position = LatLng(latitude, longitude)),
                title = "Location",
                snippet = "Marker at provided address"
            )
            Marker(
                state = MarkerState(position = LatLng(latitude+1.00, longitude + 1.00)),
                title = "Location",
                snippet = "Marker at provided address"
            )
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


@Preview(showSystemUi = true)
@Composable
fun EventsMapPreview() {
    EventsMap(navController = rememberNavController())
}