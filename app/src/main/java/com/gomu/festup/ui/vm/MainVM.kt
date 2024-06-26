package com.gomu.festup.ui.vm

import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gomu.festup.data.Contacto
import com.gomu.festup.data.UserCuadrillaAndEvent
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.Evento
import com.gomu.festup.data.localDatabase.Entities.Integrante
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.data.repositories.ICuadrillaRepository
import com.gomu.festup.data.repositories.IEventoRepository
import com.gomu.festup.data.repositories.IUserRepository
import com.gomu.festup.data.repositories.preferences.ILoginSettings
import com.gomu.festup.ui.elements.widget.FestUpWidget
import com.gomu.festup.utils.getWidgetEventos
import com.gomu.festup.utils.localUriToBitmap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date
import javax.inject.Inject

/**
 * View Model de Hilt para la gestión de todos los métodos relativos a la gestión de la aplicación.
 * Utiliza los datos en ROOM (previamente descargados de remoto) para la visualización rápida y
 * fluida de la app (a través de Flows), además de actualizar en tiempo real la base de datos remota.
 */

@HiltViewModel
class MainVM @Inject constructor(
    private val userRepository: IUserRepository,
    private val cuadrillaRepository: ICuadrillaRepository,
    private val eventoRepository: IEventoRepository,
    private val preferencesRepository: ILoginSettings
): ViewModel() {
    var retrocesoForzado: MutableState<Boolean> = mutableStateOf(false)

    var serverOk: MutableState<Boolean> = mutableStateOf(false)

    var currentUser: MutableState<Usuario?> = mutableStateOf(null)

    var usuarioMostrar: MutableList<Usuario?> = mutableListOf()

    var cuadrillaMostrar: MutableState<Cuadrilla?> = mutableStateOf(null)

    var eventoMostrar: MutableState<Evento?> = mutableStateOf(null)

    var localizacion: MutableState<Location?> = mutableStateOf(null)

    var localizacionAMostrar: MutableState<LatLng?> = mutableStateOf(null)

    val alreadySiguiendo: MutableState<Boolean?> = mutableStateOf(null)

    var selectedTabFeed: MutableState<Int> = mutableIntStateOf(0)

    var selectedTabSearch: MutableState<Int> = mutableIntStateOf(0)

    /*****************************************************
     ***************** DESCARGA DE DATOS *****************
     *****************************************************/

    suspend fun descargarUsuarios(){
        try{
            userRepository.descargarUsuarios()
            serverOk.value = true
        }catch (e: Exception) {
            Log.d("SERVER PETICION", e.toString())
        }
    }


    suspend fun descargarDatos(){
        try{
            cuadrillaRepository.descargarCuadrillas()
            cuadrillaRepository.descargarIntegrantes()
            eventoRepository.descargarEventos()
            eventoRepository.descargarUsuariosAsistentes()
            eventoRepository.descargarCuadrillasAsistentes()
            userRepository.descargarSeguidores()
            Log.d("DESCARGAR DATOS", "FIN")

        }catch (e: Exception) {
            Log.d("DESCARGAR DATOS",e.toString())
        }
    }

    suspend fun actualizarDatos(){
        try {
            userRepository.descargarUsuarios()
            cuadrillaRepository.descargarCuadrillas()
            cuadrillaRepository.descargarIntegrantes()
            eventoRepository.descargarEventos()
            eventoRepository.descargarUsuariosAsistentes()
            eventoRepository.descargarCuadrillasAsistentes()
            userRepository.descargarSeguidores()
            Log.d("ACTUALIZAR DATOS", "FIN")
        }catch (e: Exception) {
            Log.d("ACTUALIZAR DATOS",e.toString())
        }
    }

    /*****************************************************
     ************** METODOS DE SUBSCRIPCIÓN **************
     *****************************************************/

    fun suscribirASeguidos(usuariosLista: List<Usuario>){
        usuariosLista.map { usuarioLista ->
            Log.d("SUSCRIBE TO",usuarioLista.username)
            subscribeToUser(usuarioLista.username)
        }
    }
    fun unSuscribeASeguidos(usuariosLista: List<Usuario>): Boolean = runBlocking{
        usuariosLista.map { usuarioLista ->
            Log.d("UNSUSCRIBE FROM",usuarioLista.username)
            unsubscribeFromUser(usuarioLista.username)
        }
        true
    }
    fun subscribeUser() {
        val fcm = FirebaseMessaging.getInstance()
        // Eliminar el token FCM actual
        fcm.deleteToken().addOnSuccessListener {
            // Obtener el nuevo token
            fcm.token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                viewModelScope.launch(Dispatchers.IO) {
                    try{
                        userRepository.subscribeUser(task.result, currentUser.value!!.username)
                        Log.d("FCM", "Usuario suscrito ${currentUser.value!!.username}")
                    }
                    catch (e:Exception){
                        Log.d("FCM Exception", e.printStackTrace().toString())
                    }

                }
            })
        }
    }
    fun unSubscribeUser(): Boolean {
        val fcm = FirebaseMessaging.getInstance()
        // Eliminar el token FCM actual
        fcm.deleteToken().addOnSuccessListener {
            // Obtener el nuevo token
            fcm.token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                viewModelScope.launch(Dispatchers.IO) {
                    try{
                        userRepository.unSubscribeUser(task.result, currentUser.value!!.username)
                        Log.d("FCM", "Usuario desuscrito ${currentUser.value!!.username}")
                    }
                    catch (e:Exception){
                        Log.d("FCM Exception", e.printStackTrace().toString())
                    }

                }
            })
        }
        return true
    }

    fun subscribeToUser(username: String) {
        val fcm = FirebaseMessaging.getInstance()
        // Eliminar el token FCM actual
        fcm.deleteToken().addOnSuccessListener {
            // Obtener el nuevo token
            fcm.token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                viewModelScope.launch(Dispatchers.IO) {
                    try{
                        userRepository.subscribeToUser(task.result, username)
                        Log.d("FCM", "Suscribirse a $username")
                    }
                    catch (e:Exception){
                        Log.d("FCM Exception", e.printStackTrace().toString())
                    }

                }
            })
        }
    }

    fun unsubscribeFromUser(username: String) {
        val fcm = FirebaseMessaging.getInstance()
        // Eliminar el token FCM actual
        fcm.deleteToken().addOnSuccessListener {
            // Obtener el nuevo token
            fcm.token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                viewModelScope.launch(Dispatchers.IO) {
                    try{
                        userRepository.unsubscribeFromUser(task.result, username)
                        Log.d("FCM", "Desuscribirse de $username")
                    }
                    catch (e:Exception){
                        Log.d("FCM Exception", e.printStackTrace().toString())
                    }

                }
            })
        }
    }

    /*****************************************************
     ****************** METODOS USUARIO ******************
     *****************************************************/
    @RequiresApi(Build.VERSION_CODES.P)
    fun updateUserImage(context: Context, username: String, uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uri != null) {
                val imageBitmap = context.localUriToBitmap(uri)
                userRepository.setUserProfile(username, imageBitmap, context)
            }
        }
    }

    fun editUsuario(username: String, email: String, nombre: String, fecha: Date, telefono: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val usuario = withContext(Dispatchers.IO) {
                userRepository.editUsuario(username, email, nombre, fecha, telefono)
            }
            currentUser.value = usuario
        }
    }

    fun calcularEdad(usuario: Usuario): Int{
        val diff = Date().time - (usuario.fechaNacimiento.time)
        val edad = diff / (1000L * 60 * 60 * 24 * 365)
        return edad.toInt()
    }

    fun newSiguiendo(followedUsername: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.newSeguidor(currentUser.value!!.username, followedUsername)
            alreadySiguiendo.value = true
        }
    }

    fun alreadySiguiendo(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            alreadySiguiendo.value = userRepository.alreadySiguiendo(currentUser.value!!.username, username)
        }
    }

    fun unfollow(usernameToUnfollow: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteSeguidores(currentUser.value!!.username, usernameToUnfollow)
            alreadySiguiendo.value = false
        }
    }

    fun getCuadrillasUsuario(usuario: Usuario): Flow<List<Cuadrilla>> {
        return userRepository.getCuadrillasUsuario(usuario.username)
    }

    fun getUsuariosMenosCurrent(usuario: Usuario): Flow<List<Usuario>>{
        return userRepository.getUsuariosMenosCurrent(usuario)
    }
    fun estaApuntado(usuario: Usuario, id: String): Boolean = runBlocking {
        eventoRepository.estaApuntado(usuario.username, id)
    }
    fun cuadrillasUsuarioApuntadas(usuario: Usuario, id: String): Flow<List<Cuadrilla>>  {
        return eventoRepository.cuadrillasUsuarioApuntadas(usuario.username, id)
    }
    fun cuadrillasUsuarioNoApuntadas(usuario: Usuario, id: String): Flow<List<Cuadrilla>>  {
        return eventoRepository.cuadrillasUsuarioNoApuntadas(usuario.username, id)
    }

    fun listaSeguidores(usuario: Usuario): Flow<List<Usuario>>{
        return userRepository.getSeguidores(usuario.username)
    }
    fun listaSeguidos(usuario: Usuario): Flow<List<Usuario>>{
        return userRepository.getAQuienSigue(usuario.username)
    }

    fun actualizarCurrentUser(username: String): Usuario? = runBlocking(Dispatchers.IO){
        userRepository.getUsuario(username)
    }


    /*****************************************************
     ****************** METODOS CUADRILLA ******************
     *****************************************************/
    suspend fun crearCuadrilla(cuadrilla: Cuadrilla, image: Bitmap?, context: Context): Boolean  {
        return cuadrillaRepository.insertCuadrilla(currentUser.value!!.username,cuadrilla, image, context)
    }
    @RequiresApi(Build.VERSION_CODES.P)
    fun updateCuadrillaImage(context: Context, nombre: String, uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uri != null) {
                val imageBitmap = context.localUriToBitmap(uri)
                cuadrillaRepository.setCuadrillaProfile(nombre, imageBitmap, context)
            }
        }
    }
    fun usuariosCuadrilla(cuadrilla: Cuadrilla = cuadrillaMostrar.value!!): Flow<List<Usuario>> {
        return cuadrillaRepository.usuariosCuadrilla(cuadrilla.nombre)
    }

    fun eliminarIntegrante(cuadrilla: Cuadrilla)  {
        viewModelScope.launch(Dispatchers.IO) {
            cuadrillaRepository.eliminarIntegrante(cuadrilla,currentUser.value!!.username)
        }
    }

    fun getCuadrillas(): Flow<List<Cuadrilla>> {
        return cuadrillaRepository.getCuadrillas()
    }


    fun getCuadrillaAccessToken(nombre: String): String{
        return cuadrillaRepository.getCuadrillaAccessToken(nombre)
    }

    fun agregarIntegrante(nombreUsuario: String, nombreCuadrilla: String){
        viewModelScope.launch(Dispatchers.IO) {
            cuadrillaRepository.insertUser(nombreUsuario, nombreCuadrilla)
        }
    }


    /*****************************************************
     ****************** METODOS EVENTO ******************
     *****************************************************/

    suspend fun insertarEvento(evento: Evento, image: Bitmap?): Boolean {
        return eventoRepository.insertarEvento(evento, currentUser.value!!.username, image)
    }

    fun getEventos(): Flow<List<Evento>> {
        return eventoRepository.todosLosEventos()
    }
    fun apuntarse(usuario: Usuario, evento: Evento){
        viewModelScope.launch(Dispatchers.IO) {
            eventoRepository.apuntarse(usuario, evento.id)
        }
    }
    fun desapuntarse(usuario: Usuario, evento: Evento){
        viewModelScope.launch(Dispatchers.IO) {
            eventoRepository.desapuntarse(usuario, evento.id)
        }
    }
    fun apuntarse(cuadrilla: Cuadrilla, evento: Evento){
        viewModelScope.launch(Dispatchers.IO) {
            eventoRepository.apuntarse(cuadrilla, evento.id)
        }
    }
    fun desapuntarse(cuadrilla: Cuadrilla, evento: Evento){
        viewModelScope.launch(Dispatchers.IO) {
            eventoRepository.desapuntarse(cuadrilla, evento.id)
        }
    }

    fun getUsuariosEvento(evento: Evento): Flow<List<Usuario>>{
        return eventoRepository.usuariosEvento(evento.id)
    }
    fun getCuadrillasEvento(evento: Evento): Flow<List<Cuadrilla>>{
        return eventoRepository.cuadrillasEvento(evento.id)
    }

    fun eventosUsuario(usuario: Usuario): Flow<List<Evento>> {
        return eventoRepository.eventosUsuario(usuario.username)
    }
    fun eventosUsuarioConUser(usuario: Usuario): Flow<List<UserCuadrillaAndEvent>> {
        val eventosYo = eventoRepository.eventosUsuario(usuario.username).map {
            it.map {
                UserCuadrillaAndEvent(usuario.username, "", it)
            }
        }
        return eventosYo
    }

    fun eventosSeguidos(usuario: Usuario): Flow<List<UserCuadrillaAndEvent>> = runBlocking {
        val eventos = eventoRepository.eventosSeguidos(usuario.username)
        eventos
    }

    fun eventosCuadrilla(cuadrilla: Cuadrilla): Flow<List<Evento>> {
        return eventoRepository.eventosCuadrilla(cuadrilla.nombre)
    }


    fun getIntegrante(cuadrilla: Cuadrilla, usuario: Usuario): Flow<List<Integrante>> {
        return cuadrillaRepository.pertenezcoCuadrilla(cuadrilla,usuario)
    }

    fun calcularEdadMediaEvento(evento: Evento): Int = runBlocking {
        val edades = mutableListOf<Int>()
        val usuarios = getUsuariosEvento(evento).first()
        for (usuario in usuarios){
            val edad = calcularEdad(usuario)
            edades.add(edad)
        }

        val cuadrillas = getCuadrillasEvento(evento).first()
        for (cuadrilla in cuadrillas){
            val usuarios2 = usuariosCuadrilla(cuadrilla).first()
            for (usuario in usuarios2){
                val edad = calcularEdad(usuario)
                edades.add(edad)
            }
        }

        edades.sum()/if(edades.size>0){edades.size} else {1}
    }
    fun numeroDeAsistentes(evento: Evento): Int = runBlocking {
        val usuarios = getUsuariosEvento(evento).first()
        val cuadrillas = getIntegrantesCuadrillasEvento(evento).first()
        usuarios.size + cuadrillas.size
    }

    private fun getIntegrantesCuadrillasEvento(evento: Evento): Flow<List<Integrante>>{
        return cuadrillaRepository.integrantesCuadrillasEvento(evento.id)
    }


    /*****************************************************
     ******************* OTROS METODOS *******************
     *****************************************************/
    fun listaContactos(context: Context): MutableList<Contacto> {

        val contentResolver = context.contentResolver
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null)

        val contactsUsingApp = mutableListOf<Contacto>()
        if (cursor != null) {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val contactNumber = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.NUMBER))
                    contactsUsingApp.add(Contacto(contactName, contactNumber))
                }
            }
        }
        cursor?.close()
        return contactsUsingApp
    }

    fun actualizarWidget(context: Context) {
        Log.d("FestUpWidget", "Actualizando widget")
        viewModelScope.launch(Dispatchers.IO) {
            val currentUsername = preferencesRepository.getLastLoggedUser()
            // Obtener la lista de eventos del último usuario identificado
            val eventos = if (currentUsername != "") eventoRepository.eventosUsuarioList(currentUsername)
            else emptyList()
            val eventosWidget = getWidgetEventos(eventos, ::numeroDeAsistentes)
            Log.d("FestUpWidget", "$currentUsername eventos: $eventosWidget")
            // Obtener el gestor de widgets
            val manager = GlanceAppWidgetManager(context)
            // Obtenemos todos los glace IDs que son un FestUpWidget (recuerda que
            // podemos tener más de un widget del mismo tipo)
            val glanceIds = manager.getGlanceIds(FestUpWidget::class.java)
            // Para cada glanceId
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[FestUpWidget.eventosKey] = Json.encodeToString(eventosWidget)
                    prefs[FestUpWidget.userIsLoggedIn] = (currentUsername != "")
                }
                // Actualizamos el widget
                FestUpWidget().update(context, glanceId)
            }
        }
    }
}
