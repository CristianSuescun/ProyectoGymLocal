package com.example.base_datos.Repository

import com.example.base_datos.DAO.RecordatoriosDAO
import com.example.base_datos.Model.Recordatorio

class RecordatoriosRepository(private val recordatoriosDao: RecordatoriosDAO) {

    // Inserta un nuevo recordatorio
    suspend fun insert(recordatorio: Recordatorio) {
        recordatoriosDao.insert(recordatorio)
    }

    // Obtiene todos los recordatorios para una rutina específica
    suspend fun getRecordatoriosByRutinaId(rutinaId: Int): List<Recordatorio> {
        return recordatoriosDao.getRecordatoriosByRutinaId(rutinaId)
    }

    // Obtiene un recordatorio específico por su ID
    suspend fun getRecordatorioById(recordatorioId: Int): Recordatorio? {
        return recordatoriosDao.getRecordatorioById(recordatorioId)
    }

    // Elimina un recordatorio por su ID
    suspend fun deleteById(recordatorioId: Int): Int {
        return recordatoriosDao.deleteById(recordatorioId)
    }

    // Elimina un recordatorio
    suspend fun delete(recordatorio: Recordatorio) {
        recordatoriosDao.delete(recordatorio)
    }

    // Actualiza un recordatorio existente
    suspend fun update(recordatorio: Recordatorio) {
        recordatoriosDao.update(recordatorio)
    }
}
