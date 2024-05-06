package com.gomu.festup.utils

import android.content.Context
import android.location.Geocoder
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.CuadrillasAsistentes
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Integrante
import com.gomu.festup.LocalDatabase.Entities.Seguidores
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.LocalDatabase.Entities.UsuariosAsistentes
import com.gomu.festup.RemoteDatabase.RemoteCuadrilla
import com.gomu.festup.RemoteDatabase.RemoteCuadrillaAsistente
import com.gomu.festup.RemoteDatabase.RemoteEvento
import com.gomu.festup.RemoteDatabase.RemoteIntegrante
import com.gomu.festup.RemoteDatabase.RemoteSeguidor
import com.gomu.festup.RemoteDatabase.RemoteUsuario
import com.gomu.festup.RemoteDatabase.RemoteUsuarioAsistente
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Random;

private val md = MessageDigest.getInstance("SHA-512")


/**
 * Comparar hash con [String] y conversor de [String] a hash
 */

fun String.hash(): String{
    val messageDigest = md.digest(this.toByteArray())
    val no = BigInteger(1, messageDigest)
    var hashText = no.toString(16)
    while (hashText.length < 32) {
        hashText = "0$hashText"
    }
    return hashText
}

fun String.compareHash(hash:String): Boolean{
    return this.hash() == hash
}

fun randomNum(): Int{
    val random = Random()
    return random.nextInt(9000000) + 1000000
}

fun String.formatearFecha(): Date {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    val fechaFormateada: Date = dateFormat.parse(this)
    return fechaFormateada
}

fun Date.toStringNuestro(): String{
    return SimpleDateFormat("dd/MM/yyyy").format(this)
}

fun String.formatearFechaRemoto(): Date {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val fechaFormateada: Date = dateFormat.parse(this)
    return fechaFormateada
}

fun Date.toStringRemoto(): String{
    return SimpleDateFormat("yyyy-MM-dd").format(this)
}


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


fun remoteUsuarioToUsuario(remoteUsuario: RemoteUsuario): Usuario{
    return Usuario(
        remoteUsuario.username,
        remoteUsuario.email,
        remoteUsuario.nombre,
        remoteUsuario.fechaNacimiento.formatearFecha()
    )
}

fun remotecuadrillaToCuadrilla(remoteCuadrilla: RemoteCuadrilla): Cuadrilla{
    return Cuadrilla(
        remoteCuadrilla.nombre,
        remoteCuadrilla.descripcion,
        remoteCuadrilla.lugar
    )
}


fun remoteEventoToEvento(remoteEvento: RemoteEvento): Evento{
    return Evento(
        remoteEvento.id,
        remoteEvento.nombre,
        remoteEvento.fecha.formatearFecha(),
        remoteEvento.numeroAsistentes,
        remoteEvento.descripcion,
        remoteEvento.localizacion
    )
}

fun remoteUAsistenteToUAsistente(remoteUAsistente: RemoteUsuarioAsistente): UsuariosAsistentes{
    return UsuariosAsistentes(
        remoteUAsistente.username,
        remoteUAsistente.idEvento
    )
}

fun remoteCAsistenteToCAsistente(remoteCAsistente: RemoteCuadrillaAsistente): CuadrillasAsistentes{
    return CuadrillasAsistentes(
        remoteCAsistente.nombre,
        remoteCAsistente.id
    )
}

fun remoteIntegranteToIntegrante(remoteIntegrante: RemoteIntegrante): Integrante{
    return Integrante(
        remoteIntegrante.username,
        remoteIntegrante.nombre
    )
}

fun remoteSeguidorToSeguidor(remoteSeguidor: RemoteSeguidor): Seguidores{
    return Seguidores(
        remoteSeguidor.seguidor,
        remoteSeguidor.seguido
    )
}