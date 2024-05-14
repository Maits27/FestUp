package com.gomu.festup.LocalDatabase.Repositories

import android.graphics.Bitmap
import android.util.Log
import com.gomu.festup.LocalDatabase.DAO.CuadrillasAsistentesDao
import com.gomu.festup.LocalDatabase.DAO.EventoDao
import com.gomu.festup.LocalDatabase.DAO.UsuariosAsistentesDao
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.CuadrillasAsistentes
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.LocalDatabase.Entities.UsuariosAsistentes
import com.gomu.festup.RemoteDatabase.HTTPClient
import com.gomu.festup.RemoteDatabase.RemoteCuadrillaAsistente
import com.gomu.festup.RemoteDatabase.RemoteEvento
import com.gomu.festup.RemoteDatabase.RemoteUsuarioAsistente
import com.gomu.festup.data.UserCuadrillaAndEvent
import com.gomu.festup.ui.widget.EventoWidget
import com.gomu.festup.utils.formatearFechaRemoto
import com.gomu.festup.utils.remoteCAsistenteToCAsistente
import com.gomu.festup.utils.remoteEventoToEvento
import com.gomu.festup.utils.remoteUAsistenteToUAsistente
import com.gomu.festup.utils.remoteUsuarioToUsuario
import com.gomu.festup.utils.toStringNuestro
import com.gomu.festup.utils.toStringRemoto
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import javax.inject.Inject
import javax.inject.Singleton

interface IEventoRepository {
    suspend fun insertarEvento(evento: Evento, username: String, image: Bitmap?): Boolean
    fun todosLosEventos(): Flow<List<Evento>>
    fun usuariosEventos(): Flow<List<UsuariosAsistentes>>
    fun eventosUsuario(username: String): Flow<List<Evento>>
    fun eventosUsuarioList(username: String): List<Evento>
    suspend fun eventosSeguidos(username: String): Flow<List<UserCuadrillaAndEvent>>
    suspend fun estaApuntado(username: String, id: String): Boolean
    fun cuadrillasUsuarioApuntadas(username: String, id: String): Flow<List<Cuadrilla>>
    fun cuadrillasUsuarioNoApuntadas(username: String, id: String): Flow<List<Cuadrilla>>
    suspend fun apuntarse(usuario: Usuario, id: String)
    suspend fun desapuntarse(usuario: Usuario, id: String)
    suspend fun apuntarse(cuadrilla: Cuadrilla, id: String)
    suspend fun desapuntarse(cuadrilla: Cuadrilla, id: String)
    fun cuadrillasEvento(id: String): Flow<List<Cuadrilla>>
    fun usuariosEvento(id: String): Flow<List<Usuario>>
    suspend fun updateEvento(evento: Evento): Boolean
    suspend fun setEventoProfileImage(id: String, image: Bitmap): Boolean

    fun eventosCuadrilla(nombreCuadrilla: String) : Flow<List<Evento>>

    suspend fun descargarEventos()

    suspend fun descargarUsuariosAsistentes()

