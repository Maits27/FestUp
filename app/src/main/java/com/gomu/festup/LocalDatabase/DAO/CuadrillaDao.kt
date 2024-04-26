package com.gomu.festup.LocalDatabase.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.CuadrillaWithUsuarios
import kotlinx.coroutines.flow.Flow

@Dao
interface CuadrillaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCuadrilla(cuadrilla: Cuadrilla)

    @Transaction
    @Query("SELECT * FROM Cuadrilla")
    fun todasLasCuadrillas(): Flow<List<Cuadrilla>>

    @Transaction
    @Query("SELECT * FROM Cuadrilla WHERE nombre=:nombre")
    fun getCuadrilla(nombre:String): Flow<Cuadrilla>

    @Transaction
    @Query("SELECT * FROM Cuadrilla WHERE nombre IN (SELECT nombreCuadrilla FROM Integrante WHERE username = :usernameUsuario)")
    suspend fun getCuadrillasDeUsuario(usernameUsuario: String): List<CuadrillaWithUsuarios>

    @Update
    fun editarCuadrilla(cuadrilla: Cuadrilla): Int
}