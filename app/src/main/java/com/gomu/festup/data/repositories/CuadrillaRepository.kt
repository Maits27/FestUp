package com.gomu.festup.data.repositories

import android.graphics.Bitmap
import android.util.Log
import com.gomu.festup.data.http.HTTPClient
import com.gomu.festup.data.http.RemoteCuadrilla
import com.gomu.festup.data.http.RemoteIntegrante
import com.gomu.festup.data.localDatabase.DAO.CuadrillaDao
import com.gomu.festup.data.localDatabase.DAO.IntegranteDao
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.Integrante
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.utils.remoteIntegranteToIntegrante
import com.gomu.festup.utils.remotecuadrillaToCuadrilla
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/******************************************************************
 * Interfaz que define la API del listado de [Cuadrilla] - Repositorio
 * Los métodos definidos son las acciones posibles para interactuar
 * con la BBDD
 *******************************************************************/
interface ICuadrillaRepository {
    suspend fun insertCuadrilla(username: String, cuadrilla: Cuadrilla, image: Bitmap?): Boolean
    fun usuariosCuadrilla(nombre: String): Flow<List<Usuario>>
    suspend fun insertUser(nombreUsuario: String, nombreCuadrilla: String): Boolean

    suspend fun eliminarIntegrante(cuadrilla: Cuadrilla, username: String): Boolean

    fun getCuadrillas(): Flow<List<Cuadrilla>>

    fun pertenezcoCuadrilla(cuadrilla: Cuadrilla, usuario: Usuario): Flow<List<Integrante>>

    suspend fun setCuadrillaProfile(nombre: String, image: Bitmap): Boolean

   fun getCuadrillaAccessToken(nombre: String): String

    suspend fun descargarCuadrillas()

    suspend fun descargarIntegrantes()

    fun integrantesCuadrillasEvento(id: String): Flow<List<Integrante>>
}
/**
 * Implementación de [ICuadrillaRepository] que usa Hilt para inyectar los
 * parámetros necesarios. Desde aquí se accede a los diferentes DAOs, que
 * se encargan de la conexión a la BBDD de Room y con la remota.
 **/
@Singleton
class CuadrillaRepository @Inject constructor(
    private val cuadrillaDao: CuadrillaDao,
    private val integranteDao: IntegranteDao,
    private val httpClient: HTTPClient
) : ICuadrillaRepository {
    /**
     * Métodos para la información de la [Cuadrilla].
     */
    override suspend fun insertCuadrilla(username: String, cuadrilla: Cuadrilla, image: Bitmap?): Boolean {
        return try {
            // Local
            cuadrillaDao.insertCuadrilla(cuadrilla)
            integranteDao.insertIntegrante(Integrante(username, cuadrilla.nombre))

            // Remote
            httpClient.insertCuadrilla(
                RemoteCuadrilla(
                cuadrilla.nombre,
                " ",
                cuadrilla.descripcion,
                cuadrilla.lugar)
            )
            httpClient.insertIntegrante(RemoteIntegrante(username,cuadrilla.nombre))

            if (image != null) httpClient.setCuadrillaImage(cuadrilla.nombre, image)
            true
        }
        catch (e:Exception){
            Log.d("Exception crear cuadrilla", e.toString())
            false
        }
    }

    override fun getCuadrillas(): Flow<List<Cuadrilla>> {
        return cuadrillaDao.getCuadrillas()
    }

    override fun getCuadrillaAccessToken(nombre: String): String {
        return httpClient.getCuadrillaAccessToken(nombre)
    }

    /**
     * Métodos para los [Usuario] relacionados con la [Cuadrilla].
     */

    override fun usuariosCuadrilla(nombre: String): Flow<List<Usuario>> {
        return cuadrillaDao.getUsuariosDeCuadrilla(nombre)
    }

    override suspend fun insertUser(nombreUsuario: String, nombreCuadrilla: String): Boolean {
        return try {
            httpClient.insertIntegrante(RemoteIntegrante(nombreUsuario, nombreCuadrilla))
            integranteDao.insertIntegrante(Integrante(nombreUsuario, nombreCuadrilla))
            true
        }
        catch (_:Exception){
            false
        }
    }

    override suspend fun eliminarIntegrante(cuadrilla: Cuadrilla, username: String): Boolean {
        return try {
            httpClient.deleteIntegrante(RemoteIntegrante(username,cuadrilla.nombre))
            integranteDao.eliminarIntegrante(Integrante(username,cuadrilla.nombre))
            val integrantesCuadrilla = integranteDao.getIntegrantesCuadrilla(cuadrilla.nombre)

            // Si la cuadrilla se queda vacía, se borra
            if (integrantesCuadrilla.first().isEmpty()){
                httpClient.deleteCuadrilla(cuadrilla.nombre)
                cuadrillaDao.eliminarCuadrilla(cuadrilla)
            }
            true
        } catch (e:Exception){
            Log.d("Exception crear cuadrilla", e.toString())
            return false
        }
    }

    override fun pertenezcoCuadrilla(cuadrilla: Cuadrilla, usuario: Usuario): Flow<List<Integrante>> {
        return integranteDao.pertenezcoCuadrilla(cuadrilla.nombre,usuario.username)
    }

    override fun integrantesCuadrillasEvento(id: String): Flow<List<Integrante>> {
        return integranteDao.integrantesCuadrillasEvento(id)
    }

    /**
     * Métodos para el perfil de la [Cuadrilla].
     */

    override suspend fun setCuadrillaProfile(nombre: String, image: Bitmap): Boolean {
        return try {
            httpClient.setCuadrillaImage(nombre, image)
            true
        } catch (e: ResponseException) {
            Log.e("HTTP", "Couldn't upload profile image.")
            e.printStackTrace()
            false
        }
    }

    /**
     * Métodos exclusivos remoto.
     */

    override suspend fun descargarCuadrillas(){
        cuadrillaDao.eliminarCuadrillas()
        val cuadrillaList = httpClient.getCuadrillas()
        cuadrillaList.map { cuadrillaDao.insertCuadrilla(remotecuadrillaToCuadrilla(it)) }
    }

    override suspend fun descargarIntegrantes(){
        integranteDao.eliminarIntegrantes()
        val integrantesList = httpClient.getIntegrantes()
        integrantesList.map { integranteDao.insertIntegrante(remoteIntegranteToIntegrante(it)) }
    }
}