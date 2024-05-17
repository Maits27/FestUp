package com.gomu.festup.data.localDatabase

import androidx.room.TypeConverter
import java.util.Date

/**
 * [TypeConverter] para que ROOM haga la conversión del tipo
 * Date de Java a un tipo String que pueda almacenar y viceversa.
 */
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