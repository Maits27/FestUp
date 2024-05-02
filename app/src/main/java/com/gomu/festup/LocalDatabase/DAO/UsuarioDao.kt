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
    @Query("SELECT * FROM Cuadrilla WHERE nombre IN (SELECT nombreCuadrilla FROM Integrante WHERE username = :usernameUsuario)")
    suspend fun getCuadrillasDeUsuario(usernameUsuario: String): List<CuadrillaWithUsuarios>

    @Transaction
    @Query("SELECT * FROM Usuario WHERE username IN (SELECT seguido FROM Seguidores WHERE seguidor = :username)")
    suspend fun getAQuienSigue(username: String): List<Usuario>

    @Transaction
    @Query("SELECT * FROM Usuario WHERE username IN (SELECT seguidor FROM Seguidores WHERE seguido = :username)")
    suspend fun getSeguidores(username: String): List<Usuario>

    @Transaction
    @Query("SELECT * FROM Cuadrilla WHERE nombre IN (SELECT nombreCuadrilla FROM Integrante WHERE username = :username)")
    fun getCuadrillasUsuario(username: String): Flow<List<Cuadrilla>>

    @Transaction
    @Query("SELECT * FROM Usuario WHERE username=:username")
    fun getUsuario(username: String): Usuario


    @Update
    fun editarUsuario(usuario: Usuario): Int
}