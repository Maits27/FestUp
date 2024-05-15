package com.gomu.festup.data.localDatabase.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.data.localDatabase.Entities.CuadrillasAsistentes
import com.gomu.festup.data.localDatabase.Entities.Evento
import kotlinx.coroutines.flow.Flow

@Dao
interface CuadrillasAsistentesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsistente(asistente: CuadrillasAsistentes)

    @Transaction
    @Query("SELECT * FROM CuadrillasAsistentes")
    fun todasLasCuadrillasAsistentes(): Flow<List<CuadrillasAsistentes>>



    @Transaction
    @Query("SELECT nombreCuadrilla FROM CuadrillasAsistentes WHERE idEvento=:id")
    fun getCuadrillasAsistentesEvento(id: String): Flow<List<String>>
    @Transaction
    @Query("SELECT idEvento FROM CuadrillasAsistentes WHERE nombreCuadrilla=:name")
    fun getEventosIdCuadrilla(name: String): Flow<List<String>>

    @Transaction
    @Query("SELECT * FROM Evento WHERE id IN (SELECT idEvento FROM CuadrillasAsistentes WHERE nombreCuadrilla=:nombreCuadrilla)")
    fun getEventosCuadrilla(nombreCuadrilla: String): Flow<List<Evento>>

    @Update
    fun editarCuadrillasAsistentes(asistente: CuadrillasAsistentes): Int

    @Delete
    suspend fun deleteAsistente(asistente: CuadrillasAsistentes)

    @Transaction
    @Query("DELETE FROM CuadrillasAsistentes ")
    suspend fun eliminarCuadrillasAsistentes()
}