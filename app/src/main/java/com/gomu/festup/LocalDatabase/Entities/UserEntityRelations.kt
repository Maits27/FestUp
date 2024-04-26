package com.gomu.festup.LocalDatabase.Entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation



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