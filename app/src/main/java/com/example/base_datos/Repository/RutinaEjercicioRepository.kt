package com.example.base_datos.Repository

import com.example.base_datos.DAO.RutinaEjercicioDAO
import com.example.base_datos.Model.RutinaEjercicio

class RutinaEjercicioRepository(private val rutinaEjercicioDao: RutinaEjercicioDAO) {

    // Inserta una nueva relación rutina-ejercicio
    suspend fun insert(rutinaEjercicio: RutinaEjercicio) {
        rutinaEjercicioDao.insert(rutinaEjercicio)
    }

    // Obtiene todas las relaciones rutina-ejercicio para una rutina específica
    suspend fun getEjerciciosByRutinaId(rutinaId: Int): List<RutinaEjercicio> {
        return rutinaEjercicioDao.getEjerciciosByRutinaId(rutinaId)
    }

    // Elimina una relación específica entre rutina y ejercicio por sus ID
    suspend fun deleteByIds(rutinaId: Int, ejercicioId: Int): Int {
        return rutinaEjercicioDao.deleteByIds(rutinaId, ejercicioId)
    }

    // Elimina una relación rutina-ejercicio
    suspend fun delete(rutinaEjercicio: RutinaEjercicio) {
        rutinaEjercicioDao.delete(rutinaEjercicio)
    }

    // Actualiza una relación rutina-ejercicio existente
    suspend fun update(rutinaEjercicio: RutinaEjercicio) {
        rutinaEjercicioDao.update(rutinaEjercicio)
    }

    // Método para obtener todas las relaciones rutina-ejercicio
    suspend fun getAll(): List<RutinaEjercicio> {
        return rutinaEjercicioDao.getAll()
    }
}
