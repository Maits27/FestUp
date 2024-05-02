package com.gomu.festup.LocalDatabase.Repositories

import android.graphics.Bitmap
import android.util.Log
import com.gomu.festup.LocalDatabase.DAO.CuadrillaDao
import com.gomu.festup.LocalDatabase.DAO.IntegranteDao
import com.gomu.festup.LocalDatabase.DAO.UsuarioDao
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Integrante
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.RemoteDatabase.HTTPClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


interface ICuadrillaRepository {
    suspend fun insertCuadrilla(username: String, cuadrilla: Cuadrilla): Boolean
    fun cuadrillaUsuario(username: String): List<Cuadrilla>
    suspend fun usuariosCuadrilla(nombre: String): List<Usuario>
    fun eventosCuadrilla(nombreCuadrilla: String): List<Evento>
    suspend fun insertUser(usuario: Usuario): Boolean
}
@Singleton
class CuadrillaRepository @Inject constructor(
    private val cuadrillaDao: CuadrillaDao,
    private val integranteDao: IntegranteDao,
    private val httpClient: HTTPClient
) : ICuadrillaRepository{
    override suspend fun insertCuadrilla(username: String, cuadrilla: Cuadrilla): Boolean {
         try {
             cuadrillaDao.insertCuadrilla(cuadrilla)
             integranteDao.insertIntegrante(Integrante(username, cuadrilla.nombre))
             return true
         }catch (e:Exception){
             Log.d("EXCEPCION", e.toString())
             return false
         }
    }

    override fun cuadrillaUsuario(username: String): List<Cuadrilla> {
        TODO("Not yet implemented")
    }

    override suspend fun usuariosCuadrilla(nombre: String): List<Usuario> {
        // TODO primero remoto
        return cuadrillaDao.getUsuariosDeCuadrilla(nombre)
    }

    override fun eventosCuadrilla(nombreCuadrilla: String): List<Evento> {
        TODO("Not yet implemented")
    }

    override suspend fun insertUser(usuario: Usuario): Boolean {
        TODO("Not yet implemented")
    }
}