package com.gomu.festup.LocalDatabase.Repositories

import android.util.Log
import com.gomu.festup.LocalDatabase.DAO.CuadrillaDao
import com.gomu.festup.LocalDatabase.DAO.CuadrillasAsistentesDao
import com.gomu.festup.LocalDatabase.DAO.EventoDao
import com.gomu.festup.LocalDatabase.DAO.IntegranteDao
import com.gomu.festup.LocalDatabase.DAO.UsuariosAsistentesDao
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.LocalDatabase.Entities.UsuariosAsistentes
import com.gomu.festup.RemoteDatabase.HTTPClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface IEventoRepository {
    suspend fun insertEvento(evento: Evento)
    fun todosLosEventos(): Flow<List<Evento>>
    fun usuariosEventos(): Flow<List<UsuariosAsistentes>>
    suspend fun insertUsuarioAsistente(username: String, id: String)
    fun eventosUsuario(username: String): Flow<List<Evento>>
    fun cuadrillasEvento(id: String): Flow<List<Cuadrilla>>
    fun usuariosEvento(id: String): List<Usuario>
    suspend fun updateEvento(evento: Evento): Boolean
}
@Singleton
class EventoRepository @Inject constructor(
    private val eventoDao: EventoDao,
    private val usuariosAsistentesDao: UsuariosAsistentesDao,
    private val cuadrillasAsistentesDao: CuadrillasAsistentesDao,
    private val httpClient: HTTPClient
) : IEventoRepository{
    override suspend fun insertEvento(evento: Evento) {
        eventoDao.insertEvento(evento)
    }
    override suspend fun insertUsuarioAsistente(username: String, id: String){
        usuariosAsistentesDao.insertUsuarioAsistente(UsuariosAsistentes(username, id))
    }

    override fun todosLosEventos(): Flow<List<Evento>> {
        // TODO("Not yet implemented")
        return eventoDao.todosLosEventos()
    }

    override fun eventosUsuario(username: String): Flow<List<Evento>> {
        return eventoDao.eventosUsuario(username)
    }

    override fun usuariosEventos(): Flow<List<UsuariosAsistentes>> {
        return usuariosAsistentesDao.todosLosUsuariosAsistentes()
    }

    override fun cuadrillasEvento(id: String): Flow<List<Cuadrilla>> {
        return eventoDao.getCuadrillasDeEvento(id)
    }

    override fun usuariosEvento(id: String): List<Usuario> {
        TODO("Not yet implemented")
    }

    override suspend fun updateEvento(evento: Evento): Boolean {
        TODO("Not yet implemented")
    }

}