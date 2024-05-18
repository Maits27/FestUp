package com.gomu.festup.data.localDatabase.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gomu.festup.data.localDatabase.Entities.CuadrillasAsistentes
import com.gomu.festup.data.localDatabase.Entities.Evento
import kotlinx.coroutines.flow.Flow

/**
 * Consultas con respecto a las [CuadrillasAsistentesDao] en la base de datos.
 */
@Dao
interface CuadrillasAsistentesDao {
    /////////////// Funciones Insert ///////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsistente(asistente: CuadrillasAsistentes)

    /////////////// Funciones Select ///////////////
    @Transaction
    @Query("SELECT * FROM Evento WHERE id IN (SELECT idEvento FROM CuadrillasAsistentes WHERE nombreCuadrilla=:nombreCuadrilla)")
    fun getEventosCuadrilla(nombreCuadrilla: String): Flow<List<Evento>>

    /////////////// Funciones Delete ///////////////
    @Delete
    suspend fun deleteAsistente(asistente: CuadrillasAsistentes)

    @Transaction
    @Query("DELETE FROM CuadrillasAsistentes ")
    suspend fun eliminarCuadrillasAsistentes()
}