package com.gomu.festup.LocalDatabase.Entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date
import java.util.UUID


@Entity
data class Usuario(
    @PrimaryKey val username: String,
    val password: String,
    val email: String,
    val nombre: String
)

@Entity
data class Cuadrilla(
    @PrimaryKey val nombre: String,
    val descripcion: String,
    val lugar: String
)


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
@Entity
data class Evento(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val fecha: Date,
    val numeroAsistentes: Int,
    val descripcion: String,
    val localizacion: String
)

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