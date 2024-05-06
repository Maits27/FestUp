package com.gomu.festup.LocalDatabase.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.LocalDatabase.Entities.Seguidores
import kotlinx.coroutines.flow.Flow

@Dao
interface SeguidoresDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIntegrante(seguidores: Seguidores)

    @Transaction
    @Query("SELECT seguidor FROM Seguidores WHERE seguido=:username")
    fun todosLosSeguidoresUsuario(username: String): Flow<List<String>>

    @Transaction
    @Query("SELECT seguido FROM Seguidores WHERE seguidor=:username")
    fun aQuienSigue(username: String): Flow<List<String>>

    @Update
    fun editarIntegrante(seguidores: Seguidores): Int


    @Transaction
    @Query("DELETE FROM Seguidores ")
    suspend fun eliminarSeguidores()
}