package com.gomu.festup.data.localDatabase.Entities

import androidx.room.Entity


/**
 * Entidades para la relación entre las entidades de
 * [Cuadrilla] y [Usuario] entre si y entre pares de [Usuario],
 * generando la lista de los integrantes de una [Cuadrilla],
 * y la lista de [Usuario] que tienen alguna relación de seguirse el uno al otro.
 */

@Entity(primaryKeys = ["username", "nombreCuadrilla"])
data class Integrante(
    val username: String,
    val nombreCuadrilla: String
)

@Entity(primaryKeys = ["seguidor", "seguido"])
data class Seguidores(
    val seguidor: String,
    val seguido: String
)
