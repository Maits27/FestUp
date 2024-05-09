package com.gomu.festup.LocalDatabase.Repositories

import android.graphics.Bitmap
import android.util.Log
import com.gomu.festup.LocalDatabase.DAO.SeguidoresDao
import com.gomu.festup.LocalDatabase.DAO.UsuarioDao
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Integrante
import com.gomu.festup.LocalDatabase.Entities.Seguidores
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.RemoteDatabase.AuthClient
import com.gomu.festup.RemoteDatabase.AuthenticationException
import com.gomu.festup.RemoteDatabase.HTTPClient
import com.gomu.festup.RemoteDatabase.RemoteAuthUsuario
import com.gomu.festup.RemoteDatabase.RemoteSeguidor
import com.gomu.festup.RemoteDatabase.RemoteUsuario
import com.gomu.festup.RemoteDatabase.UserExistsException
import com.gomu.festup.utils.formatearFecha
import com.gomu.festup.utils.remoteSeguidorToSeguidor
import com.gomu.festup.utils.remoteUsuarioToUsuario
import com.gomu.festup.utils.toStringNuestro
import com.gomu.festup.utils.toStringRemoto
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

interface IUserRepository: ILoginSettings {

    suspend fun insertUsuario(user: Usuario, password: String): Boolean
    suspend fun verifyUser(username: String, passwd:String): Boolean
    fun getAQuienSigue(username: String): Flow<List<Usuario>>
    fun getSeguidores(username: String): Flow<List<Usuario>>
    fun getCuadrillasUsuario(username: String): Flow<List<Cuadrilla>>
    suspend fun newSeguidor(currentUsername: String, username: String): Unit
    suspend fun alreadySiguiendo(currentUsername: String, username: String): Boolean
    suspend fun deleteSeguidores(currentUsername: String, usernameToUnfollow: String)
    suspend fun setUserProfile(username: String, image: Bitmap):Boolean

    fun getUsuariosMenosCurrent(usuario: Usuario): Flow<List<Usuario>>

    fun getUsuario(username: String): Usuario

    suspend fun descargarUsuarios()

    suspend fun descargarSeguidores()

    suspend fun subscribeUser(token: String)
    suspend fun unSubscribeUser(token: String)
    suspend fun editUsuario(username: String, email: String, nombre: String, fecha: Date) : Usuario

    suspend fun subscribeToUser(token: String, username: String)

    suspend fun unsubscribeFromUser(token: String, username: String)

}
@Singleton
class UserRepository @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val seguidoresDao: SeguidoresDao,
    private val loginSettings: ILoginSettings,
    private val authClient: AuthClient,
    private val httpClient: HTTPClient
) : IUserRepository {
    override suspend fun getLastLoggedUser(): String = loginSettings.getLastLoggedUser()
    override suspend fun setLastLoggedUser(user: String) = loginSettings.setLastLoggedUser(user)
    override suspend fun getLastBearerToken(): String {
        TODO("Not yet implemented")
    }

    override suspend fun setLastBearerToken(token: String) {
        TODO("Not yet implemented")
    }

    override suspend fun insertUsuario(usuario: Usuario, password: String): Boolean {
        return try {
            usuarioDao.insertUsuario(usuario) // TODO QUITAR (de momento hace falta pq no hemos descargado todos los datos)
            val fechaNacimientoString = usuario.fechaNacimiento.toStringNuestro()
            val authUser= RemoteAuthUsuario(usuario.username,password,usuario.email,usuario.nombre,fechaNacimientoString)
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
            //usuarioDao.verifyUser(username,password)
            authClient.authenticate(username,password)
            true
        } catch (e: UserExistsException) {
            false
        }
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
        Log.d("Seguidores", "$seguidores")
        return seguidores != null
    }

    override suspend fun deleteSeguidores(currentUsername: String, usernameToUnfollow: String) {
        // Local
        seguidoresDao.deleteSeguidores(Seguidores(seguidor = currentUsername, seguido = usernameToUnfollow))

        // Remote
        httpClient.deleteSeguidor(RemoteSeguidor(seguidor = currentUsername, seguido = usernameToUnfollow))
    }

    override fun getCuadrillasUsuario(username: String): Flow<List<Cuadrilla>> {
        // TODO PRIMERO RECOGER DEL REMOTO Y LUEGO PONERLOS AQUI
        return usuarioDao.getCuadrillasUsuario(username)
    }


    override suspend fun setUserProfile(username: String, image: Bitmap): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                authClient.setUserProfile(username, image)
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

    override fun getUsuariosMenosCurrent(usuario: Usuario): Flow<List<Usuario>> {
        // TODO PRIMERO RECOGER DEL REMOTO Y LUEGO PONERLOS AQUI
        return usuarioDao.getUsuariosMenosCurrent(usuario.username)
    }

    override fun getUsuario(username: String): Usuario{
        return usuarioDao.getUsuario(username)
    }

    override suspend fun descargarUsuarios(){
        usuarioDao.eliminarUsuarios()
        val userList = authClient.getUsuarios()
        userList.map{
            Log.d("lista creada", it.username)
            usuarioDao.insertUsuario(remoteUsuarioToUsuario(it))
        }
    }

    override suspend fun descargarSeguidores(){
        seguidoresDao.eliminarSeguidores()
        val seguidoresList = httpClient.getSeguidores()
        seguidoresList.map{seguidoresDao.insertSeguidores(remoteSeguidorToSeguidor(it))}
    }

    override suspend fun subscribeUser(token: String){
        httpClient.subscribeUser(token)
    }
    override suspend fun unSubscribeUser(token: String){
        httpClient.unSubscribeUser(token)
    }

    override suspend fun editUsuario(username: String, email: String, nombre: String, fecha: Date) : Usuario {
        usuarioDao.editarUsuario(username, email, nombre, fecha)
        httpClient.editUser(RemoteUsuario(username = username, email = email, nombre = nombre, fechaNacimiento = fecha.toStringRemoto()))
        return usuarioDao.getUsuario(username)
    }
    override suspend fun subscribeToUser(token: String, username: String){
        httpClient.subscribeToUser(token, username)
    }

    override suspend fun unsubscribeFromUser(token: String, username: String){
        httpClient.unsubscribeFromUser(token, username)
    }



}