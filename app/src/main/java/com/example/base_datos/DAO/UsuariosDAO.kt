package com.example.base_datos.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.base_datos.Model.Usuario

@Dao
interface UsuariosDAO {

    // Inserta un nuevo usuario
    @Insert
    suspend fun insert(usuario: Usuario)

    // Obtiene todos los usuarios
    @Query("SELECT * FROM usuarios")
    suspend fun getAllUsuarios(): List<Usuario>

    // Obtiene un usuario específico por ID
    @Query("SELECT * FROM usuarios WHERE id = :usuarioId")
    suspend fun getUsuarioById(usuarioId: Int): Usuario?

    // Elimina un usuario por objeto
    @Delete
    suspend fun delete(usuario: Usuario)

    // Elimina un usuario por ID
    @Query("DELETE FROM usuarios WHERE id = :usuarioId")
    suspend fun deleteById(usuarioId: Int): Int

    // Actualiza un usuario existente
    @Update
    suspend fun update(usuario: Usuario)
}
