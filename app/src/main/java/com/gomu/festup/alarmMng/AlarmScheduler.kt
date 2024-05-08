package com.gomu.festup.alarmMng

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import com.gomu.festup.LocalDatabase.Entities.Evento
import java.util.Date
import android.app.PendingIntent
import android.util.Log
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

data class AlarmItem(
    val alarmTime : LocalDateTime,
    val eventTitle : String,
    val eventLocation: String,
    val eventId: String
)

interface AlarmScheduler {
    fun schedule(alarmItem: AlarmItem)

    fun cancel(alarmItem: AlarmItem)
}

class AndroidAlarmScheduler (
    private val context: Context
): AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(alarmItem: AlarmItem) {
        if (alarmItem.alarmTime > LocalDateTime.now()) {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("TITLE", "Mañana estas apuntado a: ${alarmItem.eventTitle}")
                putExtra("BODY", "El evento tendrá lugar en ${alarmItem.eventLocation}, no olvides asistir!")
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmItem.alarmTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
                PendingIntent.getBroadcast(
                    context,
                    alarmItem.eventId.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            Log.d("AlarmScheduler", "Alarma programada para: ${alarmItem.alarmTime}")
        }
    }

    override fun cancel(alarmItem: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarmItem.eventId.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}