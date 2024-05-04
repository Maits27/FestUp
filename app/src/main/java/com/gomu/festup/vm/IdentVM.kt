package com.gomu.festup.vm

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
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


    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun registrarUsuario(context: Context, username: String, password:String, email: String, nombre: String, fechaNacimiento: String, uri: Uri?): Usuario? {
        try{
            val fechaNacimientoDate = fechaNacimiento.formatearFecha()
            val newUser = Usuario(username,password,email,nombre,fechaNacimientoDate)
            val signInCorrect = userRepository.insertUsuario(newUser)
            if (signInCorrect){
                var imageBitmap: Bitmap? = null
                if (uri != null) {
                    val contentResolver: ContentResolver = context.contentResolver
                    val source = ImageDecoder.createSource(contentResolver, uri)
                    imageBitmap = ImageDecoder.decodeBitmap(source)
                    userRepository.setUserProfile(username, imageBitmap)
                }
            }

            return if (signInCorrect) newUser else null
        }catch (e:Exception){
            throw Exception("Excepcion al registrar usuario", e)
        }
    }

    suspend fun inicarSesion(username: String, password:String): Boolean {
        return userRepository.verifyUser(username, password)
    }
}