package com.gomu.festup.utils


import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gomu.festup.MyNotificationChannels
import com.gomu.festup.NotificationID
import com.gomu.festup.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Servicio para manejar las notificaciones recibidas desde FCM.
 *
 * Referencias: https://medium.com/@dugguRK/fcm-android-integration-3ca32ff425a5
 */

class FCMService : FirebaseMessagingService() {

    /**
     * Método para recibir las notificaciones FCM.
     *
     * @param remoteMessage El mensaje de notificación.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let { notification ->

            Log.d("FCM", "Message Notification Title: ${notification.title}")
            Log.d("FCM", "Message Notification Body: ${notification.body}")

            val builder = NotificationCompat.Builder(this,
                MyNotificationChannels.NOTIFICATIONS_CHANNEL.name
            )
                .setSmallIcon(R.drawable.festup)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
            try {
                with(NotificationManagerCompat.from(this)) {
                    notify(NotificationID.NOTIFICATIONS.id, builder.build())
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }
}