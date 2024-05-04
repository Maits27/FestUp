package com.gomu.festup.LocalDatabase.Repositories

import android.graphics.Bitmap
import android.util.Log
import com.gomu.festup.LocalDatabase.DAO.CuadrillaDao
import com.gomu.festup.LocalDatabase.DAO.CuadrillasAsistentesDao
import com.gomu.festup.LocalDatabase.DAO.EventoDao
import com.gomu.festup.LocalDatabase.DAO.IntegranteDao
import com.gomu.festup.LocalDatabase.DAO.UsuariosAsistentesDao
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.CuadrillasAsistentes
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Integrante
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.LocalDatabase.Entities.UsuariosAsistentes
import com.gomu.festup.RemoteDatabase.HTTPClient
import com.gomu.festup.RemoteDatabase.RemoteCuadrilla
import com.gomu.festup.RemoteDatabase.RemoteEvento
import com.gomu.festup.RemoteDatabase.RemoteIntegrante
import com.gomu.festup.RemoteDatabase.RemoteUsuarioAsistente
import com.gomu.festup.utils.toStringNuestro
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import javax.inject.Inject
import javax.inject.Singleton

interface IEventoRepository {
    suspend fun insertarEvento(evento: Evento, username: String): Boolean
    fun todosLosEventos(): Flow<List<Evento>>
    fun usuariosEventos(): Flow<List<UsuariosAsistentes>>
    fun eventosUsuario(username: String): Flow<List<Evento>>
    suspend fun estaApuntado(username: String, id: String): Boolean
    suspend fun apuntarse(usuario: Usuario, id: String)
    suspend fun desapuntarse(usuario: Usuario, id: String)
    suspend fun apuntarse(cuadrilla: Cuadrilla, id: String)
    suspend fun desapuntarse(cuadrilla: Cuadrilla, id: String)
    fun cuadrillasEvento(id: String): Flow<List<Cuadrilla>>
    fun usuariosEvento(id: String): Flow<List<Usuario>>
    suspend fun updateEvento(evento: Evento): Boolean
    suspend fun setEventoProfile(id: String, image: Bitmap): Boolean
}
@Singleton
class EventoRepository @Inject constructor(
    private val eventoDao: EventoDao,
    private val usuariosAsistentesDao: UsuariosAsistentesDao,
    private val cuadrillasAsistentesDao: CuadrillasAsistentesDao,
    private val httpClient: HTTPClient
) : IEventoRepository{

    override suspend fun insertarEvento(evento: Evento, username: String): Boolean {
        return try {
            eventoDao.insertEvento(evento)
            usuariosAsistentesDao.insertUsuarioAsistente(UsuariosAsistentes(username, evento.id))

            // TODO ARREGLAR, ID Y DATE FORMATO
            val fechaString: String = evento.fecha.toStringNuestro()
            httpClient.insertEvento(RemoteEvento(
                id = evento.id, // TODO (evento.id) cambiar esto cuando se genere correctamente el id
                nombre = evento.nombre,
                fecha = fechaString,
                numeroAsistentes = evento.numeroAsistentes,
                descripcion = evento.descripcion,
                localizacion = evento.localizacion)
            )
            httpClient.insertUsuarioAsistente(RemoteUsuarioAsistente(username,evento.id))
            true
        }catch (e:Exception){
            Log.d("Exception crear cuadrilla", e.toString())
            false
        }
    }


    override fun todosLosEventos(): Flow<List<Evento>> {
        // TODO("Not yet implemented")
        return eventoDao.todosLosEventos()
    }

    override fun eventosUsuario(username: String): Flow<List<Evento>> {
        return eventoDao.eventosUsuario(username)
    }

    override suspend fun estaApuntado(username: String, id: String): Boolean{
        val usuario = usuariosAsistentesDao.estaApuntado(username, id)
        if (usuario.isEmpty()) return false
        return true
    }
    override suspend fun apuntarse(usuario: Usuario, id: String){
        usuariosAsistentesDao.insertUsuarioAsistente(UsuariosAsistentes(usuario.username, id))
    }
    override suspend fun desapuntarse(usuario: Usuario, id: String){
        usuariosAsistentesDao.deleteAsistente(UsuariosAsistentes(usuario.username, id))
    }
    override suspend fun apuntarse(cuadrilla: Cuadrilla, id: String){
        cuadrillasAsistentesDao.insertAsistente(CuadrillasAsistentes(cuadrilla.nombre, id))
    }
    override suspend fun desapuntarse(cuadrilla: Cuadrilla, id: String){
        cuadrillasAsistentesDao.deleteAsistente(CuadrillasAsistentes(cuadrilla.nombre, id))
    }

    override fun usuariosEventos(): Flow<List<UsuariosAsistentes>> {
        return usuariosAsistentesDao.todosLosUsuariosAsistentes()
    }

    override fun cuadrillasEvento(id: String): Flow<List<Cuadrilla>> {
        return eventoDao.getCuadrillasDeEvento(id)
    }

    override fun usuariosEvento(id: String): Flow<List<Usuario>>{
        return eventoDao.getUsuariosDeEvento(id)
    }

    override suspend fun updateEvento(evento: Evento): Boolean {
        TODO("Not yet implemented")
    }
    override suspend fun setEventoProfile(id: String, image: Bitmap): Boolean {
        return try {
            httpClient.setEventoProfile(id, image)
            true
        } catch (e: ResponseException) {
            Log.e("HTTP", "Couldn't upload profile image.")
            e.printStackTrace()
            false
        }
    }

}