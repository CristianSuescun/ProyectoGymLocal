package com.example.base_datos.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.base_datos.Model.Usuario

@Dao
interface UsuariosDAO {

    @Insert
    suspend fun insert(usuario: Usuario)

    @Query("SELECT * FROM usuarios")
    suspend fun getAllUsuarios(): List<Usuario>

    @Query("SELECT * FROM usuarios WHERE id = :usuarioId")
    suspend fun getUsuarioById(usuarioId: Int): Usuario?

    @Delete
    suspend fun delete(usuario: Usuario)

    @Query("DELETE FROM usuarios WHERE id = :usuarioId")
    suspend fun deleteById(usuarioId: Int): Int

    @Update
    suspend fun update(usuario: Usuario)

    // Método para verificar si hay algún usuario administrador
    @Query("SELECT COUNT(*) FROM usuarios WHERE esAdmin = 1")
    suspend fun countAdmins(): Int
}

