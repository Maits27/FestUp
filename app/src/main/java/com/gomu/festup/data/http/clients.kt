package com.gomu.festup.data.http


import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.gomu.festup.R
import com.gomu.festup.data.repositories.preferences.ILoginSettings
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
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

class UserExistsException : Exception()
class AuthenticationException : Exception()

private val bearerTokenStorage = mutableListOf<BearerTokens>()

/**
 * Cliente HTTP con las peticiones que no requiren de autentificación
 * La petición de "authenticate" devuelve el [BearerToken] para identificar al usuario que ha hecho el login.
 */
@Singleton
class AuthClient @Inject constructor(private val loginSettings: ILoginSettings) {
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
            url = "http://34.71.128.243/auth/iniciarSesion",
            formParameters = Parameters.build {
                append("grant_type", "password")
                append("username", username)
                append("password", password)
            }).body()
        bearerTokenStorage.add(BearerTokens(tokenInfo.accessToken, tokenInfo.refreshToken))
        loginSettings.setLastBearerToken(tokenInfo.accessToken)
        loginSettings.setLastRefreshToken(tokenInfo.refreshToken)
    }
    fun addBearerToken(token: String, refresh: String){
        bearerTokenStorage.add(BearerTokens(token, refresh))
    }

    @Throws(UserExistsException::class)
    suspend fun createUser(user: RemoteAuthUsuario) {
        httpClient.post("http://34.71.128.243/createUser") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }

    suspend fun setUserProfile(username: String, image: Bitmap, context: Context) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        if ((byteArray.size/1024)>8117){
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, context.getString(R.string.imagen_grande), Toast.LENGTH_LONG).show()
            }
        }else{
            Log.d("Tamaño", (byteArray.size/1024).toString())
            httpClient.submitFormWithBinaryData(
                url = "http://34.71.128.243/setUserProfileImage",
                formData = formData {
                    append("image", byteArray, Headers.build {
                        append(HttpHeaders.ContentType, "image/png")
                        append(HttpHeaders.ContentDisposition, "filename=$username.png")
                    })
                }
            ) { method = HttpMethod.Put }
        }
    }
    @Throws(IOException::class)
    suspend fun getUsuarios(): List<RemoteUsuario> = runBlocking {
        val response = httpClient.get("http://34.71.128.243/getUsers")
        response.body()
    }
}

/**
 * Cliente HTTP con las peticiones que REQUIEREN de autentificación
 * Sin el [BearerToken] para identificar al usuario que ha hecho el
 * login, no permitirá ejecutar estas peticiones. Es necesario
 * autentificarse con un usuario creado ya o registrarse con uno nuevo.
 */

@Singleton
class HTTPClient @Inject constructor() {

