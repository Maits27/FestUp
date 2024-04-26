package com.gomu.festup.LocalDatabase.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.LocalDatabase.Entities.UsuariosAsistentes
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuariosAsistentesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsuarioAsistente(asistente: UsuariosAsistentes)

    @Transaction
    @Query("SELECT * FROM UsuariosAsistentes")
    fun todosLosUsuariosAsistentes(): Flow<List<UsuariosAsistentes>>

    @Transaction
    @Query("SELECT username FROM UsuariosAsistentes WHERE idEvento=:id")
    fun getUsuariosAsistentesEvento(id: String): Flow<List<String>>
    @Transaction
    @Query("SELECT idEvento FROM UsuariosAsistentes WHERE username=:username")
    fun getEventosUsuario(username: String): Flow<List<String>>


    @Update
    fun editarUsuarioAsistente(asistente: UsuariosAsistentes): Int
}