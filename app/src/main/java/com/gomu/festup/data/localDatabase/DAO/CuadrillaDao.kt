package com.gomu.festup.data.localDatabase.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.Usuario
import kotlinx.coroutines.flow.Flow


/**
 * Consultas con respecto a las [Cuadrilla] en la base de datos.
 */
@Dao
interface CuadrillaDao {
    /////////////// Funciones Insert ///////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCuadrilla(cuadrilla: Cuadrilla)

    /////////////// Funciones Select ///////////////
    @Transaction
    @Query("SELECT * FROM Usuario WHERE username IN (SELECT username FROM Integrante WHERE nombreCuadrilla = :nombreCuadrilla)")
    fun getUsuariosDeCuadrilla(nombreCuadrilla: String): Flow<List<Usuario>>

    @Transaction
    @Query("SELECT * FROM Cuadrilla")
    fun getCuadrillas(): Flow<List<Cuadrilla>>


    /////////////// Funciones Delete ///////////////
    @Delete(entity = Cuadrilla::class)
    fun eliminarCuadrilla(cuadrilla: Cuadrilla)

    @Transaction
    @Query("DELETE FROM Cuadrilla ")
    suspend fun eliminarCuadrillas()
}