package com.gomu.festup.Preferences

import kotlinx.coroutines.flow.Flow

interface IGeneralPreferences {
    fun language(username: String): Flow<String>
    suspend fun setLanguage(username: String, code: String)

    fun getThemePreference(username: String): Flow<Boolean>
    suspend fun saveThemePreference(username: String, dark: Boolean)

    fun getReceiveNotifications(username: String): Flow<Boolean>
    suspend fun changeReceiveNotifications(username: String)

    fun getVisualizarEdad(username: String): Flow<Boolean>
    suspend fun changeVisualizarEdad(username: String)
}