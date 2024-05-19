package com.gomu.festup.ui.elements.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
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
import com.gomu.festup.R
import com.gomu.festup.data.EventOnMap
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.utils.getLatLngFromAddress
import com.gomu.festup.utils.toStringNuestro
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

/**
 * Mapa con los eventos disponibles en la aplicación marcados con iconos.
 */
@Composable
fun EventsMap(
    navController: NavController,
    mainVM: MainVM
) {
    val context = LocalContext.current

    val miLocalizacion = mainVM.localizacion.value

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
                        size = 200,
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

// Generador de los iconos en los marker
fun bitmapDescriptorFromVector(
    context: Context,
    @DrawableRes vectorResId: Int,
    size: Int? = null,
    alpha: Int = 255,
    color: Int? = null,
): BitmapDescriptor {

    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!.also {
        if (color != null) setTint(it, color)
        it.alpha = alpha

        it.setBounds(0, 0, size ?: it.intrinsicWidth, size ?: it.intrinsicHeight)
    }

    val bitmap = Bitmap.createBitmap(size ?: vectorDrawable.intrinsicWidth, size ?: vectorDrawable.intrinsicHeight, Bitmap.Config.RGBA_F16)
    vectorDrawable.draw(Canvas(bitmap))

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
