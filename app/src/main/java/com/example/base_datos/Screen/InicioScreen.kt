package com.example.base_datos.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.base_datos.Repository.UsuariosRepository

@Composable
fun InicioScreen(
    navController: NavController,
    usuarioId: Int, // Recibir el usuarioId como parámetro
    usuariosRepository: UsuariosRepository // Repositorio de usuarios para obtener el nombre
) {
    var usuario by remember { mutableStateOf<com.example.base_datos.Model.Usuario?>(null) }
    var nombreUsuario by remember { mutableStateOf("Usuario") }
    var isAdmin by remember { mutableStateOf(false) } // Variable para verificar si es administrador
    var loading by remember { mutableStateOf(true) } // Mostrar loading mientras se obtiene el usuario
    var errorMessage by remember { mutableStateOf("") } // Mostrar mensaje de error si no se encuentra el usuario

    // Lanzamos la coroutine para obtener el usuario
    LaunchedEffect(usuarioId) {
        try {
            // Intentamos obtener el usuario de la base de datos
            usuario = usuariosRepository.getUsuarioById(usuarioId)
            if (usuario != null) {
                nombreUsuario = usuario?.nombre ?: "Usuario"
                isAdmin = usuario?.esAdmin == true // Verificamos si es administrador
            } else {
                errorMessage = "Usuario no encontrado"
            }
        } catch (e: Exception) {
            // En caso de error, mostramos un mensaje
            errorMessage = "Error al cargar el usuario: ${e.message}"
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (loading) {
            CircularProgressIndicator() // Mostrar indicador de carga mientras se obtiene el usuario
        } else {
            if (errorMessage.isNotEmpty()) {
                // Mostrar error si no se encuentra el usuario
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            } else {
                Text(
                    text = "Bienvenido, $nombreUsuario!",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Botón para Ejercicio
                Button(
                    onClick = { navController.navigate("ejerciciosScreen/$usuarioId") }, // Pasar usuarioId
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text("Ejercicios")
                }

                // Botón para Rutinas
                Button(
                    onClick = { navController.navigate("rutinasScreen/$usuarioId") },  // Pasar usuarioId
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text("Rutinas")
                }

                // Botón para Recordatorios
                Button(
                    onClick = { navController.navigate("recordatoriosScreen") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text("Recordatorios")
                }

                // Solo los administradores pueden crear, editar o eliminar ejercicios
                if (isAdmin) {
                    // Si el usuario es administrador, puedes mostrar botones adicionales
                    Text(
                        text = "Tienes permisos de administrador, puedes crear, editar y eliminar ejercicios.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }
        }
    }
}
