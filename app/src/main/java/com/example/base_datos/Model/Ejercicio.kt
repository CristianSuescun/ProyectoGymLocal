package com.example.base_datos.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ejercicios")
data class Ejercicio(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,  // Clave primaria
    val nombre: String,  // Nombre del ejercicio
    val descripcion: String?,  // Descripción del ejercicio
    val duracion: Int,  // Duración del ejercicio en minutos
    val usuarioId: Int  // ID del usuario al que pertenece el ejercicio
)
