package com.gomu.festup.RemoteDatabase

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*


import javax.inject.Inject
import javax.inject.Singleton

class UserExistsException : Exception()

/*******************************************************************************
 ****                              Cliente HTTP                             ****
 *******************************************************************************/
/**             (Requisito obligatorio)           **/
@Singleton
class HTTPClient @Inject constructor() {
    /*******************************************************************************
    ##################################    INIT    ##################################
     *******************************************************************************/

    private val httpClient = HttpClient(CIO) {

        // If return code is not a 2xx then throw an exception
        expectSuccess = true

        // Install JSON handler (allows to receive and send JSON data)
        install(ContentNegotiation) { json() }

        // Handle non 2xx status responses
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                when {
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Unauthorized -> Log.d(
                        "HTTP",
                        exception.toString()
                    )

                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Conflict -> Log.d(
                        "HTTP",
                        exception.toString()
                    )

                    else -> {
                        exception.printStackTrace()
                        Log.d("HTTP", exception.toString())
                        throw exception
                    }
                }
            }
        }
    }

    /*******************************************************************************
    ##########################    SUBSCRIPCIÓN FIREBASE    ##########################
     *******************************************************************************/
    suspend fun subscribeUser(FCMClientToken: String) {
        httpClient.post("http://") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("fcm_client_token" to FCMClientToken))
        }
    }
}
