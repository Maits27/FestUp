package com.gomu.festup.LocalDatabase.Repositories

import android.graphics.Bitmap
import android.util.Log
import com.gomu.festup.LocalDatabase.DAO.UsuarioDao
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Integrante
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.RemoteDatabase.AuthClient
import com.gomu.festup.RemoteDatabase.AuthUser
import com.gomu.festup.RemoteDatabase.AuthenticationException
import com.gomu.festup.RemoteDatabase.HTTPClient
import com.gomu.festup.RemoteDatabase.UserExistsException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import java.text.SimpleDateFormat
import javax.inject.Inject
import javax.inject.Singleton

interface IUserRepository: ILoginSettings {
    fun logIn(email: String, login:Boolean): AuthUser?
    suspend fun exists(username: String):Boolean
    suspend fun insertUsuario(user: Usuario): Boolean
    fun todosLosUsuarios(): Flow<List<Usuario>>
    suspend fun verifyUser(username: String, passwd:String): Boolean
    suspend fun editarUsuario(user: Usuario): Int
    fun getAQuienSigue(username: String): Flow<List<Usuario>>
    fun getSeguidores(username: String): Flow<List<Usuario>>
    fun getCuadrillasUsuario(username: String): Flow<List<Cuadrilla>>
    suspend fun getUserProfile(username: String): Bitmap
    suspend fun setUserProfile(username: String, image: Bitmap):Boolean

    fun getUsuariosMenosCurrent(usuario: Usuario): Flow<List<Usuario>>

    fun getUsuario(username: String): Usuario
}
@Singleton
class UserRepository @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val authClient: AuthClient,
    private val httpClient: HTTPClient
) : IUserRepository {
    override fun logIn(email: String, login: Boolean): AuthUser? {
        TODO("Not yet implemented")
    }

    override suspend fun exists(username: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun insertUsuario(usuario: Usuario): Boolean {
        return try {
            usuarioDao.insertUsuario(usuario) // TODO QUITAR (de momento hace falta pq no hemos descargado todos los datos)
            val fechaNacimientoString: String = SimpleDateFormat("dd/MM/yyyy").format(usuario.fechaNacimiento)
            val authUser= AuthUser(usuario.username,usuario.password,usuario.email,usuario.nombre,fechaNacimientoString,usuario.profileImagePath)
            authClient.createUser(authUser)
            true
        }catch (e:Exception){
            Log.d("Exception crear usuario", e.toString())
            false
        }
    }

    override fun todosLosUsuarios(): Flow<List<Usuario>> {
        TODO("Not yet implemented")
    }

    override suspend fun verifyUser(username: String, password: String): Boolean {
        return try {
            //usuarioDao.verifyUser(username,password)
            authClient.authenticate(username,password)
            true
        } catch (e: UserExistsException) {
            false
        }
    }

    override suspend fun editarUsuario(user: Usuario): Int {
        TODO("Not yet implemented")
    }

    override fun getAQuienSigue(username: String): Flow<List<Usuario>> {
        return usuarioDao.getAQuienSigue(username)
    }

    override fun getSeguidores(username: String): Flow<List<Usuario>> {
        TODO("Not yet implemented")
    }

    override fun getCuadrillasUsuario(username: String): Flow<List<Cuadrilla>> {
        // TODO PRIMERO RECOGER DEL REMOTO Y LUEGO PONERLOS AQUI
        return usuarioDao.getCuadrillasUsuario(username)
    }

    override suspend fun getUserProfile(username: String): Bitmap {
        TODO("Not yet implemented")
    }

    override suspend fun setUserProfile(username: String, image: Bitmap): Boolean {
        return try {
            httpClient.setUserProfile(username, image)
            true
        } catch (e: ResponseException) {
            Log.e("HTTP", "Couldn't upload profile image.")
            e.printStackTrace()
            false
        }
    }

    override suspend fun getLastLoggedUser(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun setLastLoggedUser(user: String) {
        TODO("Not yet implemented")
    }

    override fun getUsuariosMenosCurrent(usuario: Usuario): Flow<List<Usuario>> {
        // TODO PRIMERO RECOGER DEL REMOTO Y LUEGO PONERLOS AQUI
        return usuarioDao.getUsuariosMenosCurrent(usuario.username)
    }

    override fun getUsuario(username: String): Usuario{
        return usuarioDao.getUsuario(username)
    }

}