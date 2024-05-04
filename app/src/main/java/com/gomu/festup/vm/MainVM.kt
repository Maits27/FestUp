package com.gomu.festup.vm

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Integrante
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.LocalDatabase.Repositories.ICuadrillaRepository
import com.gomu.festup.LocalDatabase.Repositories.IEventoRepository
import com.gomu.festup.LocalDatabase.Repositories.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val userRepository: IUserRepository,
    private val cuadrillaRepository: ICuadrillaRepository,
    private val eventoRepository: IEventoRepository
): ViewModel() {

    var currentUser: MutableState<Usuario?> = mutableStateOf(null)

    var usuarioMostrar: MutableState<Usuario?> = mutableStateOf(null)

    var cuadrillaMostrar: MutableState<Cuadrilla?> = mutableStateOf(null)

    var eventoMostrar: MutableState<Evento?> = mutableStateOf(null)

    var localizacion: MutableState<Location?> = mutableStateOf(null)
    var localizacionAMostrar: MutableState<Pair<Double, Double>?> = mutableStateOf(null)


    /*****************************************************
     ****************** METODOS USUARIO ******************
     *****************************************************/

    fun calcularEdad(usuario: Usuario): Int{
        val diff = Date().time - (usuario.fechaNacimiento.time)
        val edad = diff / (1000L * 60 * 60 * 24 * 365)
        return edad.toInt()
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

    fun listaSeguidores(usuario: Usuario): Flow<List<Usuario>>{
        return userRepository.getSeguidores(usuario.username)
    }
    fun listaSeguidos(usuario: Usuario): Flow<List<Usuario>>{
        return userRepository.getAQuienSigue(usuario.username)
    }

    fun setUserProfile(context: Context, uri: Uri?, username: String): Boolean = runBlocking {
        var ivImage = ImageView(context)
        Log.d("IMAGEN", "1")
        ivImage.setImageURI(uri)
        Log.d("IMAGEN", "2")
        val drawable: Drawable = ivImage.drawable
        Log.d("IMAGEN", "3")
        if (drawable is BitmapDrawable) {
            Log.d("IMAGEN", "4")
            userRepository.setUserProfile(username, drawable.bitmap)
        }else{
            Log.d("IMAGEN", "5")
            false
        }
        false
    }

    fun actualizarCurrentUser(username: String): Usuario {
        return userRepository.getUsuario(username)
    }




    /*****************************************************
     ****************** METODOS CUADRILLA ******************
     *****************************************************/
    suspend fun crearCuadrilla(cuadrilla: Cuadrilla): Boolean  {
        return cuadrillaRepository.insertCuadrilla(currentUser.value!!.username,cuadrilla)
    }
    fun usuariosCuadrilla(): Flow<List<Usuario>> {
        return cuadrillaRepository.usuariosCuadrilla(cuadrillaMostrar.value!!.nombre)
    }

    fun eliminarCuadrilla(cuadrilla: Cuadrilla)  {
        viewModelScope.launch(Dispatchers.IO) {
            cuadrillaRepository.eliminarCuadrilla(cuadrilla)
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


    /*****************************************************
     ****************** METODOS EVENTO ******************
     *****************************************************/

    suspend fun insertarEvento(evento: Evento): Boolean {
        return eventoRepository.insertarEvento(evento,currentUser.value!!.username)
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

    fun eventosUsuario(usuario: Usuario): Flow<List<Evento>> {
        return  eventoRepository.eventosUsuario(usuario.username)
    }

    fun getUsuariosEvento(evento: Evento): Flow<List<Usuario>>{
        return eventoRepository.usuariosEvento(evento.id)
    }
    fun getCuadrillasEvento(evento: Evento): Flow<List<Cuadrilla>>{
        return eventoRepository.cuadrillasEvento(evento.id)
    }

    fun eventosSeguidos(usuario: Usuario): Flow<List<Evento>> {
        // Personas a las que sigues
        val seguidos = userRepository.getAQuienSigue(usuario.username)
        // Sus eventos
        var eventos = MutableStateFlow<List<Evento>>(emptyList())
        seguidos.map { usuarios ->
            usuarios.map {usuario ->
                val eventos2 = eventosUsuario(usuario)
                eventos.combine(eventos2){f1, f2 ->
                    Pair(f1, f2)
                }
            }
        }
        return eventos
    }
    fun eventosCuadrilla(cuadrilla: Cuadrilla): Flow<List<Cuadrilla>> {
        return eventoRepository.cuadrillasEvento(cuadrilla.nombre)
    }

    fun setEventoProfile(context: Context, uri: Uri?, id: String): Boolean = runBlocking {
        var ivImage = ImageView(context)
        ivImage.setImageURI(uri)
        val drawable: Drawable = ivImage.drawable
        if (drawable is BitmapDrawable) {
            eventoRepository.setEventoProfile(id, drawable.bitmap)
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

}
