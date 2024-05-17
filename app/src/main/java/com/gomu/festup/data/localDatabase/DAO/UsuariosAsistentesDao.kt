package com.gomu.festup.data.localDatabase.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.data.localDatabase.Entities.UsuariosAsistentes
import kotlinx.coroutines.flow.Flow

/**
 * Consultas con respecto a los [UsuariosAsistentes] en la base de datos.
 */
@Dao
interface UsuariosAsistentesDao {
    /////////////// Funciones Insert ///////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuarioAsistente(asistente: UsuariosAsistentes)

    /////////////// Funciones Select ///////////////
    @Transaction
    @Query("SELECT * FROM UsuariosAsistentes")
    fun todosLosUsuariosAsistentes(): Flow<List<UsuariosAsistentes>>

    @Transaction
    @Query("SELECT * FROM UsuariosAsistentes WHERE username=:username AND idEvento=:id")
    suspend fun estaApuntado(username: String, id: String): List<UsuariosAsistentes>

    /////////////// Funciones Delete ///////////////
    @Delete
    suspend fun deleteAsistente(asistente: UsuariosAsistentes)

    @Transaction
    @Query("DELETE FROM UsuariosAsistentes ")
    suspend fun eliminarUsuariosAsistentes()
}