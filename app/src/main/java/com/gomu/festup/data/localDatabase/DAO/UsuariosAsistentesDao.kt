package com.gomu.festup.data.localDatabase.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.data.localDatabase.Entities.UsuariosAsistentes
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuariosAsistentesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuarioAsistente(asistente: UsuariosAsistentes)

    @Transaction
    @Query("SELECT * FROM UsuariosAsistentes")
    fun todosLosUsuariosAsistentes(): Flow<List<UsuariosAsistentes>>

    @Transaction
    @Query("SELECT * FROM UsuariosAsistentes WHERE username=:username AND idEvento=:id")
    suspend fun estaApuntado(username: String, id: String): List<UsuariosAsistentes>

    @Transaction
    @Query("SELECT username FROM UsuariosAsistentes WHERE idEvento=:id")
    fun getUsuariosAsistentesEvento(id: String): Flow<List<String>>
    @Transaction
    @Query("SELECT idEvento FROM UsuariosAsistentes WHERE username=:username")
    fun getEventosUsuario(username: String): Flow<List<String>>

    @Update
    fun editarUsuarioAsistente(asistente: UsuariosAsistentes): Int
    @Delete
    suspend fun deleteAsistente(asistente: UsuariosAsistentes)

    @Transaction
    @Query("DELETE FROM UsuariosAsistentes ")
    suspend fun eliminarUsuariosAsistentes()
}