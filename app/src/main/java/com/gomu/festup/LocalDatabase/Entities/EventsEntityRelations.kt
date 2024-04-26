package com.gomu.festup.LocalDatabase.Entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation




@Entity(primaryKeys = ["username", "idEvento"])
data class UsuariosAsistentes(
    val username: String,
    val idEvento: String
)

data class UsuarioWithEventos(
    @Embedded
    var usuario: Usuario,
    @Relation(
        parentColumn = "username",
        entity = Evento::class,
        entityColumn = "id",
        associateBy = Junction(
            value = UsuariosAsistentes::class,
            parentColumn = "username",
            entityColumn = "idEvento"
        )
    )
    var eventos: List<Evento>
)


@Entity(primaryKeys = ["nombreCuadrilla", "idEvento"])
data class CuadrillasAsistentes(
    val nombreCuadrilla: String,
    val idEvento: String
)

data class CuadrillaWithEventos(
    @Embedded
    var cuadrilla: Cuadrilla,
    @Relation(
        parentColumn = "nombre",
        entity = Evento::class,
        entityColumn = "id",
        associateBy = Junction(
            value = CuadrillasAsistentes::class,
            parentColumn = "nombreCuadrilla",
            entityColumn = "idEvento"
        )
    )
    var eventos: List<Evento>
)