package com.example.base_datos.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.base_datos.Model.Recordatorio

@Dao
interface RecordatoriosDAO {

    // Inserta un nuevo recordatorio
    @Insert
    suspend fun insert(recordatorio: Recordatorio)

    // Obtiene todos los recordatorios para una rutina específica
    @Query("SELECT * FROM recordatorios WHERE rutinaId = :rutinaId")
    suspend fun getRecordatoriosByRutinaId(rutinaId: Int): List<Recordatorio>

    // Obtiene un recordatorio específico por su ID
    @Query("SELECT * FROM recordatorios WHERE id = :recordatorioId")
    suspend fun getRecordatorioById(recordatorioId: Int): Recordatorio?

    // Elimina un recordatorio
    @Delete
    suspend fun delete(recordatorio: Recordatorio)

    // Elimina un recordatorio por ID
    @Query("DELETE FROM recordatorios WHERE id = :recordatorioId")
    suspend fun deleteById(recordatorioId: Int): Int

    // Actualiza un recordatorio existente
    @Update
    suspend fun update(recordatorio: Recordatorio)
}
