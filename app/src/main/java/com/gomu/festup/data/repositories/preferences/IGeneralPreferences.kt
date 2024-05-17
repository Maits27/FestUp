package com.gomu.festup.data.repositories.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de acceso a las preferencias del usuario en el Datastore
 */
interface IGeneralPreferences {
    fun language(username: String): Flow<String>
    suspend fun setLanguage(username: String, code: String)

    fun getThemePreference(username: String): Flow<Boolean>
    suspend fun saveThemePreference(username: String, dark: Boolean)

    fun getReceiveNotifications(username: String): Flow<Boolean>
    suspend fun changeReceiveNotifications(username: String)
}