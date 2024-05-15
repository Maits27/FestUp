package com.gomu.festup.data.localDatabase.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity
data class Usuario(
    @PrimaryKey val username: String,
    val email: String,
    val nombre: String,
    val fechaNacimiento: Date,
    val telefono: String,
)

@Entity
data class Cuadrilla(
    @PrimaryKey val nombre: String,
    val descripcion: String,
    val lugar: String
)

@Entity
data class Evento(
    @PrimaryKey val id: String,
    val nombre: String,
    val fecha: Date,
    val descripcion: String,
    val localizacion: String
)



