package com.gomu.festup.utils

import android.content.Context
import android.location.Geocoder
import com.gomu.festup.data.http.RemoteCuadrilla
import com.gomu.festup.data.http.RemoteCuadrillaAsistente
import com.gomu.festup.data.http.RemoteEvento
import com.gomu.festup.data.http.RemoteIntegrante
import com.gomu.festup.data.http.RemoteSeguidor
import com.gomu.festup.data.http.RemoteUsuario
import com.gomu.festup.data.http.RemoteUsuarioAsistente
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.CuadrillasAsistentes
import com.gomu.festup.data.localDatabase.Entities.Evento
import com.gomu.festup.data.localDatabase.Entities.Integrante
import com.gomu.festup.data.localDatabase.Entities.Seguidores
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.data.localDatabase.Entities.UsuariosAsistentes
import com.gomu.festup.ui.elements.widget.EventoWidget
import com.google.android.gms.maps.model.LatLng
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date

private val md = MessageDigest.getInstance("SHA-512")


/**
 * Diferentes funciones para la conversión entre tipos de datos
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


fun getLatLngFromAddress(context: Context, mAddress: String): LatLng? {
    val coder = Geocoder(context)
    try {
        val addressList = coder.getFromLocationName(mAddress, 1)
        if (addressList.isNullOrEmpty()) {
            return null
        }
        val location = addressList[0]
        return LatLng(location.latitude, location.longitude)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}


fun remoteUsuarioToUsuario(remoteUsuario: RemoteUsuario): Usuario {
    return Usuario(
        remoteUsuario.username,
        remoteUsuario.email,
        remoteUsuario.nombre,
        remoteUsuario.fechaNacimiento.formatearFecha(),
        remoteUsuario.telefono
    )
}

fun remotecuadrillaToCuadrilla(remoteCuadrilla: RemoteCuadrilla): Cuadrilla {
    return Cuadrilla(
        remoteCuadrilla.nombre,
        remoteCuadrilla.descripcion,
        remoteCuadrilla.lugar
    )
}


fun remoteEventoToEvento(remoteEvento: RemoteEvento): Evento {
    return Evento(
        remoteEvento.id,
        remoteEvento.nombre,
        remoteEvento.fecha.formatearFecha(),
        remoteEvento.descripcion,
        remoteEvento.localizacion
    )
}

fun remoteUAsistenteToUAsistente(remoteUAsistente: RemoteUsuarioAsistente): UsuariosAsistentes {
    return UsuariosAsistentes(
        remoteUAsistente.username,
        remoteUAsistente.idEvento
    )
}

fun remoteCAsistenteToCAsistente(remoteCAsistente: RemoteCuadrillaAsistente): CuadrillasAsistentes {
    return CuadrillasAsistentes(
        remoteCAsistente.nombre,
        remoteCAsistente.id
    )
}

fun remoteIntegranteToIntegrante(remoteIntegrante: RemoteIntegrante): Integrante {
    return Integrante(
        remoteIntegrante.username,
        remoteIntegrante.nombre
    )
}

fun remoteSeguidorToSeguidor(remoteSeguidor: RemoteSeguidor): Seguidores {
    return Seguidores(
        remoteSeguidor.seguidor,
        remoteSeguidor.seguido
    )
}

fun getWidgetEventos(
    eventos: List<Evento>, numeroAsistentesEvento: (Evento) -> Int
) : List<EventoWidget> {
    return eventos.map {
        EventoWidget(
            nombre = it.nombre,
            fecha = it.fecha.toStringNuestro(),
            numeroAsistentes = numeroAsistentesEvento(it)
        )
    }
}