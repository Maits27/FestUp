package com.gomu.festup.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.gomu.festup.data.localDatabase.Entities.Evento
import java.time.LocalDateTime

/**
 * DataClass para almacenar los datos a enviar al [AlarmManager]
 */
data class AlarmItem(
    val alarmTime : LocalDateTime,
    val eventTitle : String,
    val eventLocation: String,
    val eventId: String
)

/**
 * DataClasses para gestionar las entidades recogidas de la base de datos
 */
data class UserAndEvent(
    @ColumnInfo(name = "username")
    val username: String,
    @Embedded
    val evento: Evento
)
data class CuadrillaAndEvent(
    @ColumnInfo(name = "nombreCuadrilla")
    val nombreCuadrilla: String,
    @Embedded
    val evento: Evento
)

data class UserCuadrillaAndEvent(
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "nombreCuadrilla")
    val nombreCuadrilla: String,
    @Embedded
    val evento: Evento
)

data class Contacto(
    val nombre: String,
    val telefono: String,
)

/**
 * DataClass con los idiomas que recoge la aplicación
 */
enum class AppLanguage(val language: String, val code: String) {
    EU("Euskera", "eu"),
    ES("Español", "es");

    companion object {
        /**
         * Obtener [AppLanguage] De un código.
         * @code puede ser: 'eu', 'es'
         */
        fun getFromCode(code: String) = when (code) {
            EU.code -> EU
            ES.code -> ES
            else -> ES
        }
    }
}

