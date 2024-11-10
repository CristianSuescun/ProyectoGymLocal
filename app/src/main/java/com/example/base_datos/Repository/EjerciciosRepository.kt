package com.example.base_datos.Repository

import com.example.base_datos.DAO.EjerciciosDAO
import com.example.base_datos.Model.Ejercicio

class EjerciciosRepository(private val ejerciciosDao: EjerciciosDAO) {

    // Inserta un nuevo ejercicio
    suspend fun insert(ejercicio: Ejercicio, usuarioId: Int) {
        ejerciciosDao.insert(ejercicio)
    }

    // Obtiene todos los ejercicios de un usuario específico
    suspend fun getAllEjercicios(usuarioId: Int): List<Ejercicio> {
        return ejerciciosDao.getAllEjerciciosByUser(usuarioId)
    }

    // Obtiene ejercicio por ID
    suspend fun getEjercicioById(ejercicioId: Int): Ejercicio? {
        return ejerciciosDao.getEjercicioById(ejercicioId)
    }

    // Elimina un ejercicio por ID, ahora incluye el usuarioId
    suspend fun deleteById(ejercicioId: Int, usuarioId: Int): Int {
        return ejerciciosDao.deleteById(ejercicioId, usuarioId)
    }

    // Elimina un ejercicio
    suspend fun delete(ejercicio: Ejercicio) {
        ejerciciosDao.delete(ejercicio)
    }

    // Actualiza un ejercicio existente
    suspend fun update(ejercicio: Ejercicio, usuarioId: Int) {
        ejerciciosDao.update(ejercicio)
    }
}
