package com.gomu.festup.data.localDatabase.Entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation


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


data class CuadrillaWithUsuarios(
    @Embedded
    var cuadrilla: Cuadrilla,

    @Relation(
        parentColumn = "nombre",
        entity = Usuario::class,
        entityColumn = "username",
        associateBy = Junction(
            value = Integrante::class,
            parentColumn = "nombreCuadrilla",
            entityColumn = "username"
        )
    )
    var usuario: List<Usuario>
)

@Entity(primaryKeys = ["seguidor", "seguido"])
data class Seguidores(
    val seguidor: String,
    val seguido: String
)

data class UsuarioWithUsuarios(
    @Embedded
    var usuario: Usuario,
    @Relation(
        parentColumn = "username",
        entity = Usuario::class,
        entityColumn = "username",
        associateBy = Junction(
            value = Seguidores::class,
            parentColumn = "seguidor",
            entityColumn = "seguido"
        )
    )
    var seguidos: List<Usuario>
)