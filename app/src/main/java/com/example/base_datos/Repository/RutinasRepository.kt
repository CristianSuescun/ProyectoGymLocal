package com.example.base_datos.Repository

import com.example.base_datos.DAO.RutinasDAO
import com.example.base_datos.Model.Rutina

class RutinasRepository(private val rutinasDao: RutinasDAO) {

    // Inserta una nueva rutina
    suspend fun insert(rutina: Rutina) {
        rutinasDao.insert(rutina)
    }

    // Obtiene las rutinas de un usuario específico
    suspend fun getRutinasByUsuarioId(usuarioId: Int): List<Rutina> {
        return rutinasDao.getRutinasByUsuarioId(usuarioId)
    }

    // Obtiene una rutina por ID
    suspend fun getRutinaById(rutinaId: Int): Rutina? {
        return rutinasDao.getRutinaById(rutinaId)
    }

    // Elimina una rutina por ID
    suspend fun deleteById(rutinaId: Int): Int {
        return rutinasDao.deleteById(rutinaId)
    }

    // Elimina una rutina
    suspend fun delete(rutina: Rutina) {
        rutinasDao.delete(rutina)
    }

    // Actualiza una rutina existente
    suspend fun update(rutina: Rutina) {
        rutinasDao.update(rutina)
    }
}
