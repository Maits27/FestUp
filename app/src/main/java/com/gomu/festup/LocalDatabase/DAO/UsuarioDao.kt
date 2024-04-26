package com.gomu.festup.LocalDatabase.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.LocalDatabase.Entities.CuadrillaWithUsuarios
import com.gomu.festup.LocalDatabase.Entities.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsuario(usuario: Usuario)

    @Transaction
    @Query("SELECT * FROM Usuario")
    fun todosLosUsuarios(): Flow<List<Usuario>>

    @Transaction
    @Query("SELECT * FROM Usuario WHERE username=:username")
    fun getUsuario(username: String): Flow<Usuario>

    @Transaction
    @Query("SELECT * FROM Usuario WHERE username IN (SELECT username FROM Integrante WHERE nombreCuadrilla = :nombreCuadrilla)")
    suspend fun getUsuariosDeCuadrilla(nombreCuadrilla: String): List<CuadrillaWithUsuarios>
    @Update
    fun editarUsuario(usuario: Usuario): Int
}