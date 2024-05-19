package com.gomu.festup.data.localDatabase.Entities

import androidx.room.Entity

/**
 * Entidades para la relación entre las entidades de
 * [Cuadrilla] y [Usuario] con [Evento] generando la
 * lista de usuarios y/o cuadrillas asistentes a un evento.
 */

@Entity(primaryKeys = ["username", "idEvento"])
data class UsuariosAsistentes(
    val username: String,
    val idEvento: String
)


@Entity(primaryKeys = ["nombreCuadrilla", "idEvento"])
data class CuadrillasAsistentes(
    val nombreCuadrilla: String,
    val idEvento: String
)
