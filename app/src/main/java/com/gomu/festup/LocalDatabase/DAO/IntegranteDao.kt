package com.gomu.festup.LocalDatabase.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.LocalDatabase.Entities.Integrante
import kotlinx.coroutines.flow.Flow

@Dao
interface IntegranteDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIntegrante(integrante: Integrante)

    @Transaction
    @Query("SELECT * FROM Integrante")
    fun todosLosIntegrantes(): Flow<List<Integrante>>

    @Transaction
    @Query("SELECT username FROM Integrante WHERE nombreCuadrilla=:nombreCuadrilla")
    fun getIntegrantesCuadrilla(nombreCuadrilla: String): Flow<List<String>>
    @Transaction
    @Query("SELECT nombreCuadrilla FROM Integrante WHERE username=:username")
    fun getICuadrillasUser(username: String): Flow<List<String>>


    @Update
    fun editarIntegrante(integrante: Integrante): Int
}