package com.gomu.festup.LocalDatabase.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.CuadrillaWithUsuarios
import com.gomu.festup.LocalDatabase.Entities.Integrante
import com.gomu.festup.LocalDatabase.Entities.Usuario
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
    @Query("SELECT * FROM Usuario WHERE username IN (SELECT username FROM Integrante WHERE nombreCuadrilla = :nombreCuadrilla)")
    fun getUsuariosDeCuadrilla(nombreCuadrilla: String): Flow<List<Usuario>>

    @Update
    fun editarCuadrilla(cuadrilla: Cuadrilla): Int


    @Delete(entity = Cuadrilla::class)
    fun eliminarCuadrilla(cuadrilla: Cuadrilla)


    @Transaction
    @Query("SELECT * FROM Cuadrilla")
    fun getCuadrillas(): Flow<List<Cuadrilla>>




    // NUEVO integranteDAO?? TODO
    @Transaction
    @Query("SELECT * FROM Integrante WHERE username=:username and nombreCuadrilla=:nombreCuadrilla")
    fun pertenezcoCuadrilla(nombreCuadrilla:String, username:String): Flow<List<Integrante>>

    @Transaction
    @Query("SELECT * FROM Integrante ")
    fun getIntegrantes(): Flow<List<Integrante>>

}