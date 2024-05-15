package com.gomu.festup.data.http

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteUsuario(
    val username: String,
    val email: String,
    val nombre: String,
    val fechaNacimiento: String,
    val telefono: String
)
@Serializable
data class RemoteAuthUsuario(
    val username: String,
    val password: String,
    val email: String,
    val nombre: String,
    val fechaNacimiento: String
)

@Serializable
data class RemoteCuadrilla(
    val nombre: String,
    val accessToken: String,
    val descripcion: String,
    val lugar: String
)

@Serializable
data class RemoteUsuarioAsistente(
    val username: String,
    val idEvento: String
)

@Serializable
data class RemoteCuadrillaAsistente(
    val nombre: String,
    val id: String
)

@Serializable
data class RemoteIntegrante(
    val username: String,
    val nombre: String
)

@Serializable
data class RemoteSeguidor(
    val seguidor: String,
    val seguido: String
)

@Serializable
data class RemoteEvento(
    val id: String,
    val nombre: String,
    val fecha: String,
    val descripcion: String,
    val localizacion: String
)

@Serializable
data class TokenInfo(
    @SerialName("token_type") val tokenType: String,
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Int,
)