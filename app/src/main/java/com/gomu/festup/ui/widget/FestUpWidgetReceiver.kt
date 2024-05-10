package com.gomu.festup.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Repositories.CuadrillaRepository
import com.gomu.festup.LocalDatabase.Repositories.EventoRepository
import com.gomu.festup.LocalDatabase.Repositories.ILoginSettings
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

        Log.d("FestUpWidget", "onReceive intent ${intent.action}")
        updateWidgetData(context)
    }

    fun updateWidgetData(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUsername = preferencesRepository.getLastLoggedUser()
            // Get the list of events of the last logged user
            val eventos = if (currentUsername != "") eventoRepository.eventosUsuarioList(currentUsername)
            else emptyList()
            val eventosWidget = getWidgetEventos(eventos, ::numeroAsistentesEvento)
            Log.d("FestUpWidget", "$currentUsername eventos: $eventosWidget")
            // Get the widget manager
            val manager = GlanceAppWidgetManager(context)
            // We get all the glace IDs that are a FestUpWidget (remember than we can have more
            // than one widget of the same type)
            val glanceIds = manager.getGlanceIds(FestUpWidget::class.java)
            // For each glanceId
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[FestUpWidget.eventosKey] = Json.encodeToString(eventosWidget)
                    prefs[FestUpWidget.userIsLoggedIn] = (currentUsername != "")
                }
                // We update the widget
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

