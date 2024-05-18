package com.gomu.festup.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.gomu.festup.MainActivity
import com.gomu.festup.data.localDatabase.Entities.Evento
import com.gomu.festup.ui.vm.MainVM
import com.google.android.gms.location.LocationServices
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Diferentes funciones utilizadas a lo largo de la aplicación
 */

// Recoge la localización del usuario en el momento de iniciar la app
fun nuestroLocationProvider(context: Context, mainVM: MainVM){
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context as MainActivity)
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        var location = Location("dummyProvider")
        location.latitude = 0.0
        location.longitude = 0.0
        mainVM.localizacion.value = location
    }else{
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                mainVM.localizacion.value = location
            }
    }
}

// Abre WhatsApp para mandar un mensaje a través de un intent implícito (token de cuadrilla)
fun openWhatsApp(token: String, nombre: String, context: Context) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "El codigo para unirte a la cuadrilla $nombre es el siguiente: \n\n $token" )
        intent.`package` = "com.whatsapp"
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

// Abre Telegram para mandar un mensaje a través de un intent implícito (token de cuadrilla)
fun openTelegram(token: String, nombre: String, context: Context) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "El codigo para unirte a la cuadrilla $nombre es el siguiente: \n\n $token" )
        intent.`package` = "org.telegram.messenger"
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

// Hora de alarma
fun getScheduleTime(evento: Evento): LocalDateTime {
    val date = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(evento.fecha.time),
        ZoneId.systemDefault()
    ).toLocalDate()

    var hora = 0
    var mins = LocalDateTime.now().plusMinutes(1).minute
    if (mins == 60){
        hora = 1
        mins = 0
    }
    val scheduleTime = LocalDateTime.of(date.year,
        date.month,
        date.minusDays(1).dayOfMonth,
        LocalDateTime.now().hour + hora,
        mins
    )

    return scheduleTime
}

/**
 * Formato del teléfono correcto
 */
fun formatPhone(number: String): String {
    return if (number.startsWith("+34")) {
        number.substring(4).replace(" ", "")
    }
    else{
        number.replace(" ", "")
    }
}

