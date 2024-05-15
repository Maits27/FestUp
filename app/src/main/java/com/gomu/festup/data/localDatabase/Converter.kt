package com.gomu.festup.data.localDatabase

import androidx.room.TypeConverter
import java.util.Date

class Converter {

    @TypeConverter
    fun fromDate(value: Date?): Long?{
        return  value?.time
    }
    @TypeConverter
    fun toDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }
}