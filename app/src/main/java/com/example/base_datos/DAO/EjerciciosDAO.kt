package com.example.base_datos.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.base_datos.Model.Ejercicio

@Dao
interface EjerciciosDAO {

    // Inserta un nuevo ejercicio, ahora incluye el usuarioId
    @Insert
    suspend fun insert(ejercicio: Ejercicio)

    // Obtiene todos los ejercicios de un usuario específico
    @Query("SELECT * FROM ejercicios WHERE usuarioId = :usuarioId")
    suspend fun getAllEjerciciosByUser(usuarioId: Int): List<Ejercicio>

    // Obtiene un ejercicio específico por ID
    @Query("SELECT * FROM ejercicios WHERE id = :ejercicioId")
    suspend fun getEjercicioById(ejercicioId: Int): Ejercicio?

    // Elimina un ejercicio por objeto
    @Delete
    suspend fun delete(ejercicio: Ejercicio)

    // Elimina un ejercicio por ID
    @Query("DELETE FROM ejercicios WHERE id = :ejercicioId AND usuarioId = :usuarioId")
    suspend fun deleteById(ejercicioId: Int, usuarioId: Int): Int

    // Actualiza un ejercicio existente
    @Update
    suspend fun update(ejercicio: Ejercicio)
}
