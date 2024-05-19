package com.gomu.festup.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomu.festup.data.AppLanguage
import com.gomu.festup.data.repositories.preferences.IGeneralPreferences
import com.gomu.festup.data.repositories.preferences.ILoginSettings
import com.gomu.festup.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * View Model de Hilt para preferencias del usuario local
 */
@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val preferencesRepository: IGeneralPreferences,
    private val loginRepository: ILoginSettings,
    private val languageManager: LanguageManager
) : ViewModel() {

    /*************************************************
     **                    Estados                  **
     *************************************************/
    private val _currentUser = MutableStateFlow("")
    val currentUser: Flow<String> = _currentUser

    val lastLoggedUser: String = runBlocking { return@runBlocking loginRepository.getLastLoggedUser() }
    val lastBearerToken: String = runBlocking { return@runBlocking loginRepository.getLastBearerToken() }
    val lastRefreshToken: String = runBlocking { return@runBlocking loginRepository.getLastRefreshToken() }

    val currentSetLang by languageManager::currentLang
    val idioma: (String)-> Flow<AppLanguage> = { preferencesRepository.language(it).map { AppLanguage.getFromCode(it) } }
    val darkTheme: (String)-> Flow<Boolean> = { preferencesRepository.getThemePreference(it)}
    val receiveNotifications: (String)-> Flow<Boolean> = { preferencesRepository.getReceiveNotifications(it)}



    /*************************************************
     **                    Eventos                  **
     *************************************************/

    /***************************  Usuario  ***************************/
    suspend fun changeUser(username: String){
        _currentUser.value = username
        loginRepository.setLastLoggedUser(username)
    }

    /***************************  Idioma  ***************************/
    fun changeLang(i: AppLanguage) {
        languageManager.changeLang(i)
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.setLanguage(currentUser.first(), i.code)
        }
    }

    /***************************  Tema  ***************************/
    fun changeTheme(dark: Boolean){
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.saveThemePreference(currentUser.first(), dark) }
    }

    fun restartLang(i: AppLanguage){
        viewModelScope.launch {
            languageManager.changeLang(i)
        }
    }

    /***************************  Notificaciones  ***************************/
    fun changeReceiveNotifications(){
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.changeReceiveNotifications(currentUser.first()) }
    }

//    ////////////////////// Edad //////////////////////
//
//    fun changeVisualizarEdad() {
//        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.changeVisualizarEdad(currentUser.first()) }
//    }

}
