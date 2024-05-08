package com.gomu.festup.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomu.festup.Preferences.IGeneralPreferences
import com.gomu.festup.data.AppLanguage
import com.gomu.festup.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val preferencesRepository: IGeneralPreferences,
    private val languageManager: LanguageManager
) : ViewModel() {

    /*************************************************
     **                    Estados                  **
     *************************************************/
    private val _currentUser = MutableStateFlow("")

    val currentUser: Flow<String> = _currentUser

    val currentSetLang by languageManager::currentLang

    val idioma: (String)-> Flow<AppLanguage> = { preferencesRepository.language(it).map { AppLanguage.getFromCode(it) } }

    val darkTheme: (String)-> Flow<Boolean> = { preferencesRepository.getThemePreference(it)}

    val receiveNotifications: (String)-> Flow<Boolean> = { preferencesRepository.getReceiveNotifications(it)}


    /*************************************************
     **                    Eventos                  **
     *************************************************/

    fun changeUser(email: String){
        _currentUser.value = email
    }


    ////////////////////// Idioma //////////////////////

    // Cambio del idioma de preferencia
    fun changeLang(i: AppLanguage) {
        languageManager.changeLang(i)
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.setLanguage(currentUser.first(), i.code)
        }
    }


    ////////////////////// Tema //////////////////////

    // Cambio del tema de preferencia
    fun changeTheme(dark: Boolean){
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.saveThemePreference(currentUser.first(), dark) }
    }

    fun restartLang(i: AppLanguage){
        viewModelScope.launch {
            languageManager.changeLang(i)
        }
    }

    ////////////////////// Notificaciones //////////////////////

    fun changeReceiveNotifications(){
        viewModelScope.launch(Dispatchers.IO) { preferencesRepository.changeReceiveNotifications(currentUser.first()) }
    }


}
