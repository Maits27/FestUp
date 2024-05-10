package com.gomu.festup.Preferences

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gomu.festup.LocalDatabase.Repositories.ILoginSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "PREFERENCES_SETTINGS")

@Singleton
class PreferencesRepository @Inject constructor(
    private val context: Context,
) : IGeneralPreferences, ILoginSettings {
    val LAST_LOGGED_USER = stringPreferencesKey("last_logged_user")
    val LAST_BEARER_TOKEN = stringPreferencesKey("last_bearer_token")
    val LAST_REFRESH_TOKEN = stringPreferencesKey("last_refresh_token")
    fun PREFERENCE_LANGUAGE(username: String) = stringPreferencesKey("${username}_preference_lang")
    fun PREFERENCE_THEME_DARK(username: String) = booleanPreferencesKey("${username}_preference_theme")
    fun PREFERENCE_NOTIFICATIONS(username: String) = booleanPreferencesKey("${username}_preference_save")


    override suspend fun getLastLoggedUser(): String = context.dataStore.data.first()[LAST_LOGGED_USER]?:""

    // Set the last logged user on DataStore Preferences
    override suspend fun setLastLoggedUser(user: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_LOGGED_USER] = user
        }
    }

    override suspend fun getLastBearerToken(): String = context.dataStore.data.first()[LAST_BEARER_TOKEN]?:""

    override suspend fun setLastBearerToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_BEARER_TOKEN] = token
        }
    }

    override suspend fun getLastRefreshToken(): String = context.dataStore.data.first()[LAST_REFRESH_TOKEN]?:""

    override suspend fun setLastRefreshToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_REFRESH_TOKEN] = token
        }
    }

    //////////////// Preferencias de idioma ////////////////

    /**
     * Recoge el primer valor del Flow del Datastore en los idiomas y lo devuelve.
     * Por defecto se escoge el idioma local del dispositivo Android.
     */
    override fun language(username: String): Flow<String> =
        context.dataStore.data.map { preferences ->
        preferences[PREFERENCE_LANGUAGE(username)] ?: "es"

    }

    override suspend fun setLanguage(username: String, code: String) {
        context.dataStore.edit { settings ->
            settings[PREFERENCE_LANGUAGE(username)] = code
        }
    }

    //////////////// Preferencias del tema ////////////////

    /**
     * Recoge el primer valor del Flow del Datastore en los temas y lo devuelve
     * Valor numérico del 0 al 2:
     *      0 -> Verde (por defecto)
     *      1 -> Azul
     *      2 -> Morado
     */

    override fun getThemePreference(username: String): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[PREFERENCE_THEME_DARK(username)] ?: true
        }

    override suspend fun saveThemePreference(username: String, dark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PREFERENCE_THEME_DARK(username)] = dark
        }
    }

    //////////////// Preferencias de Calendario ////////////////

    /**
     * En base a si es true o false, se guardarán los eventos del usuario
     * en el calendario local del dispositivo
     */
    override fun getReceiveNotifications(username: String): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[PREFERENCE_NOTIFICATIONS(username)] ?: false
        }

    override suspend fun changeReceiveNotifications(username: String) {
        context.dataStore.edit { preferences ->
            preferences[PREFERENCE_NOTIFICATIONS(username)] = !getReceiveNotifications(username).first()
        }
    }



}
