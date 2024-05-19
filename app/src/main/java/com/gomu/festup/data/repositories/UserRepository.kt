package com.gomu.festup.data.repositories

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.gomu.festup.data.http.AuthClient
import com.gomu.festup.data.http.HTTPClient
import com.gomu.festup.data.http.RemoteAuthUsuario
import com.gomu.festup.data.http.RemoteSeguidor
import com.gomu.festup.data.http.RemoteUsuario
import com.gomu.festup.data.http.UserExistsException
import com.gomu.festup.data.localDatabase.DAO.SeguidoresDao
import com.gomu.festup.data.localDatabase.DAO.UsuarioDao
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.Seguidores
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.data.repositories.preferences.ILoginSettings
import com.gomu.festup.utils.remoteSeguidorToSeguidor
import com.gomu.festup.utils.remoteUsuarioToUsuario
import com.gomu.festup.utils.toStringNuestro
import com.gomu.festup.utils.toStringRemoto
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/******************************************************************
 * Interfaz que define la API del listado de [Usuario] - Repositorio
 * Los métodos definidos son las acciones posibles para interactuar
 * con la BBDD
 *******************************************************************/
interface IUserRepository: ILoginSettings {

    suspend fun insertUsuario(usuario: Usuario, password: String): Boolean
    suspend fun verifyUser(username: String, password:String): Boolean
    fun recuperarSesion(token: String, refresh: String)
    fun getAQuienSigue(username: String): Flow<List<Usuario>>
    fun getSeguidores(username: String): Flow<List<Usuario>>
    fun getCuadrillasUsuario(username: String): Flow<List<Cuadrilla>>
    suspend fun newSeguidor(currentUsername: String, username: String)
    suspend fun alreadySiguiendo(currentUsername: String, username: String): Boolean
    suspend fun deleteSeguidores(currentUsername: String, usernameToUnfollow: String)
    suspend fun setUserProfile(username: String, image: Bitmap, context: Context):Boolean
    fun getUsuariosMenosCurrent(usuario: Usuario): Flow<List<Usuario>>
    fun getUsuario(username: String): Usuario?
    suspend fun descargarUsuarios()
    suspend fun descargarSeguidores()
    suspend fun subscribeUser(token: String, username: String)
    suspend fun unSubscribeUser(token: String, username: String)
    suspend fun editUsuario(username: String, email: String, nombre: String, fecha: Date, telefono: String) : Usuario
    suspend fun subscribeToUser(token: String, username: String)
    suspend fun unsubscribeFromUser(token: String, username: String)

}

/**
 * Implementación de [IUserRepository] e [ILoginSettings] que usan
 * Hilt para inyectar los parámetros necesarios.
 * Desde aquí se accede a los diferentes DAOs, que se encargan de
 * la conexión a la BBDD de Room y con la remota.
 * */
