package com.example.base_datos.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rutina_ejercicio")
data class RutinaEjercicio(
    @PrimaryKey(autoGenerate = true) val id: Int? = null, // Clave primaria
    val rutinaId: Int, // Clave foránea que referencia a Rutina(id)
    val ejercicioId: Int, // Clave foránea que referencia a Ejercicio(id)
    val repeticiones: Int,
    val series: Int
)
