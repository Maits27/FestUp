package com.gomu.festup.LocalDatabase.Repositories

import android.graphics.Bitmap
import com.gomu.festup.LocalDatabase.DAO.UsuarioDao
import com.gomu.festup.LocalDatabase.Entities.AuthUser
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.RemoteDatabase.HTTPClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface IUserRepository: ILoginSettings {
    fun logIn(email: String, login:Boolean): AuthUser?
    suspend fun exists(username: String):Boolean
    suspend fun insertUsuario(user: Usuario): Boolean
    fun todosLosUsuarios(): Flow<List<Usuario>>
    suspend fun verifyUser(username: String, passwd:String): Boolean
    suspend fun editarUsuario(user: Usuario): Int
    suspend fun getAQuienSigue(username: String): List<Usuario>
    suspend fun getSeguidores(username: String): List<Usuario>
    suspend fun getCuadrillasUsuario(username: String): List<Cuadrilla>
    suspend fun getUserProfile(username: String): Bitmap
    suspend fun setUserProfile(username: String, image: Bitmap): Bitmap
}
@Singleton
class UserRepository @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val httpClient: HTTPClient
) : IUserRepository {
    override fun logIn(email: String, login: Boolean): AuthUser? {
        TODO("Not yet implemented")
    }

    override suspend fun exists(username: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun insertUsuario(user: Usuario): Boolean {
        TODO("Not yet implemented")
    }

    override fun todosLosUsuarios(): Flow<List<Usuario>> {
        TODO("Not yet implemented")
    }

    override suspend fun verifyUser(username: String, passwd: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun editarUsuario(user: Usuario): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getAQuienSigue(username: String): List<Usuario> {
        TODO("Not yet implemented")
    }

    override suspend fun getSeguidores(username: String): List<Usuario> {
        TODO("Not yet implemented")
    }

    override suspend fun getCuadrillasUsuario(username: String): List<Cuadrilla> {
        // TODO PRIMERO RECOGER DEL REMOTO Y LUEGO PONERLOS AQUI
        return usuarioDao.getCuadrillasUsuario(username)
    }

    override suspend fun getUserProfile(username: String): Bitmap {
        TODO("Not yet implemented")
    }

    override suspend fun setUserProfile(username: String, image: Bitmap): Bitmap {
        TODO("Not yet implemented")
    }

    override suspend fun getLastLoggedUser(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun setLastLoggedUser(user: String) {
        TODO("Not yet implemented")
    }

}