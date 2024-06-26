package com.gomu.festup.data.localDatabase.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.data.CuadrillaAndEvent
import com.gomu.festup.data.UserAndEvent
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.Evento
import com.gomu.festup.data.localDatabase.Entities.Usuario
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Consultas con respecto a los [Evento] en la base de datos.
 */
@Dao
interface EventoDao {
    /////////////// Funciones Insert ///////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvento(evento: Evento)

    /////////////// Funciones Select ///////////////
    @Transaction
    @Query("SELECT * FROM Evento")
    fun todosLosEventos(): Flow<List<Evento>>

    @Transaction
    @Query("SELECT * FROM Evento WHERE id IN ( " +
            "SELECT idEvento FROM UsuariosAsistentes WHERE username = :username " +
            "UNION " +
            "SELECT idEvento FROM CuadrillasAsistentes WHERE nombreCuadrilla IN ( " +
            "SELECT nombreCuadrilla FROM Integrante WHERE username = :username))" +
            "AND fecha >= :today  ORDER BY fecha")
            /**
             * Coge el Flow de los eventos futuros de un usuario ordenados por fecha
             */
    fun eventosUsuario(username: String, today: Date = Date()): Flow<List<Evento>>

    @Transaction
    @Query("SELECT * FROM Evento WHERE id IN ( " +
            "SELECT idEvento FROM UsuariosAsistentes WHERE username = :username " +
            "UNION " +
            "SELECT idEvento FROM CuadrillasAsistentes WHERE nombreCuadrilla IN ( " +
            "SELECT nombreCuadrilla FROM Integrante WHERE username = :username))" +
            "AND fecha >= :today  ORDER BY fecha")
            /**
             * Coge los eventos futuros de un usuario ordenados por fecha
             */
    fun eventosUsuarioList(username: String, today: Date = Date()): List<Evento>

    @Transaction
    @Query("SELECT * FROM Usuario WHERE username IN (SELECT username FROM UsuariosAsistentes WHERE idEvento = :id)")
    fun getUsuariosDeEvento(id: String): Flow<List<Usuario>>

    @Transaction
    @Query("SELECT * FROM Cuadrilla WHERE nombre IN (SELECT nombreCuadrilla FROM CuadrillasAsistentes WHERE idEvento = :id)")
    fun getCuadrillasDeEvento(id: String): Flow<List<Cuadrilla>>

    @Transaction
    @Query("SELECT * FROM Cuadrilla WHERE nombre IN " +
            "(SELECT nombreCuadrilla FROM Integrante WHERE username=:username) " +
            "AND nombre IN (SELECT nombreCuadrilla FROM CuadrillasAsistentes WHERE idEvento=:id)")
    fun cuadrillasUsuarioApuntadas(username: String, id: String): Flow<List<Cuadrilla>>

    @Transaction
    @Query("SELECT * FROM Cuadrilla WHERE nombre NOT IN " +
            "(SELECT nombreCuadrilla FROM CuadrillasAsistentes WHERE idEvento=:id) " +
            "AND nombre IN (SELECT nombreCuadrilla FROM Integrante WHERE username=:username)")
    fun cuadrillasUsuarioNoApuntadas(username: String, id: String): Flow<List<Cuadrilla>>

    @Transaction
    @Query("SELECT DISTINCT Integrante.nombreCuadrilla AS nombreCuadrilla, Evento.* FROM Evento " +
            "INNER JOIN CuadrillasAsistentes ON Evento.id = CuadrillasAsistentes.idEvento " +
            "INNER JOIN Integrante ON CuadrillasAsistentes.nombreCuadrilla = Integrante.nombreCuadrilla " +
            "WHERE Evento.id IN ( " +
            "    SELECT idEvento FROM CuadrillasAsistentes WHERE nombreCuadrilla IN (" +
            "       SELECT nombreCuadrilla FROM Integrante WHERE username IN( " +
            "           SELECT seguido FROM Seguidores WHERE seguidor = :username AND seguido != :username " + // Excluir el usuario
            "    )) " +
            ") AND Integrante.nombreCuadrilla IN (SELECT nombreCuadrilla FROM Integrante WHERE username IN" +
            "(SELECT seguido FROM Seguidores WHERE seguidor = :username AND seguido != :username))" +
            "AND Evento.fecha >= :today ORDER BY Evento.fecha")
    fun getUserCuadrillaAndEvent(username: String, today: Date = Date()): Flow<List<CuadrillaAndEvent>>

    @Transaction
    @Query("SELECT UsuariosAsistentes.username AS username, Evento.* FROM Evento " +
            "INNER JOIN UsuariosAsistentes ON Evento.id = UsuariosAsistentes.idEvento " +
            "WHERE Evento.id IN ( " +
            "    SELECT idEvento FROM UsuariosAsistentes WHERE username IN (" +
            "       SELECT seguido FROM Seguidores WHERE seguidor = :username AND seguido != :username " + // Excluir el usuario
            "    ) " +
            ") AND username IN (SELECT seguido FROM Seguidores WHERE seguidor = :username AND seguido != :username)" +
            "AND Evento.fecha >= :today ORDER BY Evento.fecha")
    fun getUserFollowedFromEvent(username: String, today: Date = Date()): Flow<List<UserAndEvent>>


    /////////////// Funciones Update ///////////////
    @Update
    fun editarEvento(evento: Evento): Int

    /////////////// Funciones Delete ///////////////
    @Transaction
    @Query("DELETE FROM Evento ")
    suspend fun eliminarEventos()

}