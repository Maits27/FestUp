package com.gomu.festup.vm

import androidx.lifecycle.ViewModel
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.LocalDatabase.Repositories.IUserRepository
import com.gomu.festup.utils.formatearFecha
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class IdentVM @Inject constructor(
    private val userRepository: IUserRepository
): ViewModel() {


    suspend fun registrarUsuario(username: String, password:String, email: String, nombre: String, fechaNacimiento: String, profileImagePath: String): Usuario? {
        try{
            val fechaNacimientoDate = fechaNacimiento.formatearFecha()
            val newUser = Usuario(username,password,email,nombre,fechaNacimientoDate,profileImagePath)
            val signInCorrect = userRepository.insertUsuario(newUser)
            return if (signInCorrect) newUser else null
        }catch (e:Exception){
            throw Exception("Excepcion al registrar usuario", e)
        }
    }

    suspend fun inicarSesion(username: String, password:String): Boolean {
        return userRepository.verifyUser(username, password)
    }
}