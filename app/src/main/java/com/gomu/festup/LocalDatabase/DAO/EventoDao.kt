package com.gomu.festup.LocalDatabase.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.CuadrillaWithEventos
import com.gomu.festup.LocalDatabase.Entities.CuadrillaWithUsuarios
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.LocalDatabase.Entities.UsuarioWithEventos
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date

@Dao
interface EventoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvento(evento: Evento)
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
    fun eventosUsuario(username: String, today: Date = Date()): Flow<List<Evento>>

    @Transaction
    @Query("SELECT * FROM Usuario WHERE username IN (SELECT username FROM UsuariosAsistentes WHERE idEvento = :id)")
    fun getUsuariosDeEvento(id: String): Flow<List<Usuario>>

    @Transaction
    @Query("SELECT * FROM Cuadrilla WHERE nombre IN (SELECT nombreCuadrilla FROM CuadrillasAsistentes WHERE idEvento = :id)")
    fun getCuadrillasDeEvento(id: String): Flow<List<Cuadrilla>>

    @Transaction
    @Query("SELECT * FROM Evento WHERE id=:id")
    fun getEvento(id:String): Flow<Evento>

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

    @Update
    fun editarEvento(evento: Evento): Int

    @Transaction
    @Query("DELETE FROM Evento ")
    suspend fun eliminarEventos()

    @Transaction
    @Query("SELECT * FROM Evento WHERE id IN ( " +
            "SELECT idEvento FROM UsuariosAsistentes WHERE username IN ( " +
            "SELECT seguido FROM Seguidores WHERE seguidor = :username " +
            ") " +
            "UNION " +
            "SELECT idEvento FROM CuadrillasAsistentes WHERE nombreCuadrilla IN ( " +
            "SELECT nombreCuadrilla FROM Integrante WHERE username IN ( " +
            "SELECT seguido FROM Seguidores WHERE seguidor = :username))) "+
            "AND fecha >= :today  ORDER BY fecha")

    fun getEventosSeguidos(username: String, today: Date = Date()): Flow<List<Evento>>


}