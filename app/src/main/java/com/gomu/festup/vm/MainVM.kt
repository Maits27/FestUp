package com.gomu.festup.vm

import android.util.Log
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.text.SimpleDateFormat
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



    /*****************************************************
     ****************** METODOS CUADRILLA ******************
     *****************************************************/
    suspend fun crearCuadrilla(cuadrilla: Cuadrilla)  {
        val creada = currentUser.value?.let { cuadrillaRepository.insertCuadrilla(it.username, cuadrilla) }
        if (creada?:false){
            cuadrillaMostrar.value = cuadrilla
        }
        Log.d("Cuadrilla creada", cuadrillaMostrar.value?.nombre?:"")
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




    /*****************************************************
     ****************** METODOS EVENTO ******************
     *****************************************************/

    suspend fun insertarEvento(evento: Evento) {
        eventoRepository.insertEvento(evento)
        eventoRepository.insertUsuarioAsistente(currentUser.value!!.username, evento.id)
    }

    fun getEventos(): Flow<List<Evento>> {
        return eventoRepository.todosLosEventos()
    }

    fun eventosUsuario(usuario: Usuario): Flow<List<Evento>> {
        return  eventoRepository.eventosUsuario(usuario.username)
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
