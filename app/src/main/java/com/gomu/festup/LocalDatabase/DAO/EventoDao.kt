package com.gomu.festup.LocalDatabase.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gomu.festup.LocalDatabase.Entities.Evento
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvento(evento: Evento)
    @Transaction
    @Query("SELECT * FROM Evento")
    fun todosLosEventos(): Flow<List<Evento>>

    @Transaction
    @Query("SELECT * FROM Evento WHERE id=:id")
    fun getEvento(id:String): Flow<Evento>

    @Update
    fun editarEvento(evento: Evento): Int

}