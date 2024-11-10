package com.example.base_datos.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordatorios")
data class Recordatorio(
    @PrimaryKey(autoGenerate = true) val id: Int? = null, // Clave primaria
    val rutinaId: Int, // Clave foránea que referencia a Rutina(id)
    val fechaHora: String, // Fecha y hora del recordatorio
    val mensaje: String,
    val repetir: Boolean,
    val frecuencia: String // Puede ser "diaria", "semanal", etc.
)
