package com.gomu.festup.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat.setTint
import androidx.navigation.NavController
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.utils.getLatLngFromAddress
import com.gomu.festup.utils.toStringNuestro
import com.gomu.festup.vm.MainVM
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext


@Composable
fun EventsMap(
    navController: NavController,
    mainVM: MainVM
) {
    val context = LocalContext.current

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

    var locations by remember { mutableStateOf<List<EventOnMap>?>(null) }

    LaunchedEffect(Unit) {
        val mappedLocations = withContext(Dispatchers.IO) {
            val eventos = mainVM.getEventos().first()
            eventos.mapNotNull { evento ->
                val location = getLatLngFromAddress(context, evento.localizacion)
                if (location != null) EventOnMap(evento, location)
                else null
            }
        }
        locations = mappedLocations
        Log.d("locations", locations!!.size.toString())
    }

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(isMyLocationEnabled = true)
        )
    }

    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            )
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings =  mapUiSettings
        ) {
            locations?.forEach { location ->
                Marker(
                    state = MarkerState(position = location.location),
                    title = location.evento.nombre,
                    icon = bitmapDescriptorFromVector(
                        LocalContext.current,
                        R.drawable.logoloc_min,
                        size = 120,
                        alpha = 255
                    ),
                    snippet = location.evento.fecha.toStringNuestro(),
                    onInfoWindowClick = {
                        mainVM.eventoMostrar.value = location.evento
                        navController.navigate(AppScreens.Evento.route)
                    }
                )
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
fun bitmapDescriptorFromVector(
    context: Context,
    @DrawableRes vectorResId: Int,
    size: Int? = null,
    alpha: Int = 255,
    color: Int? = null,
): BitmapDescriptor {

    // Load drawable and apply options: alpha, color and size
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!.also {
        if (color != null) setTint(it, color)
        it.alpha = alpha

        it.setBounds(0, 0, size ?: it.intrinsicWidth, size ?: it.intrinsicHeight)
    }

    // Convert to bitmap
    val bitmap = Bitmap.createBitmap(size ?: vectorDrawable.intrinsicWidth, size ?: vectorDrawable.intrinsicHeight, Bitmap.Config.RGBA_F16)
    vectorDrawable.draw(Canvas(bitmap))

    // Convert to BitmapDescriptor and return
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
data class EventOnMap(
    val evento: Evento,
    val location: LatLng
)