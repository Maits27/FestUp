package com.gomu.festup.vm

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.LocalDatabase.Repositories.IUserRepository
import com.gomu.festup.utils.formatearFecha
import com.gomu.festup.utils.localUriToBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class IdentVM @Inject constructor(
    private val userRepository: IUserRepository
): ViewModel() {


    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun registrarUsuario(
        context: Context,
        username: String,
        password:String,
        email: String,
        nombre: String,
        fechaNacimiento: String,
        uri: Uri?
    ): Usuario? {
        try{
            val fechaNacimientoDate = fechaNacimiento.formatearFecha()
            val newUser = Usuario(username,email,nombre,fechaNacimientoDate)
            val signInCorrect = userRepository.insertUsuario(newUser,password)
            if (signInCorrect){
                if (uri != null && uri != Uri.parse("http://34.16.74.167/userProfileImages/no-user.png")) {
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