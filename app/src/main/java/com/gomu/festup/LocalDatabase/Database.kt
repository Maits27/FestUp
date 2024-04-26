package com.gomu.festup.LocalDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.LocalDatabase.Entities.CuadrillasAsistentes
import com.gomu.festup.LocalDatabase.Entities.Evento
import com.gomu.festup.LocalDatabase.Entities.Integrante
import com.gomu.festup.LocalDatabase.Entities.Seguidores
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.LocalDatabase.Entities.UsuariosAsistentes

@Database(entities = [Usuario::class, Cuadrilla::class, Integrante::class,
    Seguidores::class, Evento::class, UsuariosAsistentes::class,
    CuadrillasAsistentes::class], version=1)
@TypeConverters(Converter::class)
abstract class Database: RoomDatabase() {
//    abstract fun cuadrillaDao(): Cua
}


