package com.gomu.festup.LocalDatabase.Repositories

import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.collectAsState
import com.gomu.festup.LocalDatabase.DAO.CuadrillaDao
import com.gomu.festup.LocalDatabase.DAO.IntegranteDao
import com.gomu.festup.LocalDatabase.DAO.UsuarioDao
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Integrante
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.RemoteDatabase.HTTPClient
import com.gomu.festup.RemoteDatabase.RemoteCuadrilla
import com.gomu.festup.RemoteDatabase.RemoteIntegrante
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton


interface ICuadrillaRepository {
    suspend fun insertCuadrilla(username: String, cuadrilla: Cuadrilla): Boolean
    fun cuadrillaUsuario(username: String): List<Cuadrilla>
    fun usuariosCuadrilla(nombre: String): Flow<List<Usuario>>
    fun eventosCuadrilla(nombreCuadrilla: String): List<Evento>
    suspend fun insertUser(usuario: Usuario): Boolean

    suspend fun eliminarCuadrilla(cuadrilla: Cuadrilla): Boolean

    fun getCuadrillas(): Flow<List<Cuadrilla>>

    fun pertenezcoCuadrilla(cuadrilla: Cuadrilla, usuario: Usuario): Flow<List<Integrante>>

    fun getIntegrantes(): Flow<List<Integrante>>
    suspend fun setCuadrillaProfile(nombre: String, image: Bitmap): Boolean
}
@Singleton
class CuadrillaRepository @Inject constructor(
    private val cuadrillaDao: CuadrillaDao,
    private val integranteDao: IntegranteDao,
    private val httpClient: HTTPClient
) : ICuadrillaRepository{
    override suspend fun insertCuadrilla(username: String, cuadrilla: Cuadrilla): Boolean {
        return try {
            cuadrillaDao.insertCuadrilla(cuadrilla)
            integranteDao.insertIntegrante(Integrante(username, cuadrilla.nombre))


            httpClient.insertCuadrilla(RemoteCuadrilla(cuadrilla.nombre," ",cuadrilla.descripcion,cuadrilla.lugar,cuadrilla.profileImagePath))
            httpClient.insertIntegrante(RemoteIntegrante(username,cuadrilla.nombre))
            true
        }catch (e:Exception){
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

    override fun eventosCuadrilla(nombreCuadrilla: String): List<Evento> {
        TODO("Not yet implemented")
    }

    override suspend fun insertUser(usuario: Usuario): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun eliminarCuadrilla(cuadrilla: Cuadrilla): Boolean {
        return try {
            httpClient.deleteCuadrilla(cuadrilla.nombre)
            cuadrillaDao.eliminarCuadrilla(cuadrilla)
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
            httpClient.setCuadrillaProfile(nombre, image)
            true
        } catch (e: ResponseException) {
            Log.e("HTTP", "Couldn't upload profile image.")
            e.printStackTrace()
            false
        }
    }


}