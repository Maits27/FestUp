package com.gomu.festup.RemoteDatabase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.room.PrimaryKey
import com.gomu.festup.LocalDatabase.Entities.AuthUser
import com.gomu.festup.utils.randomNum
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
import java.util.Date
import java.util.UUID


import javax.inject.Inject
import javax.inject.Singleton

class UserExistsException : Exception()
class AuthenticationException : Exception()

@Serializable
data class TokenInfo(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("token_type") val tokenType: String,
)

@Serializable
data class RemoteCuadrilla(
    val nombre: String,
    val token: Int = randomNum(),
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
    val nombreCuadrilla: String
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
    // val fecha: Date,                TODO PONER FECHA BIEN
    val numeroAsistentes: Int,
    val descripcion: String,
    val localizacion: String
)

private val bearerTokenStorage = mutableListOf<BearerTokens>()



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
                    else -> {
                        exception.printStackTrace()
                        throw exception
                    }
                }
            }
        }
    }

    @Throws(AuthenticationException::class, Exception::class)
    suspend fun authenticate(user: AuthUser) {
        val tokenInfo: TokenInfo = httpClient.submitForm(
            url = "https://XXXX/iniciarSesion",
            formParameters = Parameters.build {
                append("grant_type", "password")
                append("username", user.username)
                append("password", user.password)
            }).body()

        bearerTokenStorage.add(BearerTokens(tokenInfo.accessToken, tokenInfo.refreshToken))
    }

    @Throws(UserExistsException::class)
    suspend fun createUser(user: AuthUser) {
        httpClient.post("https://XXXX/createUser") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
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

                sendWithoutRequest { request -> request.url.host == "XXX" }

                refreshTokens {

                    val refreshTokenInfo: TokenInfo = client.submitForm(
                        url = "https://XXX/refresh",
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
        val response = httpClient.get("http://XXX/getUsers")
        response.body()
    }

    suspend fun getCuadrillasUsuario(username: String): List<RemoteCuadrilla> = runBlocking {
        val response = httpClient.get("http://XXX/getCuadrillasUsuario?username=$username")
        response.body()
    }

    suspend fun getSeguidosUsuario(username: String): List<AuthUser> = runBlocking {
        val response = httpClient.get("http://XXX/getSeguidosUsuario?username=$username")
        response.body()
    }

    suspend fun getSeguidoresUsuario(username: String): List<AuthUser> = runBlocking {
        val response = httpClient.get("http://XXX/getSeguidoresUsuario?username=$username")
        response.body()
    }


    // ---------------------------  USUARIO ASISTENTE ------------------------------

    suspend fun insertUsuarioAsistente(usuarioAsistente: RemoteUsuarioAsistente) = runBlocking {
        httpClient.post("http://XXX/insertUsuarioAsistente") {
            contentType(ContentType.Application.Json)
            setBody(usuarioAsistente)
        }
    }

    suspend fun deleteUsuarioAsistente(usuarioAsistente: RemoteUsuarioAsistente) = runBlocking {
        httpClient.post("http://XXX/deleteUsuarioAsistente") {
            contentType(ContentType.Application.Json)
            setBody(usuarioAsistente)
        }
    }


    // ---------------------------  CUADRILLA ------------------------------

    suspend fun insertCuadrilla(cuadrilla: RemoteCuadrilla) = runBlocking {
        httpClient.post("http://XXX/insertCuadrilla") {
            contentType(ContentType.Application.Json)
            setBody(cuadrilla)
        }
    }

    suspend fun deleteCuadrilla(nombre: String) = runBlocking {
        httpClient.post("http://XXX/deleteCuadrilla") {
            contentType(ContentType.Application.Json)
            parameter("nombre", nombre)
        }
    }

    // ---------------------------  CUADRILLA ASISTENTE ------------------------------

    suspend fun insertCuadrillaAsistente(cuadrillaAsistente: RemoteCuadrillaAsistente) = runBlocking {
        httpClient.post("http://XXX/insertCuadrillaAsistente") {
            contentType(ContentType.Application.Json)
            setBody(cuadrillaAsistente)
        }
    }

    suspend fun deleteCuadrillaAsistente(cuadrillaAsistente: RemoteCuadrillaAsistente) = runBlocking {
        httpClient.post("http://XXX/deleteCuadrillaAsistente") {
            contentType(ContentType.Application.Json)
            setBody(cuadrillaAsistente)
        }
    }

    // ---------------------------  EVENTO ------------------------------

    suspend fun insertEvento(evento: RemoteEvento) = runBlocking {
        httpClient.post("http://XXX/insertEvento") {
            contentType(ContentType.Application.Json)
            setBody(evento)
        }
    }

    suspend fun deleteEvento(eventoId: Int) = runBlocking {
        httpClient.post("http://XXX/deleteEvento") {
            contentType(ContentType.Application.Json)
            parameter("eventoId", eventoId)
        }
    }

    // ---------------------------  INTEGRANTE ------------------------------
    suspend fun insertIntegrante(integrante: RemoteIntegrante) = runBlocking {
        httpClient.post("http://XXX/insertIntegrante") {
            contentType(ContentType.Application.Json)
            setBody(integrante)
        }
    }

    suspend fun deleteIntegrante(integrante: RemoteIntegrante) = runBlocking {
        httpClient.post("http://XXX/deleteIntegrante") {
            contentType(ContentType.Application.Json)
            setBody(integrante)
        }
    }

    // ---------------------------  SEGUIDORES ------------------------------
    suspend fun insertSeguidor(seguidor: RemoteSeguidor) = runBlocking {
        httpClient.post("http://XXX/insertSeguidores") {
            contentType(ContentType.Application.Json)
            setBody(seguidor)
        }
    }

    suspend fun deleteSeguidor(seguidor: RemoteSeguidor) = runBlocking {
        httpClient.post("http://XXX/deleteSeguidor") {
            contentType(ContentType.Application.Json)
            setBody(seguidor)
        }
    }



    // ---------------------------  NOTIFICACIONES ------------------------------
    suspend fun subscribeUser(FCMClientToken: String) {
        httpClient.post("https://") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("fcm_client_token" to FCMClientToken))
        }
    }



}
