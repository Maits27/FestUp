package com.gomu.festup.data.localDatabase.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.Seguidores
import kotlinx.coroutines.flow.Flow

/**
 * Consultas con respecto a los [Seguidores] en la base de datos.
 */
@Dao
interface SeguidoresDao {
    /////////////// Funciones Insert ///////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSeguidores(seguidores: Seguidores)

    /////////////// Funciones Select ///////////////
    @Query("SELECT * FROM Seguidores WHERE seguidor=:seguidor and seguido=:seguido")
    fun findUserSeguidor(seguidor: String, seguido: String): Seguidores

    /////////////// Funciones Delete ///////////////
    @Delete
    fun deleteSeguidores(seguidores: Seguidores)

    @Transaction
    @Query("DELETE FROM Seguidores ")
    suspend fun eliminarSeguidores()
}