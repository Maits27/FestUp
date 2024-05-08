package com.gomu.festup.ui.widget

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.annotation.DimenRes
import androidx.annotation.RestrictTo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
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
import androidx.glance.unit.Dimension
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.gomu.festup.R


@Serializable
data class EventoWidget(
    val nombre: String,
    val fecha: String,
    val numeroAsistentes: Int
)

val colorFondo = Color(0xFF534340)
val textColor = Color(0xFFFFB4A6)
val colorFondoCard = Color(0xFF442A25)

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
        val eventos: List<EventoWidget> = if (eventosString != null) Json.decodeFromString(eventosString)
                                          else emptyList()
        val userIsLoggedIn = prefs[userIsLoggedIn] ?: false


        Column(
            modifier = GlanceModifier.fillMaxSize().background(colorFondo).padding(top = 10.dp)
        ) {
            Row (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                Text(
                    text = "Próximos eventos",
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
                        item {
                            EventoCard(evento = EventoWidget(nombre = "San Roque", fecha = "14-08-2024", numeroAsistentes = 1000)) // TODO de prueba
                        }
                        item {
                            Spacer(modifier = GlanceModifier.padding(bottom = 10.dp))
                        }
                        item {
                            EventoCard(evento = EventoWidget(nombre = "San Jorge", fecha = "23-04-2024", numeroAsistentes = 2000)) // TODO de prueba
                            Spacer(modifier = GlanceModifier.padding(bottom = 10.dp))
                        }
                        eventos.forEach {
                            item {
                                EventoCard(it)
                                Spacer(modifier = GlanceModifier.padding(bottom = 10.dp))
                            }
                        }
                    }
                }
                else {
                    Text(
                        text = "No tienes eventos en los próximos días",
                        style = TextStyle(
                            color = ColorProvider(textColor),
                            textAlign = TextAlign.Center
                        ),
                        modifier = GlanceModifier.fillMaxSize().padding(vertical = 10.dp, horizontal = 50.dp)
                    )
                }
            }
            else {
                Text(
                    text = "Inicia sesión para poder ver los próximos eventos",
                    style = TextStyle(
                        color = ColorProvider(textColor),
                        textAlign = TextAlign.Center
                    ),
                    modifier = GlanceModifier.fillMaxSize().padding(vertical = 10.dp, horizontal = 50.dp)
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
                .border(width = 5.dp, color = ColorProvider(colorFondoCard))
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
            Row (
                horizontalAlignment = Alignment.End,
                modifier = GlanceModifier.fillMaxWidth()
            ){
                Text(
                    text = evento.numeroAsistentes.toString(),
                    style = TextStyle(textAlign = TextAlign.End, color = ColorProvider(textColor)),
                    modifier = GlanceModifier.padding(end = 10.dp)
                )
                Image(provider = ImageProvider(R.drawable.round_people_24), contentDescription = "People")
            }
        }
    }
    /*
Código sacado de futuras versiones de Glance:
https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:glance/glance-wear-tiles/src/main/java/androidx/glance/wear/tiles/Border.kt?q=file:androidx%2Fglance%2Fwear%2Ftiles%2FBorder.kt%20function:border
*/

    /**
     * Apply a border around an element, border width is provided in Dp
     *
     * @param width The width of the border, in DP
     * @param color The color of the border
     */
    public fun GlanceModifier.border(
        width: Dp,
        color: ColorProvider
    ): GlanceModifier = this.then(
        BorderModifier(BorderDimension(dp = width), color)
    )

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public data class BorderModifier(
        public val width: BorderDimension,
        public val color: ColorProvider
    ) : GlanceModifier.Element

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public data class BorderDimension(
        public val dp: Dp = 0.dp,
        @DimenRes public val resourceId: Int = 0
    ) {
        fun toDp(resources: Resources): Dp =
            if (resourceId == 0) dp
            else (resources.getDimension(resourceId) / resources.displayMetrics.density).dp
    }
}

