package com.gomu.festup.LocalDatabase.Repositories

import com.gomu.festup.LocalDatabase.DAO.CuadrillaDao
import com.gomu.festup.LocalDatabase.DAO.CuadrillasAsistentesDao
import com.gomu.festup.LocalDatabase.DAO.EventoDao
import com.gomu.festup.LocalDatabase.DAO.IntegranteDao
import com.gomu.festup.LocalDatabase.DAO.UsuariosAsistentesDao
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.RemoteDatabase.HTTPClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface IEventoRepository {
    suspend fun insertEvento(evento: Evento): Boolean
    fun todosLosEventos(): Flow<List<Evento>>
    fun cuadrillasEvento(id: String): List<Cuadrilla>
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
    override suspend fun insertEvento(evento: Evento): Boolean {
        TODO("Not yet implemented")
    }

    override fun todosLosEventos(): Flow<List<Evento>> {
        // TODO("Not yet implemented")
        return eventoDao.todosLosEventos()
    }

    override fun cuadrillasEvento(id: String): List<Cuadrilla> {
        TODO("Not yet implemented")
    }

    override fun usuariosEvento(id: String): List<Usuario> {
        TODO("Not yet implemented")
    }

    override suspend fun updateEvento(evento: Evento): Boolean {
        TODO("Not yet implemented")
    }

}