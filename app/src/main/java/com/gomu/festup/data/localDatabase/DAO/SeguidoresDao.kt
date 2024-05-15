package com.gomu.festup.data.localDatabase.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.data.localDatabase.Entities.Seguidores
import kotlinx.coroutines.flow.Flow

@Dao
interface SeguidoresDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSeguidores(seguidores: Seguidores)

    @Transaction
    @Query("SELECT seguidor FROM Seguidores WHERE seguido=:username")
    fun todosLosSeguidoresUsuario(username: String): Flow<List<String>>

    @Transaction
    @Query("SELECT seguido FROM Seguidores WHERE seguidor=:username")
    fun aQuienSigue(username: String): Flow<List<String>>

    @Query("SELECT * FROM Seguidores WHERE seguidor=:seguidor and seguido=:seguido")
    fun findUserSeguidor(seguidor: String, seguido: String): Seguidores

    @Update
    fun editarIntegrante(seguidores: Seguidores): Int

    @Delete
    fun deleteSeguidores(seguidores: Seguidores)

    @Transaction
    @Query("DELETE FROM Seguidores ")
    suspend fun eliminarSeguidores()
}