    suspend fun descargarCuadrillasAsistentes()


}
@Singleton
class EventoRepository @Inject constructor(
    private val eventoDao: EventoDao,
    private val usuariosAsistentesDao: UsuariosAsistentesDao,
    private val cuadrillasAsistentesDao: CuadrillasAsistentesDao,
    private val httpClient: HTTPClient
) : IEventoRepository{

    override suspend fun insertarEvento(evento: Evento, username: String, image: Bitmap?): Boolean {
        return try {
            // Remote: first in remote to generate the id
            // TODO ARREGLAR ID
            val fechaString: String = evento.fecha.toStringRemoto()
            val insertedEvento = httpClient.insertEvento(RemoteEvento(
                id = "", // TODO (evento.id) cambiar esto cuando se genere correctamente el id
                nombre = evento.nombre,
                fecha = fechaString,
                descripcion = evento.descripcion,
                localizacion = evento.localizacion)
            )
            httpClient.insertUsuarioAsistente(RemoteUsuarioAsistente(username, insertedEvento.id))
            if (image != null) httpClient.setEventoProfileImage(insertedEvento.id, image)

            // Local
            // Create a Evento object with the id returned from the server
            val eventoToLocal = Evento(
                id = insertedEvento.id,
                nombre = evento.nombre,
                fecha = fechaString.formatearFechaRemoto(),
                descripcion = evento.descripcion,
                localizacion = evento.localizacion
            )
            eventoDao.insertEvento(eventoToLocal)
            usuariosAsistentesDao.insertUsuarioAsistente(UsuariosAsistentes(username, insertedEvento.id))

            true
        }catch (e:Exception){
            Log.d("Exception crear evento", e.toString())
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

    override fun eventosUsuarioList(username: String): List<Evento> {
        return eventoDao.eventosUsuarioList(username)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun eventosSeguidos(username: String): Flow<List<UserCuadrillaAndEvent>> {
        var cuadrillas = eventoDao.getUserCuadrillaAndEvent(username).map {
            it.map { UserCuadrillaAndEvent("", it.nombreCuadrilla, it.evento) }
        }
        var usuarios = eventoDao.getUserFollowedFromEvent(username).map {
            it.map {
                UserCuadrillaAndEvent(it.username, "", it.evento)
            }
        }
        return cuadrillas.zip(usuarios) { c, u -> c + u }.map { it.sortedBy { it.evento.fecha } }
    }

    fun eventosSeguidosPrevio(username: String): Flow<List<Evento>> {
        return eventoDao.getEventosSeguidosPrevio(username)
    }


    override suspend fun estaApuntado(username: String, id: String): Boolean{
        val usuario = usuariosAsistentesDao.estaApuntado(username, id)
        if (usuario.isEmpty()) return false
        return true
    }

    override fun cuadrillasUsuarioApuntadas(username: String, id: String): Flow<List<Cuadrilla>> {
        return eventoDao.cuadrillasUsuarioApuntadas(username, id)
    }

    override fun cuadrillasUsuarioNoApuntadas(username: String, id: String): Flow<List<Cuadrilla>> {
        return eventoDao.cuadrillasUsuarioNoApuntadas(username, id)
    }

    override suspend fun apuntarse(usuario: Usuario, id: String){
        httpClient.insertUsuarioAsistente(RemoteUsuarioAsistente(usuario.username,id))
        usuariosAsistentesDao.insertUsuarioAsistente(UsuariosAsistentes(usuario.username, id))
    }
    override suspend fun desapuntarse(usuario: Usuario, id: String){
        httpClient.deleteUsuarioAsistente(RemoteUsuarioAsistente(usuario.username,id))
        usuariosAsistentesDao.deleteAsistente(UsuariosAsistentes(usuario.username, id))
    }
    override suspend fun apuntarse(cuadrilla: Cuadrilla, id: String){
        httpClient.insertCuadrillaAsistente(RemoteCuadrillaAsistente(cuadrilla.nombre,id))
        cuadrillasAsistentesDao.insertAsistente(CuadrillasAsistentes(cuadrilla.nombre, id))
    }
    override suspend fun desapuntarse(cuadrilla: Cuadrilla, id: String){
        httpClient.deleteCuadrillaAsistente(RemoteCuadrillaAsistente(cuadrilla.nombre,id))
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

    override fun eventosCuadrilla(nombreCuadrilla: String): Flow<List<Evento>> {
        return cuadrillasAsistentesDao.getEventosCuadrilla(nombreCuadrilla)
    }

    override suspend fun updateEvento(evento: Evento): Boolean {
        TODO("Not yet implemented")
    }
    override suspend fun setEventoProfileImage(id: String, image: Bitmap): Boolean {
        return try {
            httpClient.setEventoProfileImage(id, image)
            true
        } catch (e: ResponseException) {
            Log.e("HTTP", "Couldn't upload profile image.")
            e.printStackTrace()
            false
        }
    }

    override suspend fun descargarEventos(){
        eventoDao.eliminarEventos()
        val eventosList = httpClient.getEventos()
        eventosList.map{eventoDao.insertEvento(remoteEventoToEvento(it))}
    }


    override suspend fun descargarUsuariosAsistentes(){
        usuariosAsistentesDao.eliminarUsuariosAsistentes()
        val usuariosAsistentesList = httpClient.getUsuariosAsistentes()
        usuariosAsistentesList.map{usuariosAsistentesDao.insertUsuarioAsistente(
            remoteUAsistenteToUAsistente(it)
        )}
    }

    override suspend fun descargarCuadrillasAsistentes(){
        cuadrillasAsistentesDao.eliminarCuadrillasAsistentes()
        val cuadrillasAsistentesList = httpClient.getCuadrillasAsistentes()
        cuadrillasAsistentesList.map{cuadrillasAsistentesDao.insertAsistente(
            remoteCAsistenteToCAsistente(it)
        )}
    }


}