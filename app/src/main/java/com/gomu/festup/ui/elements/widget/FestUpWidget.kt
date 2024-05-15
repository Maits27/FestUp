package com.gomu.festup.ui.elements.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.gomu.festup.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


@Serializable
data class EventoWidget(
    val nombre: String,
    val fecha: String,
    val numeroAsistentes: Int
)

val colorFondo = Color(0xFF141318)      // Using background-dark from theme
val textColor = Color(0xFFCEBDFE)       // Using primary-dark from theme
val colorFondoCard = Color(0xFF4C3E76)  // Using primaryContainer-dark from theme

class FestUpWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    // PreferencesGlanceStateDefinition (DataStore) keys. This is only use because we need to update
    // the DataStore if we want the widget to change.
    companion object {
        // As we can only have basic types in DataStore, we need to use a string key for events
        // it will be encoded as a JSON to decode later on.
        val eventosKey = stringPreferencesKey("eventos")
        val userIsLoggedIn = booleanPreferencesKey("loggedInUser")
        val idiomaUser = stringPreferencesKey("idioma")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // TODO
        // UI
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        // Get the PreferencesGlanceStateDefinition (DataStore) unique for each widget
        val prefs = currentState<Preferences>()

        val eventosString: String? = prefs[eventosKey]
        val eventos: List<EventoWidget> =
            if (eventosString != null) Json.decodeFromString(eventosString)
            else emptyList()
        val userIsLoggedIn = prefs[userIsLoggedIn] ?: false
        val idiomaUser = prefs[idiomaUser]
        Log.d("Idioma widget", idiomaUser.toString())
        Column(
            modifier = GlanceModifier.fillMaxSize().background(colorFondo).padding(top = 10.dp)
        ) {
            Row(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                Text(
                    text = if (idiomaUser == "eu") "Datozen ekitaldiak" else "Próximos eventos",
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        color = ColorProvider(textColor),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    modifier = GlanceModifier.padding(end = 10.dp)
                )
                Image(
                    provider = ImageProvider(R.drawable.party_emoji),
                    contentDescription = "Party emoji",
                    modifier = GlanceModifier.size(30.dp)
                )
            }
            if (userIsLoggedIn) {
                if (eventos.isNotEmpty()) {
                    LazyColumn(modifier = GlanceModifier.padding(15.dp)) {
                        eventos.forEach {
                            item {
                                EventoCard(it)
                            }
                            item {
                                Spacer(modifier = GlanceModifier.padding(bottom = 10.dp))
                            }
                        }
                    }
                } else {
                    Text(
                        text = if (idiomaUser == "eu") "Ez daukazu ekitaldirik hurrengo egunetan" else "No tienes eventos en los próximos días",
                        style = TextStyle(
                            color = ColorProvider(textColor),
                            textAlign = TextAlign.Center
                        ),
                        modifier = GlanceModifier.fillMaxSize()
                            .padding(vertical = 10.dp, horizontal = 50.dp)
                    )
                }
            } else {
                Text(
                    text = "Inicia sesión para poder ver los próximos eventos",
                    style = TextStyle(
                        color = ColorProvider(textColor),
                        textAlign = TextAlign.Center
                    ),
                    modifier = GlanceModifier.fillMaxSize()
                        .padding(vertical = 10.dp, horizontal = 50.dp)
                )
            }
        }
    }

    @Composable
    fun EventoCard(evento: EventoWidget) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(colorFondoCard)
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Text(
                text = evento.nombre,
                style = TextStyle(color = ColorProvider(textColor), fontWeight = FontWeight.Bold),
                modifier = GlanceModifier.padding(horizontal = 5.dp)
            )
            Text(
                text = evento.fecha,
                style = TextStyle(color = ColorProvider(textColor)),
                modifier = GlanceModifier.padding(horizontal = 5.dp)
            )
            Row(
                horizontalAlignment = Alignment.End,
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                Text(
                    text = evento.numeroAsistentes.toString(),
                    style = TextStyle(textAlign = TextAlign.End, color = ColorProvider(textColor)),
                    modifier = GlanceModifier.padding(end = 10.dp)
                )
                Image(
                    provider = ImageProvider(R.drawable.round_people_24),
                    contentDescription = "People"
                )
            }
        }
    }
}
