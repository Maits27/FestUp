package com.gomu.festup.utils


import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gomu.festup.MainActivity
import com.gomu.festup.MyNotificationChannels
import com.gomu.festup.NotificationID
import com.gomu.festup.R
import com.gomu.festup.data.repositories.preferences.IGeneralPreferences
import com.gomu.festup.data.repositories.preferences.ILoginSettings
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


/**
 * Servicio para manejar las notificaciones recibidas desde FCM.
 *
 * Referencias: https://medium.com/@dugguRK/fcm-android-integration-3ca32ff425a5
 */

class FCMService : FirebaseMessagingService() {

    @Inject
    lateinit var preferences: IGeneralPreferences
    @Inject
    lateinit var loginSettings: ILoginSettings

    /**
     * Método para recibir las notificaciones FCM.
     *
     * @param remoteMessage El mensaje de notificación.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val context = this
        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = loginSettings.getLastLoggedUser()
            if(currentUser!="") {
                if (preferences.getReceiveNotifications(currentUser).first()) {
                    withContext(Dispatchers.Default) {
                        remoteMessage.notification?.let { notification ->

                            Log.d("FCM", "Message Notification Title: ${notification.title}")
                            Log.d("FCM", "Message Notification Body: ${notification.body}")

                            val builder = NotificationCompat.Builder(
                                context,
                                MyNotificationChannels.NOTIFICATIONS_CHANNEL.name
                            )
                                .setSmallIcon(R.drawable.festup)
                                .setContentTitle(notification.title)
                                .setContentText(notification.body)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                            try {
                                with(NotificationManagerCompat.from(context)) {
                                    notify(NotificationID.NOTIFICATIONS.id, builder.build())
                                }
                            } catch (e: SecurityException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }

    }
}