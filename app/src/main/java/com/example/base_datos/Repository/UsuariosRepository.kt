package com.example.base_datos.Repository

import com.example.base_datos.DAO.UsuariosDAO
import com.example.base_datos.Model.Usuario

class UsuariosRepository(private val usuariosDao: UsuariosDAO) {

    // Inserta un nuevo usuario con la lógica de administrador
    suspend fun insert(usuario: Usuario) {
        // Verifica si hay usuarios existentes en la base de datos
        val usuariosExistentes = getAllUsuarios()
        val esPrimerUsuario = usuariosExistentes.isEmpty()

        // Si es el primer usuario, se marca como administrador
        val usuarioConRol = if (esPrimerUsuario) {
            usuario.copy(esAdmin = true)
        } else {
            usuario.copy(esAdmin = false)
        }

        // Inserta el usuario en la base de datos
        usuariosDao.insert(usuarioConRol)
    }

    // Obtiene todos los usuarios
    suspend fun getAllUsuarios(): List<Usuario> {
        return usuariosDao.getAllUsuarios()
    }

    // Obtiene un usuario por ID
    suspend fun getUsuarioById(usuarioId: Int): Usuario? {
        return usuariosDao.getUsuarioById(usuarioId)
    }

    // Elimina un usuario por ID
    suspend fun deleteById(usuarioId: Int): Int {
        return usuariosDao.deleteById(usuarioId)
    }

    // Elimina un usuario
    suspend fun delete(usuario: Usuario) {
        usuariosDao.delete(usuario)
    }

    // Actualiza un usuario existente
    suspend fun update(usuario: Usuario) {
        usuariosDao.update(usuario)
    }
}
