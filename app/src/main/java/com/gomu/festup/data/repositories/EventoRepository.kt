package com.gomu.festup.data.repositories

import android.graphics.Bitmap
import android.util.Log
import com.gomu.festup.data.UserCuadrillaAndEvent
import com.gomu.festup.data.http.HTTPClient
import com.gomu.festup.data.http.RemoteCuadrillaAsistente
import com.gomu.festup.data.http.RemoteEvento
import com.gomu.festup.data.http.RemoteUsuarioAsistente
import com.gomu.festup.data.localDatabase.DAO.CuadrillasAsistentesDao
import com.gomu.festup.data.localDatabase.DAO.EventoDao
import com.gomu.festup.data.localDatabase.DAO.UsuariosAsistentesDao
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.CuadrillasAsistentes
import com.gomu.festup.data.localDatabase.Entities.Evento
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.data.localDatabase.Entities.UsuariosAsistentes
import com.gomu.festup.utils.formatearFechaRemoto
import com.gomu.festup.utils.remoteCAsistenteToCAsistente
import com.gomu.festup.utils.remoteEventoToEvento
import com.gomu.festup.utils.remoteUAsistenteToUAsistente
import com.gomu.festup.utils.toStringRemoto
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import javax.inject.Inject
import javax.inject.Singleton

/******************************************************************
 * Interfaz que define la API del listado de [Evento] - Repositorio
 * Los métodos definidos son las acciones posibles para interactuar
 * con la BBDD
 *******************************************************************/
interface IEventoRepository {
    suspend fun insertarEvento(evento: Evento, username: String, image: Bitmap?): Boolean
    fun todosLosEventos(): Flow<List<Evento>>
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
    fun eventosCuadrilla(nombreCuadrilla: String) : Flow<List<Evento>>
    suspend fun descargarEventos()
    suspend fun descargarUsuariosAsistentes()
    suspend fun descargarCuadrillasAsistentes()
}

/**
 * Implementación de [IEventoRepository] que usa Hilt para inyectar los
 * parámetros necesarios. Desde aquí se accede a los diferentes DAOs, que
 * se encargan de la conexión a la BBDD de Room y con la remota.
 * */
@Singleton
class EventoRepository @Inject constructor(
    private val eventoDao: EventoDao,
    private val usuariosAsistentesDao: UsuariosAsistentesDao,
    private val cuadrillasAsistentesDao: CuadrillasAsistentesDao,
    private val httpClient: HTTPClient
) : IEventoRepository {

    /**
     * Métodos para la información del [Evento].
     */
    override suspend fun insertarEvento(evento: Evento, username: String, image: Bitmap?): Boolean {
        return try {
            // Remote: first in remote to generate the id
            val fechaString: String = evento.fecha.toStringRemoto()
            val insertedEvento = httpClient.insertEvento(
                RemoteEvento(
                id = "",
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
        return eventoDao.todosLosEventos()
    }

    /**
     * Métodos para los eventos relacionados con un
     * usuario o cuadrilla ([UsuariosAsistentes] y [CuadrillasAsistentes]).
     */

    override fun eventosUsuario(username: String): Flow<List<Evento>> {
        return eventoDao.eventosUsuario(username)
    }

    override fun eventosUsuarioList(username: String): List<Evento> {
        return eventoDao.eventosUsuarioList(username)
    }

    override suspend fun eventosSeguidos(username: String): Flow<List<UserCuadrillaAndEvent>> {
        val cuadrillas = eventoDao.getUserCuadrillaAndEvent(username).map {
            it.map { UserCuadrillaAndEvent("", it.nombreCuadrilla, it.evento) }
        }
        val usuarios = eventoDao.getUserFollowedFromEvent(username).map {
            it.map {
                UserCuadrillaAndEvent(it.username, "", it.evento)
            }
        }
        return cuadrillas.zip(usuarios) { c, u -> c + u }.map { it.sortedBy { it.evento.fecha } }
    }

    override suspend fun estaApuntado(username: String, id: String): Boolean{
        val usuario = usuariosAsistentesDao.estaApuntado(username, id)
        return usuario.isNotEmpty()
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

    override fun cuadrillasEvento(id: String): Flow<List<Cuadrilla>> {
        return eventoDao.getCuadrillasDeEvento(id)
    }

    override fun usuariosEvento(id: String): Flow<List<Usuario>>{
        return eventoDao.getUsuariosDeEvento(id)
    }

    override fun eventosCuadrilla(nombreCuadrilla: String): Flow<List<Evento>> {
        return cuadrillasAsistentesDao.getEventosCuadrilla(nombreCuadrilla)
    }


    /**
     * Métodos exclusivos remoto.
     */

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