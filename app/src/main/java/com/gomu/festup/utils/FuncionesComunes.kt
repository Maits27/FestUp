package com.gomu.festup.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.gomu.festup.MainActivity
import com.gomu.festup.vm.MainVM
import com.google.android.gms.location.LocationServices

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

// Function to open whatsapp in order to share a text with the activity's information
fun openShare(token: String, context: Context){
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, token)
        intent.`package` = "com.whatsapp"
        startActivity(context, intent, null)
    } catch (_: Exception) {

    }
}