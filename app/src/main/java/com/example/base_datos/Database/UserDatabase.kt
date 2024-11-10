package com.example.base_datos.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.base_datos.Model.Usuario
import com.example.base_datos.Model.Rutina
import com.example.base_datos.Model.Ejercicio
import com.example.base_datos.Model.RutinaEjercicio
import com.example.base_datos.Model.Recordatorio
import com.example.base_datos.DAO.UsuariosDAO
import com.example.base_datos.DAO.RutinasDAO
import com.example.base_datos.DAO.EjerciciosDAO
import com.example.base_datos.DAO.RutinaEjercicioDAO
import com.example.base_datos.DAO.RecordatoriosDAO

@Database(
    entities = [Usuario::class, Rutina::class, Ejercicio::class, RutinaEjercicio::class, Recordatorio::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs para interactuar con las tablas
    abstract fun usuariosDao(): UsuariosDAO
    abstract fun rutinasDao(): RutinasDAO
    abstract fun ejerciciosDao(): EjerciciosDAO
    abstract fun rutinaEjercicioDao(): RutinaEjercicioDAO
    abstract fun recordatoriosDao(): RecordatoriosDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "base_de_datos2_gym" // Nombre de la base de datos
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
