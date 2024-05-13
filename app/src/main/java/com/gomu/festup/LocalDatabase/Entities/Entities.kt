package com.gomu.festup.LocalDatabase.Entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.gomu.festup.utils.randomNum
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID


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



