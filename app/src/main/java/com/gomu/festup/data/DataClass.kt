package com.gomu.festup.data

import androidx.compose.ui.graphics.painter.Painter
import com.gomu.festup.ui.AppScreens

data class Diseño(val pantalla: AppScreens, val icono: Painter, val nombre: String="")
enum class AppLanguage(val language: String, val code: String) {
    EU("Euskera", "eu"),
    ES("Español", "es");


    companion object {
        /**
         * Obtener [AppLanguage] De un código.
         * @code puede ser: 'eu', 'es'
         */
        fun getFromCode(code: String) = when (code) {
            EU.code -> EU
            ES.code -> ES
            else -> ES
        }
    }
}
