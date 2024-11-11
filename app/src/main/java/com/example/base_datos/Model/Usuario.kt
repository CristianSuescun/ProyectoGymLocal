package com.example.base_datos.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int? = null, // Clave primaria
    val nombre: String,
    val email: String,
    val password: String,
    val fechaRegistro: String, // Usa el formato adecuado para fecha
    val esAdmin: Boolean = false // Campo para indicar si es administrador
)
