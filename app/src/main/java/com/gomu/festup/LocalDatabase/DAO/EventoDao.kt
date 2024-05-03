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
import com.gomu.festup.LocalDatabase.Entities.UsuarioWithEventos
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvento(evento: Evento)
    @Transaction
    @Query("SELECT * FROM Evento")
    fun todosLosEventos(): Flow<List<Evento>>

    @Transaction
    @Query("SELECT * FROM Evento WHERE id IN (SELECT id FROM UsuariosAsistentes WHERE username = :username)")
    fun eventosUsuario(username: String): Flow<List<Evento>>
    @Transaction
    @Query("SELECT * FROM Usuario WHERE username IN (SELECT username FROM UsuariosAsistentes WHERE idEvento = :id)")
    fun getUsuariosDeEvento(id: String): List<UsuarioWithEventos>

    @Transaction
    @Query("SELECT * FROM Cuadrilla WHERE nombre IN (SELECT nombreCuadrilla FROM CuadrillasAsistentes WHERE idEvento = :id)")
    fun getCuadrillasDeEvento(id: String): Flow<List<Cuadrilla>>

    @Transaction
    @Query("SELECT * FROM Evento WHERE id=:id")
    fun getEvento(id:String): Flow<Evento>

    @Update
    fun editarEvento(evento: Evento): Int

}