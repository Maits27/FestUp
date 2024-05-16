package com.gomu.festup.data.localDatabase.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.Usuario
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsuario(usuario: Usuario)

    @Transaction
    @Query("SELECT * FROM Usuario")
    fun todosLosUsuarios(): Flow<List<Usuario>>

    @Transaction
    @Query("SELECT * FROM Usuario WHERE username IN (SELECT seguido FROM Seguidores WHERE seguidor = :username)")
    fun getAQuienSigue(username: String): Flow<List<Usuario>>

    @Transaction
    @Query("SELECT * FROM Usuario WHERE username IN (SELECT seguidor FROM Seguidores WHERE seguido = :username)")
    fun getSeguidores(username: String): Flow<List<Usuario>>

    @Transaction
    @Query("SELECT * FROM Cuadrilla WHERE nombre IN (SELECT nombreCuadrilla FROM Integrante WHERE username = :username)")
    fun getCuadrillasUsuario(username: String): Flow<List<Cuadrilla>>

    @Transaction
    @Query("SELECT * FROM Usuario WHERE username=:username")
    fun getUsuario(username: String): Usuario

    @Update
    fun editarUsuario(usuario: Usuario): Int

    @Transaction
    @Query( "UPDATE Usuario " +
            "SET nombre=:nombre, email=:email, fechaNacimiento=:fecha, telefono=:telefono " +
            "WHERE username=:ususername")
    fun editarUsuario(ususername: String, email: String, nombre: String, fecha: Date, telefono: String)

    @Transaction
    @Query("SELECT * FROM Usuario WHERE username != :username")
    fun getUsuariosMenosCurrent(username: String): Flow<List<Usuario>>


    @Transaction
    @Query("DELETE FROM Usuario ")
    suspend fun eliminarUsuarios()
}