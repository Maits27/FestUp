package com.gomu.festup.RemoteDatabase

import android.graphics.Bitmap
import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream


import javax.inject.Inject
import javax.inject.Singleton

class UserExistsException : Exception()
class AuthenticationException : Exception()


@Serializable
data class AuthUser(
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
    val nombreCuadrilla: String,
    val idEvento: String
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
    val numeroAsistentes: Int,
    val descripcion: String,
    val localizacion: String
)

private val bearerTokenStorage = mutableListOf<BearerTokens>()


@Serializable
data class TokenInfo(
    @SerialName("token_type") val tokenType: String,
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Int,
)


@Singleton
class AuthClient @Inject constructor() {
    private val httpClient = HttpClient(CIO) {

        expectSuccess = true

        install(ContentNegotiation) { json() }

        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                when {
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Unauthorized -> throw AuthenticationException()
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Conflict -> throw UserExistsException()
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.NotFound -> throw UserExistsException()
                    else -> {
                        exception.printStackTrace()
                        throw exception
                    }
                }
            }
        }
    }

    @Throws(AuthenticationException::class, Exception::class)
    suspend fun authenticate(username: String, password: String) {
        val tokenInfo: TokenInfo = httpClient.submitForm(
            url = "http://34.16.74.167/iniciarSesion",
            formParameters = Parameters.build {
                append("grant_type", "password")
                append("username", username)
                append("password", password)
            }).body()
        bearerTokenStorage.add(BearerTokens(tokenInfo.accessToken, tokenInfo.refreshToken))
    }

    @Throws(UserExistsException::class)
    suspend fun createUser(user: AuthUser) {
        httpClient.post("http://34.16.74.167/createUser") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }

    suspend fun setUserProfile(username: String, image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        Log.d("Image", "size: " + byteArray.size.toString())
        httpClient.submitFormWithBinaryData(
            url = "http://34.16.74.167/setUserProfileImage",
            formData = formData {
                append("image", byteArray, Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=$username.png")
                })
            }
        ) { method = HttpMethod.Put }
    }
}



@Singleton
class HTTPClient @Inject constructor() {

    private val httpClient = HttpClient(CIO) {

        expectSuccess = true

        install(ContentNegotiation) { json() }

        install(Auth) {
            bearer {

                loadTokens { bearerTokenStorage.last() }

                sendWithoutRequest { request -> request.url.host == "34.16.74.167" }

                refreshTokens {

                    val refreshTokenInfo: TokenInfo = client.submitForm(
                        url = "http://34.16.74.167/refresh",
                        formParameters = Parameters.build {
                            append("grant_type", "refresh_token")
                            append("refresh_token", oldTokens?.refreshToken ?: "")
                        }
                    ) { markAsRefreshTokenRequest() }.body()

                    bearerTokenStorage.add(BearerTokens(refreshTokenInfo.accessToken, oldTokens?.refreshToken!!))
                    bearerTokenStorage.last()
                }
            }
        }
    }

    // ---------------------------  USER ------------------------------
    suspend fun getUsuarios(): List<AuthUser> = runBlocking {
        val response = httpClient.get("http://34.16.74.167/getUsers")
        response.body()
    }

    fun getUsuario(username: String): AuthUser = runBlocking {
        Log.d("USUARIO", "get")
        val response = httpClient.get("http://34.16.74.167/getUser?username=$username")
        response.body()
    }

    // ---------------------------  USUARIO ASISTENTE ------------------------------
    suspend fun getUsuariosAsistentes(): List<RemoteUsuarioAsistente> = runBlocking {
        val response = httpClient.get("http://34.16.74.167/getUsuariosAsistentes")
        response.body()
    }
    suspend fun insertUsuarioAsistente(usuarioAsistente: RemoteUsuarioAsistente) = runBlocking {
        httpClient.post("http://34.16.74.167/insertUsuarioAsistente") {
            contentType(ContentType.Application.Json)
            setBody(usuarioAsistente)
        }
    }

    suspend fun deleteUsuarioAsistente(usuarioAsistente: RemoteUsuarioAsistente) = runBlocking {
        httpClient.post("http://34.16.74.167/deleteUsuarioAsistente") {
            contentType(ContentType.Application.Json)
            setBody(usuarioAsistente)
        }
    }


    // ---------------------------  CUADRILLA ------------------------------

    suspend fun getCuadrillas(): List<RemoteCuadrilla> = runBlocking {
        val response = httpClient.get("http://34.16.74.167/getCuadrillas")
        response.body()
    }

    suspend fun insertCuadrilla(cuadrilla: RemoteCuadrilla) = runBlocking {
        httpClient.post("http://34.16.74.167/insertCuadrilla") {
            contentType(ContentType.Application.Json)
            setBody(cuadrilla)
        }
    }

