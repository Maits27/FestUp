package com.gomu.festup.LocalDatabase.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.LocalDatabase.Entities.CuadrillasAsistentes
import kotlinx.coroutines.flow.Flow

@Dao
interface CuadrillasAsistentesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIntegrante(asistente: CuadrillasAsistentes)

    @Transaction
    @Query("SELECT * FROM CuadrillasAsistentes")
    fun todasLasCuadrillasAsistentes(): Flow<List<CuadrillasAsistentes>>

    @Transaction
    @Query("SELECT nombreCuadrilla FROM CuadrillasAsistentes WHERE idEvento=:id")
    fun getCuadrillasAsistentesEvento(id: String): Flow<List<String>>
    @Transaction
    @Query("SELECT idEvento FROM CuadrillasAsistentes WHERE nombreCuadrilla=:name")
    fun getEventosCuadrilla(name: String): Flow<List<String>>


    @Update
    fun editarCuadrillasAsistentes(asistente: CuadrillasAsistentes): Int
}