package com.gomu.festup.data.localDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gomu.festup.data.localDatabase.DAO.CuadrillaDao
import com.gomu.festup.data.localDatabase.DAO.CuadrillasAsistentesDao
import com.gomu.festup.data.localDatabase.DAO.EventoDao
import com.gomu.festup.data.localDatabase.DAO.IntegranteDao
import com.gomu.festup.data.localDatabase.DAO.SeguidoresDao
import com.gomu.festup.data.localDatabase.DAO.UsuarioDao
import com.gomu.festup.data.localDatabase.DAO.UsuariosAsistentesDao
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.CuadrillasAsistentes
import com.gomu.festup.data.localDatabase.Entities.Evento
import com.gomu.festup.data.localDatabase.Entities.Integrante
import com.gomu.festup.data.localDatabase.Entities.Seguidores
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.data.localDatabase.Entities.UsuariosAsistentes

/**
 * Base de datos con las entidades almacenadas en ROOM, el [TypeConverter]
 */
@Database(entities = [Usuario::class, Cuadrilla::class, Integrante::class,
    Seguidores::class, Evento::class, UsuariosAsistentes::class,
    CuadrillasAsistentes::class], version=1)
@TypeConverters(Converter::class)
abstract class Database: RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun cuadrillaDao(): CuadrillaDao
    abstract fun eventoDao(): EventoDao
    abstract fun integranteDao(): IntegranteDao
    abstract fun seguidoresDao(): SeguidoresDao
    abstract fun usuariosAsistentesDao(): UsuariosAsistentesDao
    abstract fun cuadrillasAsistentesDao(): CuadrillasAsistentesDao
}


