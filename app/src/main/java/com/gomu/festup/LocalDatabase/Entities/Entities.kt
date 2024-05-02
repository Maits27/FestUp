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
    val password: String,
    val email: String,
    val nombre: String,
    val fechaNacimiento: Date,
    val profileImagePath: String
)

@Entity
data class Cuadrilla(
    @PrimaryKey val nombre: String,
    val descripcion: String,
    val lugar: String,
    val profileImagePath: String,
    val accessToken: Int = randomNum()
)

@Entity
data class Evento(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val fecha: Date,
    val numeroAsistentes: Int,
    val descripcion: String,
    val localizacion: String,
    val eventoImagePath: String
)



