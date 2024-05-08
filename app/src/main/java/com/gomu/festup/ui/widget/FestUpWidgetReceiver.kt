package com.gomu.festup.ui.widget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import com.gomu.festup.LocalDatabase.Repositories.EventoRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private val currentUsername = "aingerubellido" // TODO conseguir el real del DataStore

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)


        CoroutineScope(Dispatchers.IO).launch {
            // Get the list of events
            val eventos = eventoRepository.eventosUsuarioForWidget(currentUsername)
            // Get the widget manager
            val manager = GlanceAppWidgetManager(context)
            // We get all the glace IDs that are a FestUpWidget (remember than we can have more
            // than one widget of the same type)
            val glanceIds = manager.getGlanceIds(FestUpWidget::class.java)
            // For each glanceId
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[FestUpWidget.eventosKey] = Json.encodeToString(eventos)
                }
                // We update the widget
                FestUpWidget().update(context, glanceId)
            }
        }
    }
}