@Singleton
class UserRepository @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val seguidoresDao: SeguidoresDao,
    private val loginSettings: ILoginSettings,
    private val authClient: AuthClient,
    private val httpClient: HTTPClient
) : IUserRepository {
    /**
     * Métodos para gestionar el Registro, Login y Verificación del usuario.
     */
    override suspend fun getLastLoggedUser(): String = loginSettings.getLastLoggedUser()
    override suspend fun setLastLoggedUser(user: String) = loginSettings.setLastLoggedUser(user)
    override suspend fun getLastBearerToken(): String =loginSettings.getLastBearerToken()
    override suspend fun setLastBearerToken(token: String) =loginSettings.setLastBearerToken(token)
    override suspend fun getLastRefreshToken(): String = loginSettings.getLastRefreshToken()
    override suspend fun setLastRefreshToken(token: String) = loginSettings.setLastRefreshToken(token)

    override suspend fun insertUsuario(usuario: Usuario, password: String): Boolean {
        return try {
            val fechaNacimientoString = usuario.fechaNacimiento.toStringNuestro()
            val authUser= RemoteAuthUsuario(usuario.username,password,usuario.email,usuario.nombre,fechaNacimientoString, usuario.telefono)
            authClient.createUser(authUser)
            // Authenticate to get the bearer token
            authClient.authenticate(usuario.username, password)
            true
        }catch (e:Exception){
            Log.d("Exception crear usuario", e.toString())
            false
        }
    }

    override suspend fun verifyUser(username: String, password: String): Boolean {
        return try {
            authClient.authenticate(username,password)
            true
        } catch (e: UserExistsException) {
            false
        }
    }

    override fun recuperarSesion(token: String, refresh: String) {
        authClient.addBearerToken(token, refresh)
    }

    /**
     * Métodos para los [Seguidores] del usuario.
     */
    override suspend fun descargarSeguidores(){
        seguidoresDao.eliminarSeguidores()
        val seguidoresList = httpClient.getSeguidores()
        seguidoresList.map{seguidoresDao.insertSeguidores(remoteSeguidorToSeguidor(it))}
    }

    override fun getAQuienSigue(username: String): Flow<List<Usuario>> {
        return usuarioDao.getAQuienSigue(username)
    }

    override fun getSeguidores(username: String): Flow<List<Usuario>> {
        return usuarioDao.getSeguidores(username)
    }

    override suspend fun newSeguidor(currentUsername: String, username: String) {
        // Local
        seguidoresDao.insertSeguidores(Seguidores(seguidor = currentUsername, seguido = username))

        // Remote
        httpClient.insertSeguidor(RemoteSeguidor(seguidor = currentUsername, seguido = username))
    }

    override suspend fun alreadySiguiendo(currentUsername: String, username: String): Boolean {
        // Local
        val seguidores = seguidoresDao.findUserSeguidor(seguidor = currentUsername, seguido = username)
        return seguidores != null
    }

    override suspend fun deleteSeguidores(currentUsername: String, usernameToUnfollow: String) {
        // Local
        seguidoresDao.deleteSeguidores(Seguidores(seguidor = currentUsername, seguido = usernameToUnfollow))

        // Remote
        httpClient.deleteSeguidor(RemoteSeguidor(seguidor = currentUsername, seguido = usernameToUnfollow))
    }

    /**
     * Métodos para las [Cuadrilla] del usuario.
     */
    override fun getCuadrillasUsuario(username: String): Flow<List<Cuadrilla>> {
        return usuarioDao.getCuadrillasUsuario(username)
    }

    /**
     * Métodos para el perfil del [Usuario].
     */
    override suspend fun setUserProfile(username: String, image: Bitmap, context: Context): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                authClient.setUserProfile(username, image, context )
            }
            true
        } catch (e: ResponseException) {
            Log.e("HTTP", "Couldn't upload profile image.")
            e.printStackTrace()
            false
        } catch (e: Exception) {
            Log.e("HTTP", e.toString())
            false
        }
    }

    /**
     * Métodos para la información del [Usuario].
     */
    override fun getUsuariosMenosCurrent(usuario: Usuario): Flow<List<Usuario>> {
        return usuarioDao.getUsuariosMenosCurrent(usuario.username)
    }

    override fun getUsuario(username: String): Usuario? {
        return usuarioDao.getUsuario(username)
    }

    override suspend fun editUsuario(username: String, email: String, nombre: String, fecha: Date, telefono: String) : Usuario {
        usuarioDao.editarUsuario(username, email, nombre, fecha, telefono)
        httpClient.editUser(RemoteUsuario(username = username, email = email, nombre = nombre, fechaNacimiento = fecha.toStringRemoto(), telefono = telefono))
        return usuarioDao.getUsuario(username)!!
    }

    /**
     * Métodos exclusivos remoto.
     */

    override suspend fun descargarUsuarios(){
        usuarioDao.eliminarUsuarios()
        val userList = authClient.getUsuarios()
        userList.map{
            usuarioDao.insertUsuario(remoteUsuarioToUsuario(it))
        }
    }

    /**
     * Métodos para la suscribir y desuscribir al [Usuario].
     */

    override suspend fun subscribeUser(token: String, username: String){
        httpClient.subscribeUser(token, username)
    }
    override suspend fun unSubscribeUser(token: String, username: String){
        httpClient.unSubscribeUser(token, username)
    }

    override suspend fun subscribeToUser(token: String, username: String){
        httpClient.subscribeToUser(token, username)
    }

    override suspend fun unsubscribeFromUser(token: String, username: String){
        httpClient.unsubscribeFromUser(token, username)
    }

}