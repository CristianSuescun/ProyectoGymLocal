package com.example.base_datos.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import com.example.base_datos.Model.RutinaEjercicio

@Dao
interface RutinaEjercicioDAO {

    // Inserta una nueva relación entre rutina y ejercicio
    @Insert
    suspend fun insert(rutinaEjercicio: RutinaEjercicio)

    // Obtiene todos los ejercicios para una rutina dada
    @Query("SELECT * FROM rutina_ejercicio WHERE rutinaId = :rutinaId")
    suspend fun getEjerciciosByRutinaId(rutinaId: Int): List<RutinaEjercicio>

    // Elimina una relación entre rutina y ejercicio
    @Delete
    suspend fun delete(rutinaEjercicio: RutinaEjercicio)

    // Elimina una relación específica entre rutina y ejercicio por sus ID
    @Query("DELETE FROM rutina_ejercicio WHERE rutinaId = :rutinaId AND ejercicioId = :ejercicioId")
    suspend fun deleteByIds(rutinaId: Int, ejercicioId: Int): Int

    // Actualiza una relación existente entre rutina y ejercicio
    @Update
    suspend fun update(rutinaEjercicio: RutinaEjercicio)
}
