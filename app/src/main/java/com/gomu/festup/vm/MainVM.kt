package com.gomu.festup.vm

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.LocalDatabase.Repositories.ICuadrillaRepository
import com.gomu.festup.LocalDatabase.Repositories.IEventoRepository
import com.gomu.festup.LocalDatabase.Repositories.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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


    /*****************************************************
     ****************** METODOS USUARIO ******************
     *****************************************************/

    fun calcularEdad(usuario: Usuario): Int{
        val diff = Date().time - (usuario.fechaNacimiento.time)
        val edad = diff / (1000L * 60 * 60 * 24 * 365)
        return edad.toInt()
    }
    fun getCuadrillasUsuario(usuario: Usuario): List<Cuadrilla> = runBlocking {
        usuario.let { userRepository.getCuadrillasUsuario(it.username) }
    }

    /*****************************************************
     ****************** METODOS CUADRILLA ******************
     *****************************************************/
    fun crearCuadrilla(cuadrilla: Cuadrilla) = runBlocking {
        val creada = currentUser.value?.let { cuadrillaRepository.insertCuadrilla(it.username, cuadrilla) }
        if (creada?:false){
            cuadrillaMostrar.value = cuadrilla
        }
    }
    fun usuariosCuadrilla(): List<Usuario> = runBlocking {
        cuadrillaMostrar.value?.let { cuadrillaRepository.usuariosCuadrilla(it.nombre) }?: emptyList()
    }

}
