package com.gomu.festup.LocalDatabase.Repositories

import android.graphics.Bitmap
import android.util.Log
import com.gomu.festup.LocalDatabase.DAO.CuadrillaDao
import com.gomu.festup.LocalDatabase.DAO.IntegranteDao
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Integrante
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.http.HTTPClient
import com.gomu.festup.http.RemoteCuadrilla
import com.gomu.festup.http.RemoteIntegrante
import com.gomu.festup.utils.remoteIntegranteToIntegrante
import com.gomu.festup.utils.remotecuadrillaToCuadrilla
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton


interface ICuadrillaRepository {
    suspend fun insertCuadrilla(username: String, cuadrilla: Cuadrilla, image: Bitmap?): Boolean
    fun cuadrillaUsuario(username: String): List<Cuadrilla>
    fun usuariosCuadrilla(nombre: String): Flow<List<Usuario>>
    suspend fun insertUser(nombreUsuario: String, nombreCuadrilla: String): Boolean

    suspend fun eliminarIntegrante(cuadrilla: Cuadrilla, username: String): Boolean

    fun getCuadrillas(): Flow<List<Cuadrilla>>

    fun pertenezcoCuadrilla(cuadrilla: Cuadrilla, usuario: Usuario): Flow<List<Integrante>>

    fun getIntegrantes(): Flow<List<Integrante>>
    suspend fun setCuadrillaProfile(nombre: String, image: Bitmap): Boolean

   fun getCuadrillaAccessToken(nombre: String): String

    suspend fun descargarCuadrillas()

    suspend fun descargarIntegrantes()

    fun integrantesCuadrillasEvento(id: String): Flow<List<Integrante>>
}
@Singleton
class CuadrillaRepository @Inject constructor(
    private val cuadrillaDao: CuadrillaDao,
    private val integranteDao: IntegranteDao,
    private val httpClient: HTTPClient
) : ICuadrillaRepository{
    override suspend fun insertCuadrilla(username: String, cuadrilla: Cuadrilla, image: Bitmap?): Boolean {
        return try {
            // Local
            cuadrillaDao.insertCuadrilla(cuadrilla)
            integranteDao.insertIntegrante(Integrante(username, cuadrilla.nombre))

            // Remote
            httpClient.insertCuadrilla(RemoteCuadrilla(
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

    override fun cuadrillaUsuario(username: String): List<Cuadrilla> {
        TODO("Not yet implemented")
    }

    override fun usuariosCuadrilla(nombre: String): Flow<List<Usuario>> {
        // TODO primero remoto
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
                Log.d("INTEGRANTES CUADRILLA", integrantesCuadrilla.first().toString())
                httpClient.deleteCuadrilla(cuadrilla.nombre)
                cuadrillaDao.eliminarCuadrilla(cuadrilla)
            }
            true
        } catch (e:Exception){
            Log.d("Exception crear cuadrilla", e.toString())
            return false
        }
    }

    override fun getCuadrillas(): Flow<List<Cuadrilla>> {
        // TODO primero remoto
        return cuadrillaDao.getCuadrillas()
    }


    // NUEVO integranteRepository? TODO
    override fun pertenezcoCuadrilla(cuadrilla: Cuadrilla, usuario: Usuario): Flow<List<Integrante>> {
        return integranteDao.pertenezcoCuadrilla(cuadrilla.nombre,usuario.username)
    }

    override fun getIntegrantes(): Flow<List<Integrante>>{
        return integranteDao.getIntegrantes()
    }

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

    override fun getCuadrillaAccessToken(nombre: String): String {
        return httpClient.getCuadrillaAccessToken(nombre)
    }

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

    override fun integrantesCuadrillasEvento(id: String): Flow<List<Integrante>> {
        return integranteDao.integrantesCuadrillasEvento(id)
    }

}