package com.gomu.festup.vm

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.LocalDatabase.Repositories.IUserRepository
import com.gomu.festup.utils.formatearFecha
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class IdentVM @Inject constructor(
    private val userRepository: IUserRepository
): ViewModel() {


    suspend fun registrarUsuario(
        username: String,
        password:String,
        email: String,
        nombre: String,
        fechaNacimiento: String,
    ): Usuario? {
        try{
            val fechaNacimientoDate = fechaNacimiento.formatearFecha()
            val newUser = Usuario(username,password,email,nombre,fechaNacimientoDate)
            val signInCorrect = userRepository.insertUsuario(newUser)
            return if (signInCorrect) newUser else null
        }catch (e:Exception){
            throw Exception("Excepcion al registrar usuario", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun registrarFoto(context: Context, uri: Uri?, username: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var imageBitmap: Bitmap? = null
                if (uri != null && uri != Uri.parse("http://34.16.74.167/userProfileImages/no-user.png")) {
                    val contentResolver: ContentResolver = context.contentResolver
                    val source = ImageDecoder.createSource(contentResolver, uri)
                    imageBitmap = ImageDecoder.decodeBitmap(source)
                    userRepository.setUserProfile(username, imageBitmap)
                }
            }catch (e: Exception){
                Log.d("Error al subir foto", e.toString())
            }
        }

    }

    suspend fun inicarSesion(username: String, password:String): Boolean {
        return userRepository.verifyUser(username, password)
    }
}