    private val httpClient = HttpClient(CIO) {

        expectSuccess = true

        install(ContentNegotiation) { json() }

        install(Auth) {
            bearer {

                loadTokens { bearerTokenStorage.last() }

                sendWithoutRequest { request -> request.url.host == "34.71.128.243" }

                refreshTokens {

                    val refreshTokenInfo: TokenInfo = client.submitForm(
                        url = "http://34.71.128.243/auth/refresh",
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
    @Throws(IOException::class)
    suspend fun getUsuarios(): List<RemoteUsuario> = runBlocking {
        val response = httpClient.get("http://34.71.128.243/getUsers")
        response.body()
    }

    fun getUsuario(username: String): RemoteUsuario = runBlocking {
        val response = httpClient.get("http://34.71.128.243/getUser?username=$username")
        response.body()
    }

    fun editUser(usuario: RemoteUsuario) = runBlocking {
        httpClient.post("http://34.71.128.243/editUser") {
            contentType(ContentType.Application.Json)
            setBody(usuario)
        }
    }

    // ---------------------------  USUARIO ASISTENTE ------------------------------
    suspend fun getUsuariosAsistentes(): List<RemoteUsuarioAsistente> = runBlocking {
        val response = httpClient.get("http://34.71.128.243/getUsuariosAsistentes")
        response.body()
    }
    suspend fun insertUsuarioAsistente(usuarioAsistente: RemoteUsuarioAsistente) = runBlocking {
        httpClient.post("http://34.71.128.243/insertUsuarioAsistente") {
            contentType(ContentType.Application.Json)
            setBody(usuarioAsistente)
        }
    }

    suspend fun deleteUsuarioAsistente(usuarioAsistente: RemoteUsuarioAsistente) = runBlocking {
        httpClient.post("http://34.71.128.243/deleteUsuarioAsistente") {
            contentType(ContentType.Application.Json)
            setBody(usuarioAsistente)
        }
    }


    // ---------------------------  CUADRILLA ------------------------------

    suspend fun getCuadrillas(): List<RemoteCuadrilla> = runBlocking {
        val response = httpClient.get("http://34.71.128.243/getCuadrillas")
        response.body()
    }

    suspend fun insertCuadrilla(cuadrilla: RemoteCuadrilla) = runBlocking {
        httpClient.post("http://34.71.128.243/insertCuadrilla") {
            contentType(ContentType.Application.Json)
            setBody(cuadrilla)
        }
    }

    suspend fun deleteCuadrilla(nombre: String) = runBlocking {
        httpClient.post("http://34.71.128.243/deleteCuadrilla") {
            contentType(ContentType.Application.Json)
            parameter("nombre", nombre)
        }
    }

    fun getCuadrillaAccessToken(nombre: String): String = runBlocking {
        val response = httpClient.get("http://34.71.128.243/getCuadrillaAccessToken?nombre=$nombre")
        response.body<String>().replace("\"", "")
    }


    // ---------------------------  CUADRILLA ASISTENTE ------------------------------

    suspend fun getCuadrillasAsistentes(): List<RemoteCuadrillaAsistente> = runBlocking {
        val response = httpClient.get("http://34.71.128.243/getCuadrillasAsistentes")
        response.body()

    }
    suspend fun insertCuadrillaAsistente(cuadrillaAsistente: RemoteCuadrillaAsistente) = runBlocking {
        httpClient.post("http://34.71.128.243/insertCuadrillaAsistente") {
            contentType(ContentType.Application.Json)
            setBody(cuadrillaAsistente)
        }
    }

    suspend fun deleteCuadrillaAsistente(cuadrillaAsistente: RemoteCuadrillaAsistente) = runBlocking {
        httpClient.post("http://34.71.128.243/deleteCuadrillaAsistente") {
            contentType(ContentType.Application.Json)
            setBody(cuadrillaAsistente)
        }
    }

    // ---------------------------  EVENTO ------------------------------

    suspend fun getEventos(): List<RemoteEvento> = runBlocking {
        val response = httpClient.get("http://34.71.128.243/getEventos")
        response.body()
    }
    suspend fun insertEvento(evento: RemoteEvento): RemoteEvento = runBlocking {
        val response = httpClient.post("http://34.71.128.243/insertEvento") {
            contentType(ContentType.Application.Json)
            setBody(evento)
        }
        response.body()
    }
    suspend fun deleteEvento(eventoId: Int) = runBlocking {
        httpClient.post("http://34.71.128.243/deleteEvento") {
            contentType(ContentType.Application.Json)
            parameter("eventoId", eventoId)
        }
    }

    // ---------------------------  INTEGRANTE ------------------------------

    suspend fun getIntegrantes(): List<RemoteIntegrante> = runBlocking {
        val response = httpClient.get("http://34.71.128.243/getIntegrantes")
        response.body()
    }

    suspend fun insertIntegrante(integrante: RemoteIntegrante) = runBlocking {
        httpClient.post("http://34.71.128.243/insertIntegrante") {
            contentType(ContentType.Application.Json)
            setBody(integrante)
        }
    }

    suspend fun deleteIntegrante(integrante: RemoteIntegrante) = runBlocking {
        httpClient.post("http://34.71.128.243/deleteIntegrante") {
            contentType(ContentType.Application.Json)
            setBody(integrante)
        }
    }

    // ---------------------------  SEGUIDORES ------------------------------

    suspend fun getSeguidores(): List<RemoteSeguidor> = runBlocking {
        val response = httpClient.get("http://34.71.128.243/getSeguidores")
        response.body()
    }

    suspend fun insertSeguidor(seguidor: RemoteSeguidor) = runBlocking {
        httpClient.post("http://34.71.128.243/insertSeguidores") {
            contentType(ContentType.Application.Json)
            setBody(seguidor)
        }
    }

    suspend fun deleteSeguidor(seguidor: RemoteSeguidor) = runBlocking {
        httpClient.post("http://34.71.128.243/deleteSeguidor") {
            contentType(ContentType.Application.Json)
            setBody(seguidor)
        }
    }



    // ---------------------------  NOTIFICACIONES ------------------------------
    suspend fun subscribeUser(FCMClientToken: String, username: String) {
        val result = httpClient.post("http://34.71.128.243/notifications/subscribe") {
            contentType(ContentType.Application.Json)
            parameter("username", username)
            setBody(mapOf("fcm_client_token" to FCMClientToken))
        }
    }
    suspend fun unSubscribeUser(FCMClientToken: String, username: String) {
        val result = httpClient.post("http://34.71.128.243/notifications/unsubscribe") {
            contentType(ContentType.Application.Json)
            parameter("username", username)
            setBody(mapOf("fcm_client_token" to FCMClientToken))
        }
    }
    suspend fun subscribeToUser(FCMClientToken: String, username: String) {
        val result = httpClient.post("http://34.71.128.243/notifications/subscribeToUser") {
            contentType(ContentType.Application.Json)
            parameter("username", username)
            setBody(mapOf("fcm_client_token" to FCMClientToken))
        }
    }

    suspend fun unsubscribeFromUser(FCMClientToken: String, username: String) {
        val result = httpClient.delete("http://34.71.128.243/notifications/unsubscribeFromUser") {
            contentType(ContentType.Application.Json)
            parameter("username", username)
            setBody(mapOf("fcm_client_token" to FCMClientToken))
        }
    }



    // ---------------------------  IMAGEN DE PERFIL ------------------------------

    suspend fun setCuadrillaImage(nombre: String, image: Bitmap, context: Context) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        if ((byteArray.size/1024)>8117){
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, context.getString(R.string.imagen_grande), Toast.LENGTH_LONG).show()
            }
        }else{
            httpClient.submitFormWithBinaryData(
                url = "http://34.71.128.243/insertCuadrillaImage",
                formData = formData {
                    append("image", byteArray, Headers.build {
                        append(HttpHeaders.ContentType, "image/png")
                        append(HttpHeaders.ContentDisposition, "filename=$nombre.png")
                    })
                }
            ) { method = HttpMethod.Put }
        }
    }

    suspend fun setEventoProfileImage(id: String, image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        httpClient.submitFormWithBinaryData(
            url = "http://34.71.128.243/insertEventoImage",
            formData = formData {
                append("image", byteArray, Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=$id.png")
                })
            }
        ) { method = HttpMethod.Put }
    }
}
