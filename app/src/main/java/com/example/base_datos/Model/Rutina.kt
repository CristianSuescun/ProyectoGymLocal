package com.example.base_datos.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rutinas")
data class Rutina(
    @PrimaryKey(autoGenerate = true) val id: Int? = null, // Clave primaria
    val usuarioId: Int, // Clave foránea que referencia a Usuario(id)
    val nombre: String,
    val descripcion: String?,
    val dia: String, // Día de la semana
    val completado: Boolean,
    val fechaCreacion: String // Usa el formato adecuado para fecha
)
