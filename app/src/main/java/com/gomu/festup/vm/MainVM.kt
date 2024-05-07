package com.gomu.festup.vm

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Integrante
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.LocalDatabase.Repositories.ICuadrillaRepository
import com.gomu.festup.LocalDatabase.Repositories.IEventoRepository
import com.gomu.festup.LocalDatabase.Repositories.IUserRepository
import com.gomu.festup.utils.localUriToBitmap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val userRepository: IUserRepository,
    private val cuadrillaRepository: ICuadrillaRepository,
    private val eventoRepository: IEventoRepository
): ViewModel() {
    var serverOk: MutableState<Boolean> = mutableStateOf(false)

    var currentUser: MutableState<Usuario?> = mutableStateOf(null)

    var usuarioMostrar: MutableState<Usuario?> = mutableStateOf(null)

    var cuadrillaMostrar: MutableState<Cuadrilla?> = mutableStateOf(null)

    var eventoMostrar: MutableState<Evento?> = mutableStateOf(null)

    var localizacion: MutableState<Location?> = mutableStateOf(null)

    var localizacionAMostrar: MutableState<LatLng?> = mutableStateOf(null)

    val alreadySiguiendo: MutableState<Boolean?> = mutableStateOf(null)


    /*****************************************************
     ****************** METODOS USUARIO ******************
     *****************************************************/

    fun descargarUsuarios(){
        Log.d("SERVER PETICION", "main dentro")

        CoroutineScope(Dispatchers.IO).launch {
            try{
                userRepository.descargarUsuarios()
                serverOk.value = true
            }catch (e: Exception) {
                Log.d("SERVER PETICION", e.toString())
            }
        }
    }


    fun descargarDatos(){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                cuadrillaRepository.descargarCuadrillas()
                cuadrillaRepository.descargarIntegrantes()
                eventoRepository.descargarEventos()
                eventoRepository.descargarUsuariosAsistentes()
                eventoRepository.descargarCuadrillasAsistentes()
                userRepository.descargarSeguidores()
            }catch (e: Exception) {
                Log.d("Excepccion al descargar datos",e.toString())
            }
        }

    }

    fun editUsuario(username: String, email: String, nombre: String, fecha: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            val usuario = withContext(Dispatchers.IO) {
                userRepository.editUsuario(username, email, nombre, fecha)
            }
            currentUser.value = usuario
        }
    }

    fun calcularEdad(usuario: Usuario): Int{
        val diff = Date().time - (usuario.fechaNacimiento.time)
        val edad = diff / (1000L * 60 * 60 * 24 * 365)
        return edad.toInt()
    }

    fun calcularEdadMediaEvento(evento: Evento): Int = runBlocking {
        var edades = mutableListOf<Int>()
        var usuarios = getUsuariosEvento(evento).first()
        for (usuario in usuarios){
            val edad = calcularEdad(usuario)
            edades.add(edad)
        }

        var cuadrillas = getCuadrillasEvento(evento).first()
        for (cuadrilla in cuadrillas){
            val usuarios2 = usuariosCuadrilla(cuadrilla).first()
            for (usuario in usuarios2){
                val edad = calcularEdad(usuario)
                edades.add(edad)
            }
        }

        edades.sum()/if(edades.size>0){edades.size} else {1}
    }
    fun numeroDeAsistentes(evento: Evento): Int = runBlocking {
        val usuarios = getUsuariosEvento(evento).first()
        val cuadrillas = getCuadrillasEvento(evento).first()
        usuarios.size + cuadrillas.size
    }


    fun getCuadrillasUsuario(usuario: Usuario): Flow<List<Cuadrilla>> {
        return userRepository.getCuadrillasUsuario(usuario.username)
    }

    fun getUsuariosMenosCurrent(usuario: Usuario): Flow<List<Usuario>>{
        return userRepository.getUsuariosMenosCurrent(usuario)
    }
    fun estaApuntado(usuario: Usuario, id: String): Boolean = runBlocking {
        eventoRepository.estaApuntado(usuario.username, id)
    }
    fun cuadrillasUsuarioApuntadas(usuario: Usuario, id: String): Flow<List<Cuadrilla>>  {
        return eventoRepository.cuadrillasUsuarioApuntadas(usuario.username, id)
    }
    fun cuadrillasUsuarioNoApuntadas(usuario: Usuario, id: String): Flow<List<Cuadrilla>>  {
        return eventoRepository.cuadrillasUsuarioNoApuntadas(usuario.username, id)
    }

    fun listaSeguidores(usuario: Usuario): Flow<List<Usuario>>{
        return userRepository.getSeguidores(usuario.username)
    }
    fun listaSeguidos(usuario: Usuario): Flow<List<Usuario>>{
        return userRepository.getAQuienSigue(usuario.username)
    }



    fun actualizarCurrentUser(username: String): Usuario = runBlocking(Dispatchers.IO){
        userRepository.getUsuario(username)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun updateUserImage(context: Context, username: String, uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uri != null) {
                val imageBitmap = context.localUriToBitmap(uri)
                userRepository.setUserProfile(username, imageBitmap)
            }
        }
    }


    /*****************************************************
     ****************** METODOS CUADRILLA ******************
     *****************************************************/
    suspend fun crearCuadrilla(cuadrilla: Cuadrilla, image: Bitmap?): Boolean  {
        return cuadrillaRepository.insertCuadrilla(currentUser.value!!.username,cuadrilla, image)
    }
    @RequiresApi(Build.VERSION_CODES.P)
    fun updateCuadrillaImage(context: Context, nombre: String, uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uri != null) {
                val imageBitmap = context.localUriToBitmap(uri)
                cuadrillaRepository.setCuadrillaProfile(nombre, imageBitmap)
            }
        }
    }
    fun usuariosCuadrilla(cuadrilla: Cuadrilla = cuadrillaMostrar.value!!): Flow<List<Usuario>> {
        return cuadrillaRepository.usuariosCuadrilla(cuadrilla.nombre)
    }

    fun eliminarIntegrante(cuadrilla: Cuadrilla)  {
        viewModelScope.launch(Dispatchers.IO) {
            cuadrillaRepository.eliminarIntegrante(cuadrilla,currentUser.value!!.username)
        }
    }

    fun getCuadrillas(): Flow<List<Cuadrilla>> {
        return cuadrillaRepository.getCuadrillas()
    }


    fun setCuadrillaProfile(context: Context, uri: Uri?, nombre: String): Boolean = runBlocking {
        var ivImage = ImageView(context)
        ivImage.setImageURI(uri)
        val drawable: Drawable = ivImage.drawable
        if (drawable is BitmapDrawable) {
            cuadrillaRepository.setCuadrillaProfile(nombre, drawable.bitmap)
        }else{
            false
        }
    }

    fun getCuadrillaAccessToken(nombre: String): String{
        return cuadrillaRepository.getCuadrillaAccessToken(nombre)
    }

    fun agregarIntegrante(nombreUsuario: String, nombreCuadrilla: String){
        viewModelScope.launch(Dispatchers.IO) {
            cuadrillaRepository.insertUser(nombreUsuario, nombreCuadrilla)
        }
    }


    /*****************************************************
     ****************** METODOS EVENTO ******************
     *****************************************************/

    suspend fun insertarEvento(evento: Evento, image: Bitmap?): Boolean {
        return eventoRepository.insertarEvento(evento, currentUser.value!!.username, image)
    }

    fun getEventos(): Flow<List<Evento>> {
        return eventoRepository.todosLosEventos()
    }
    fun apuntarse(usuario: Usuario, evento: Evento){
        viewModelScope.launch(Dispatchers.IO) {
            eventoRepository.apuntarse(usuario, evento.id)
        }
    }
    fun desapuntarse(usuario: Usuario, evento: Evento){
        viewModelScope.launch(Dispatchers.IO) {
            eventoRepository.desapuntarse(usuario, evento.id)
        }
    }
    fun apuntarse(cuadrilla: Cuadrilla, evento: Evento){
        viewModelScope.launch(Dispatchers.IO) {
            eventoRepository.apuntarse(cuadrilla, evento.id)
        }
    }
    fun desapuntarse(cuadrilla: Cuadrilla, evento: Evento){
        viewModelScope.launch(Dispatchers.IO) {
            eventoRepository.desapuntarse(cuadrilla, evento.id)
        }
    }

    fun getUsuariosEvento(evento: Evento): Flow<List<Usuario>>{
        return eventoRepository.usuariosEvento(evento.id)
    }
    fun getCuadrillasEvento(evento: Evento): Flow<List<Cuadrilla>>{
        return eventoRepository.cuadrillasEvento(evento.id)
    }

    fun eventosUsuario(usuario: Usuario): Flow<List<Evento>> {
        return  eventoRepository.eventosUsuario(usuario.username)
    }

    fun eventosSeguidos(usuario: Usuario): Flow<List<Evento>> {
        return eventoRepository.eventosSeguidos(usuario.username)
    }


    fun eventosCuadrilla(cuadrilla: Cuadrilla): Flow<List<Cuadrilla>> {
        return eventoRepository.cuadrillasEvento(cuadrilla.nombre)
    }

    fun setEventoProfile(context: Context, uri: Uri?, id: String): Boolean = runBlocking {
        var ivImage = ImageView(context)
        ivImage.setImageURI(uri)
        val drawable: Drawable = ivImage.drawable
        if (drawable is BitmapDrawable) {
            eventoRepository.setEventoProfileImage(id, drawable.bitmap)
        }else{
            false
        }
    }


    /*****************************************************
     ****************** METODOS INTEGRANTE ******************
     *****************************************************/

    fun getIntegrante(cuadrilla: Cuadrilla, usuario: Usuario): Flow<List<Integrante>> {
        val integrante =cuadrillaRepository.pertenezcoCuadrilla(cuadrilla,usuario)
        return integrante
    }

    fun getIntegrantes(): Flow<List<Integrante>> {
        return cuadrillaRepository.getIntegrantes()
    }

    /*****************************************************
     ****************** METODOS INTEGRANTE ******************
     *****************************************************/

    fun newSiguiendo(followedUsername: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.newSeguidor(currentUser.value!!.username, followedUsername)
            alreadySiguiendo.value = true
        }
    }

    fun alreadySiguiendo(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            alreadySiguiendo.value = userRepository.alreadySiguiendo(currentUser.value!!.username, username)
        }
    }

    fun unfollow(usernameToUnfollow: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteSeguidores(currentUser.value!!.username, usernameToUnfollow)
            alreadySiguiendo.value = false
        }
    }


    fun subscribeUser() {
        val fcm = FirebaseMessaging.getInstance()
        // Eliminar el token FCM actual
        fcm.deleteToken().addOnSuccessListener {
            // Obtener el nuevo token
            fcm.token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                viewModelScope.launch(Dispatchers.IO) {
                    try{
                        userRepository.subscribeUser(task.result)
                        Log.d("FCM", "Usuario suscrito")
                    }
                    catch (e:Exception){
                        Log.d("Exception", e.printStackTrace().toString())
                    }

                }
            })
        }
    }
}
