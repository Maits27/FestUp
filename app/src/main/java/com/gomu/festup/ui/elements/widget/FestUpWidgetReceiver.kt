package com.gomu.festup.ui.elements.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import com.gomu.festup.data.localDatabase.Entities.Evento
import com.gomu.festup.data.repositories.CuadrillaRepository
import com.gomu.festup.data.repositories.EventoRepository
import com.gomu.festup.data.repositories.preferences.IGeneralPreferences
import com.gomu.festup.data.repositories.preferences.ILoginSettings
import com.gomu.festup.utils.getWidgetEventos
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * This is the BroadCast Receiver for the FestUpWidget widget.
 *
 * We need to perform some actions here as we cannot inject dependencies with hilt into the widget
 * directly.
 *
 * Hilt currently supports the following Android classes:
 * * Application (by using @HiltAndroidApp)
 * * ViewModel (by using @HiltViewModel)
 * * Activity
 * * Fragment
 * * View
 * * Service
 * * BroadcastReceiver
 */
@AndroidEntryPoint
class FestUpWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FestUpWidget()

    @Inject
    lateinit var eventoRepository: EventoRepository
    @Inject
    lateinit var preferencesRepository: ILoginSettings
    @Inject
    lateinit var cuadrillaRepository: CuadrillaRepository
    @Inject
    lateinit var preferences: IGeneralPreferences


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        Log.d("FestUpWidget", "onUpdate")
        updateWidgetData(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        updateWidgetData(context)
    }

    private fun updateWidgetData(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUsername = preferencesRepository.getLastLoggedUser()
            val currentLanguage = preferences.language(currentUsername).first()
            // Obtener la lista de eventos del último usuario identificado
            val eventos = if (currentUsername != "") eventoRepository.eventosUsuarioList(currentUsername)
            else emptyList()
            val eventosWidget = getWidgetEventos(eventos, ::numeroAsistentesEvento)
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
                    prefs[FestUpWidget.idiomaUser] = currentLanguage
                }
                // Actualizamos el widget
                FestUpWidget().update(context, glanceId)
            }
        }
    }

    private fun numeroAsistentesEvento(evento: Evento) : Int = runBlocking {
        val usuarios = eventoRepository.usuariosEvento(evento.id).first()
        val cuadrillas = cuadrillaRepository.integrantesCuadrillasEvento(evento.id).first()
        usuarios.size + cuadrillas.size
    }
}

