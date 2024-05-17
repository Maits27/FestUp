package com.gomu.festup.ui.vm

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.data.repositories.IUserRepository
import com.gomu.festup.utils.formatearFecha
import com.gomu.festup.utils.localUriToBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IdentVM @Inject constructor(
    private val userRepository: IUserRepository
): ViewModel() {


    var selectedTabLogin: MutableState<Int> = mutableIntStateOf(0)

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun registrarUsuario(
        context: Context,
        username: String,
        password:String,
        email: String,
        nombre: String,
        fechaNacimiento: String,
        telefono: String,
        uri: Uri?
    ): Usuario? {
        try{
            val fechaNacimientoDate = fechaNacimiento.formatearFecha()
            val newUser = Usuario(username,email,nombre,fechaNacimientoDate, telefono)
            val signInCorrect = userRepository.insertUsuario(newUser,password)
            if (signInCorrect){
                if (uri != null && uri != Uri.parse("http://34.71.128.243/userProfileImages/no-user.png")) {
                    val imageBitmap = context.localUriToBitmap(uri)
                    Log.d("Sign up", "Setting user image")
                    userRepository.setUserProfile(username, imageBitmap)
                    Log.d("Sign up", "User image sended to server")
                }
            }
            Log.d("Sign up", "Sign up correct $signInCorrect")
            return if (signInCorrect) newUser else null
        }catch (e:Exception){
            Log.d("Excepcion al registrar usuario", e.toString())
            throw Exception("Excepcion al registrar usuario", e)
        }
    }

    suspend fun iniciarSesion(username: String, password:String): Boolean {
        return userRepository.verifyUser(username, password)
    }
    fun recuperarSesion(token: String, refresh: String){
        userRepository.recuperarSesion(token, refresh)
    }
}