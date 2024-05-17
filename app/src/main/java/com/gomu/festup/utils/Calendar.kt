package com.gomu.festup.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.util.Log
import java.util.TimeZone


/**
 * Función para coger los ID de los calendarios locales (con google calendar)
 */
@SuppressLint("Range")
fun getAllCalendarIds(context: Context): List<Long> {
    val calendarIds = mutableListOf<Long>()
    val projection = arrayOf(CalendarContract.Calendars._ID)

    val selection = "${CalendarContract.Calendars.ACCOUNT_TYPE} = ? AND ${CalendarContract.Calendars.NAME} LIKE ?"
    val selectionArgs = arrayOf("com.google", "%@%")

    context.contentResolver.query(
        CalendarContract.Calendars.CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID))
            calendarIds.add(id)
        }
    }
    return calendarIds
}

/**
 * Función para coger los ID de los calendarios locales (salvo google calendar)
 */
@SuppressLint("Range")
fun getLocalCalendarIds(context: Context): List<Long> {
    val calendarIds = mutableListOf<Long>()
    val projection = arrayOf(CalendarContract.Calendars._ID)

    // Filtro para coger solo los locales
    val selection = "${CalendarContract.Calendars.ACCOUNT_TYPE} IN (?)"
    val selectionArgs = arrayOf("LOCAL")

    context.contentResolver.query(
        CalendarContract.Calendars.CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID))
            calendarIds.add(id)
        }
    }
    return calendarIds
}

/**
 * Función para añadir eventos futuros al calendario
 */
fun addEventOnCalendar(context: Context, title: String, dateP: Long){
    val contentResolver: ContentResolver = context.contentResolver
    val timeZone = TimeZone.getDefault().id

    // Si es posible añadir los eventos al calendario
    var calendarIDs = getLocalCalendarIds(context)

    // Si no hay locales intentar añadirlo en otro calendario
    if (calendarIDs.isEmpty()){
        Log.d("NO LOCALS", "NO LOCALS")
        calendarIDs = getAllCalendarIds(context)
    }

    for (calendarID in calendarIDs) {

        val contentValues = ContentValues().apply {
            put(Events.CALENDAR_ID, calendarID)
            put(Events.TITLE, title)
            put(Events.DTSTART, dateP)
            put(Events.DTEND, dateP +  (24 * 60 * 60 * 1000))
            put(Events.ALL_DAY, 1)
            put(Events.EVENT_TIMEZONE, timeZone)
        }
        contentResolver.insert(Events.CONTENT_URI, contentValues)
    }
}