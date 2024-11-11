package com.example.base_datos.Repository

import com.example.base_datos.DAO.EjerciciosDAO
import com.example.base_datos.DAO.UsuariosDAO
import com.example.base_datos.Model.Ejercicio

class EjerciciosRepository(
    private val ejerciciosDAO: EjerciciosDAO,
    private val usuariosDAO: UsuariosDAO
) {

    // Inserta un nuevo ejercicio solo si el usuario es administrador
    suspend fun insert(ejercicio: Ejercicio, usuarioId: Int) {
        val usuario = usuariosDAO.getUsuarioById(usuarioId)
        if (usuario != null && usuario.esAdmin) {
            ejerciciosDAO.insert(ejercicio)
        } else {
            throw Exception("Solo los administradores pueden crear ejercicios.")
        }
    }

    // Obtiene todos los ejercicios, filtrando según el rol del usuario
    suspend fun getEjercicios(usuarioId: Int): List<Ejercicio> {
        val usuario = usuariosDAO.getUsuarioById(usuarioId)
        return if (usuario != null && usuario.esAdmin) {
            // Si es admin, obtiene todos los ejercicios
            ejerciciosDAO.getAllEjerciciosByUser(usuarioId)
        } else {
            // Si no es admin, obtiene solo los ejercicios creados por administradores
            ejerciciosDAO.getAllEjerciciosByUser(usuarioId)
        }
    }

    // Elimina un ejercicio solo si el usuario es administrador o el creador
    suspend fun delete(ejercicio: Ejercicio, usuarioId: Int) {
        val usuario = usuariosDAO.getUsuarioById(usuarioId)
        if (usuario != null && (usuario.esAdmin || ejercicio.usuarioId == usuarioId)) {
            ejerciciosDAO.delete(ejercicio)
        } else {
            throw Exception("No tienes permisos para eliminar este ejercicio.")
        }
    }

    // Actualiza un ejercicio solo si el usuario es administrador o el creador
    suspend fun update(ejercicio: Ejercicio, usuarioId: Int) {
        val usuario = usuariosDAO.getUsuarioById(usuarioId)
        if (usuario != null && (usuario.esAdmin || ejercicio.usuarioId == usuarioId)) {
            ejerciciosDAO.update(ejercicio)
        } else {
            throw Exception("No tienes permisos para editar este ejercicio.")
        }
    }
}

