package com.gomu.festup.data.localDatabase.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.Usuario
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Consultas con respecto a los [Usuario] en la base de datos.
 */
@Dao
interface UsuarioDao {
    /////////////// Funciones Insert ///////////////
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsuario(usuario: Usuario)

    /////////////// Funciones Select ///////////////
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
    fun getUsuario(username: String): Usuario?

    @Transaction
    @Query("SELECT * FROM Usuario WHERE username != :username")
    fun getUsuariosMenosCurrent(username: String): Flow<List<Usuario>>

    /////////////// Funciones Update ///////////////
    @Transaction
    @Query( "UPDATE Usuario " +
            "SET nombre=:nombre, email=:email, fechaNacimiento=:fecha, telefono=:telefono " +
            "WHERE username=:ususername")
    fun editarUsuario(ususername: String, email: String, nombre: String, fecha: Date, telefono: String)

    /////////////// Funciones Delete ///////////////
    @Transaction
    @Query("DELETE FROM Usuario ")
    suspend fun eliminarUsuarios()
}