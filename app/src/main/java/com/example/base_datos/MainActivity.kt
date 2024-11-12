package com.example.base_datos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.base_datos.DAO.UsuariosDAO
import com.example.base_datos.DAO.EjerciciosDAO
import com.example.base_datos.DAO.RutinaEjercicioDAO  // Asegúrate de importar el DAO de RutinaEjercicio
import com.example.base_datos.Database.AppDatabase
import com.example.base_datos.Repository.UsuariosRepository
import com.example.base_datos.Repository.RutinasRepository
import com.example.base_datos.Repository.EjerciciosRepository
import com.example.base_datos.Repository.RutinaEjercicioRepository  // Asegúrate de importar el repositorio de RutinaEjercicio
import com.example.base_datos.Screen.LoginScreen
import com.example.base_datos.Screen.RegistroScreen
import com.example.base_datos.Screen.InicioScreen
import com.example.base_datos.Screen.RutinasScreen
import com.example.base_datos.Screen.EjerciciosScreen
import com.example.base_datos.Screen.RutinasEjerciciosScreen  // Importa la pantalla de RutinasEjerciciosScreen
import com.example.base_datos.ui.theme.Base_DatosTheme

class MainActivity : ComponentActivity() {
    private lateinit var usuariosDAO: UsuariosDAO
    private lateinit var usuariosRepository: UsuariosRepository
    private lateinit var rutinasRepository: RutinasRepository
    private lateinit var ejerciciosDAO: EjerciciosDAO
    private lateinit var ejerciciosRepository: EjerciciosRepository
    private lateinit var rutinaEjercicioRepository: RutinaEjercicioRepository  // Declare el repositorio de RutinaEjercicio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa la base de datos
        val db = AppDatabase.getDatabase(applicationContext)
        usuariosDAO = db.usuariosDao()
        usuariosRepository = UsuariosRepository(usuariosDAO)
        rutinasRepository = RutinasRepository(db.rutinasDao())
        ejerciciosDAO = db.ejerciciosDao()

        // Inicializamos el repositorio de RutinaEjercicio
        rutinaEjercicioRepository = RutinaEjercicioRepository(db.rutinaEjercicioDao())

        // Aquí debes pasar tanto 'ejerciciosDAO' como 'usuariosDAO' al repositorio de Ejercicios
        ejerciciosRepository = EjerciciosRepository(ejerciciosDAO, usuariosDAO)

        enableEdgeToEdge()
        setContent {
            Base_DatosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Configuración de la navegación
                    NavHost(navController = navController, startDestination = "loginScreen") {
                        // Composable para la pantalla de Login
                        composable("loginScreen") {
                            LoginScreen(usuariosRepository = usuariosRepository, navController = navController)
                        }
                        // Composable para la pantalla de Registro
                        composable("registroScreen") {
                            RegistroScreen(usuariosRepository = usuariosRepository, navController = navController)
                        }
                        // Composable para la pantalla de Inicio (después de login)
                        composable("inicioScreen/{usuarioId}") { backStackEntry ->
                            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toInt() ?: 0
                            InicioScreen(
                                navController = navController,
                                usuarioId = usuarioId, // Pasamos el usuarioId
                                usuariosRepository = usuariosRepository
                            )
                        }
                        // Composable para la pantalla de Rutinas
                        composable("rutinasScreen/{usuarioId}") { backStackEntry ->
                            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toInt() ?: 0
                            RutinasScreen(
                                rutinasRepository = rutinasRepository,
                                navController = navController,
                                usuarioId = usuarioId // Ahora pasamos el usuarioId
                            )
                        }
                        // Composable para la pantalla de Ejercicios (recibe usuarioId como argumento)
                        composable("ejerciciosScreen/{usuarioId}") { backStackEntry ->
                            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toInt() ?: 0
                            EjerciciosScreen(
                                ejerciciosRepository = ejerciciosRepository,
                                usuariosRepository = usuariosRepository,  // Aquí pasas el usuariosRepository
                                navController = navController,
                                usuarioId = usuarioId
                            )
                        }
                        // Composable para la pantalla de RutinasEjerciciosScreen
                        composable("rutinasEjerciciosScreen/{usuarioId}") { backStackEntry ->
                            val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toInt() ?: 0
                            RutinasEjerciciosScreen(
                                rutinasRepository = rutinasRepository,
                                ejerciciosRepository = ejerciciosRepository,
                                rutinaEjercicioRepository = rutinaEjercicioRepository,  // Pasamos el repositorio de RutinaEjercicio
                                usuarioId = usuarioId,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}
