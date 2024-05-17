package com.gomu.festup.alarmMng

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gomu.festup.MainActivity
import com.gomu.festup.R
import com.gomu.festup.data.repositories.preferences.IGeneralPreferences
import com.gomu.festup.data.repositories.preferences.ILoginSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * [AlarmReceiver] que recoge las alarmas programadas por el [AlarmScheduler]
 *  * y ejecuta el código necesario en base a lo programado.
 */
@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver() {
    @Inject
    lateinit var preferences: IGeneralPreferences
    @Inject
    lateinit var loginSettings: ILoginSettings
    override fun onReceive(context: Context, intent: Intent?) {

        val title = intent?.getStringExtra("TITLE")?: return
        val body = intent.getStringExtra("BODY") ?: return
        Log.d("AlarmScheduler", "Alarma recibida")

        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = loginSettings.getLastLoggedUser()
            if (currentUser != "") {
                if (preferences.getReceiveNotifications(currentUser).first()) {
                    val notificationId = System.currentTimeMillis().toInt()

                    val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.festuplogo)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                    with(NotificationManagerCompat.from(context)) {
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            notify(notificationId, builder.build())
                        }
                    }
                }
            }
        }
    }
}