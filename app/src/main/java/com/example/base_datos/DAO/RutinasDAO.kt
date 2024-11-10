package com.example.base_datos.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.base_datos.Model.Rutina

@Dao
interface RutinasDAO {

    @Insert
    suspend fun insert(rutina: Rutina)

    // Obtiene las rutinas de un usuario específico
    @Query("SELECT * FROM rutinas WHERE usuarioId = :usuarioId")
    suspend fun getRutinasByUsuarioId(usuarioId: Int): List<Rutina>

    // Obtiene una rutina por su ID
    @Query("SELECT * FROM rutinas WHERE id = :rutinaId")
    suspend fun getRutinaById(rutinaId: Int): Rutina?

    @Delete
    suspend fun delete(rutina: Rutina)

    // Elimina una rutina por su ID
    @Query("DELETE FROM rutinas WHERE id = :rutinaId")
    suspend fun deleteById(rutinaId: Int): Int

    @Update
    suspend fun update(rutina: Rutina)
}