    suspend fun deleteCuadrilla(nombre: String) = runBlocking {
        httpClient.post("http://34.16.74.167/deleteCuadrilla") {
            contentType(ContentType.Application.Json)
            parameter("nombre", nombre)
        }
    }

    fun getCuadrillaAccessToken(nombre: String): String = runBlocking {
        val response = httpClient.get("http://34.16.74.167/getCuadrillaAccessToken?nombre=$nombre")
        response.body()
    }


    // ---------------------------  CUADRILLA ASISTENTE ------------------------------

    suspend fun getCuadrillasAsistentes(): List<RemoteCuadrillaAsistente> = runBlocking {
        val response = httpClient.get("http://34.16.74.167/getCuadrillasAsistentes")
        response.body()
    }
    suspend fun insertCuadrillaAsistente(cuadrillaAsistente: RemoteCuadrillaAsistente) = runBlocking {
        httpClient.post("http://34.16.74.167/insertCuadrillaAsistente") {
            contentType(ContentType.Application.Json)
            setBody(cuadrillaAsistente)
        }
    }

    suspend fun deleteCuadrillaAsistente(cuadrillaAsistente: RemoteCuadrillaAsistente) = runBlocking {
        httpClient.post("http://34.16.74.167/deleteCuadrillaAsistente") {
            contentType(ContentType.Application.Json)
            setBody(cuadrillaAsistente)
        }
    }

    // ---------------------------  EVENTO ------------------------------

    suspend fun getEventos(): List<RemoteEvento> = runBlocking {
        val response = httpClient.get("http://34.16.74.167/getEventos")
        response.body()
    }
    suspend fun insertEvento(evento: RemoteEvento) : RemoteEvento = runBlocking {
        val response = httpClient.post("http://34.16.74.167/insertEvento") {
            contentType(ContentType.Application.Json)
            setBody(evento)
        }
        response.body()
    }
    suspend fun deleteEvento(eventoId: Int) = runBlocking {
        httpClient.post("http://34.16.74.167/deleteEvento") {
            contentType(ContentType.Application.Json)
            parameter("eventoId", eventoId)
        }
    }

    // ---------------------------  INTEGRANTE ------------------------------

    suspend fun getIntegrantes(): List<RemoteIntegrante> = runBlocking {
        val response = httpClient.get("http://34.16.74.167/getIntegrantes")
        response.body()
    }

    suspend fun insertIntegrante(integrante: RemoteIntegrante) = runBlocking {
        httpClient.post("http://34.16.74.167/insertIntegrante") {
            contentType(ContentType.Application.Json)
            setBody(integrante)
        }
    }

    suspend fun deleteIntegrante(integrante: RemoteIntegrante) = runBlocking {
        httpClient.post("http://34.16.74.167/deleteIntegrante") {
            contentType(ContentType.Application.Json)
            setBody(integrante)
        }
    }

    // ---------------------------  SEGUIDORES ------------------------------

    suspend fun getSeguidores(): List<RemoteSeguidor> = runBlocking {
        val response = httpClient.get("http://34.16.74.167/getSeguidores")
        response.body()
    }

    suspend fun insertSeguidor(seguidor: RemoteSeguidor) = runBlocking {
        httpClient.post("http://34.16.74.167/insertSeguidores") {
            contentType(ContentType.Application.Json)
            setBody(seguidor)
        }
    }

    suspend fun deleteSeguidor(seguidor: RemoteSeguidor) = runBlocking {
        httpClient.post("http://34.16.74.167/deleteSeguidor") {
            contentType(ContentType.Application.Json)
            setBody(seguidor)
        }
    }



    // ---------------------------  NOTIFICACIONES ------------------------------
    suspend fun subscribeUser(FCMClientToken: String) {
        httpClient.post("http://34.16.74.167") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("fcm_client_token" to FCMClientToken))
        }
    }

    // ---------------------------  IMAGEN DE PERFIL ------------------------------





    suspend fun setCuadrillaImage(nombre: String, image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        Log.d("Image", byteArray.size.toString())
        httpClient.submitFormWithBinaryData(
            url = "http://34.16.74.167/insertCuadrillaImage",
            formData = formData {
                append("image", byteArray, Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=$nombre.png")
                })
            }
        ) { method = HttpMethod.Put }
    }

    suspend fun setEventoProfileImage(id: String, image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        httpClient.submitFormWithBinaryData(
            url = "http://34.16.74.167/insertEventoImage",
            formData = formData {
                append("image", byteArray, Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=$id.png")
                })
            }
        ) { method = HttpMethod.Put }
    }
}
