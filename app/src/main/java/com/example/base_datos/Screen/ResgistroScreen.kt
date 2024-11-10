package com.example.base_datos.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.base_datos.Repository.UsuariosRepository
import com.example.base_datos.Model.Usuario
import java.time.LocalDate
import kotlinx.coroutines.launch

@Composable
fun RegistroScreen(
    usuariosRepository: UsuariosRepository,  // Pasamos el repositorio para insertar el usuario
    navController: NavController
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fechaRegistro by remember { mutableStateOf(LocalDate.now().toString()) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Usamos la coroutineScope para manejar la inserción del usuario en la base de datos
    val scope = rememberCoroutineScope()

    // Interfaz de la pantalla de registro
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Registro de Usuario", style = MaterialTheme.typography.titleLarge)

        // Campo para el nombre
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo para el email
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo para la contraseña
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Fecha de registro (automáticamente se obtiene la fecha actual)
        TextField(
            value = fechaRegistro,
            onValueChange = { fechaRegistro = it },
            label = { Text("Fecha de Registro") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        // Mensaje de error (si lo hubiera)
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }

        // Indicador de carga
        if (isLoading) {
            CircularProgressIndicator()
        }

        // Botón para registrar el usuario
        Button(
            onClick = {
                // Verificamos si los campos no están vacíos
                if (nombre.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    val nuevoUsuario = Usuario(
                        nombre = nombre,
                        email = email,
                        password = password,
                        fechaRegistro = fechaRegistro
                    )

                    // Usamos una coroutineScope para ejecutar la función suspendida
                    scope.launch {
                        try {
                            isLoading = true
                            // Registramos al usuario en la base de datos
                            usuariosRepository.insert(nuevoUsuario)
                            navController.popBackStack() // Volver a la pantalla anterior
                        } catch (e: Exception) {
                            errorMessage = "Error al registrar el usuario: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                } else {
                    errorMessage = "Por favor, complete todos los campos."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }
    }
}
