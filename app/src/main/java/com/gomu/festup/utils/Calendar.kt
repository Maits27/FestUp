package com.gomu.festup.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.CalendarContract
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.TimeZone
import android.provider.CalendarContract.Events

// Function to get all calendar's IDs (including google calendars)
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

// Function to get the local calendar's IDs (except google calendars)
@SuppressLint("Range")
fun getLocalCalendarIds(context: Context): List<Long> {
    val calendarIds = mutableListOf<Long>()
    val projection = arrayOf(CalendarContract.Calendars._ID)

    // Filter the calendars in order to keep only the local ones
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

// Function to add future activities to calendars
fun addEventOnCalendar(context: Context, title: String, dateP: Long){
    val contentResolver: ContentResolver = context.contentResolver
    val timeZone = TimeZone.getDefault().id

    // If is possible add events on local calendar
    var calendarIDs = getLocalCalendarIds(context)

    // If not local calendars available try to add events in other calendars
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