package com.example.base_datos.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.base_datos.Repository.UsuariosRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    usuariosRepository: UsuariosRepository, // Pasar el repositorio de usuarios
    navController: NavController // Para la navegación al registro o a la pantalla principal
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") } // Para mensajes de error
    var showLoading by remember { mutableStateOf(false) } // Para mostrar la carga durante el inicio de sesión
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campo de texto para el email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de texto para la contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Si hay un mensaje de error, lo mostramos
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón de iniciar sesión
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Por favor ingrese ambos campos"
                    return@Button
                }

                showLoading = true
                coroutineScope.launch {
                    try {
                        // Obtener todos los usuarios
                        val usuarios = usuariosRepository.getAllUsuarios()

                        if (usuarios.isEmpty()) {
                            errorMessage = "No hay usuarios registrados"
                            showLoading = false
                            return@launch
                        }

                        // Verificar si existe un usuario con el email y la contraseña proporcionados
                        val usuario = usuarios.find { it.email == email && it.password == password }
                        if (usuario != null) {
                            // Login exitoso, redirigir a la pantalla de inicio o pantalla correspondiente
                            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            navController.navigate("inicioScreen/${usuario.id}") // Redirigimos a la pantalla de inicio con el usuarioId
                        } else {
                            errorMessage = "Usuario o contraseña incorrectos"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Error al verificar los datos: ${e.message}"
                    } finally {
                        showLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !showLoading
        ) {
            if (showLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Iniciar sesión")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para ir al registro
        Button(
            onClick = {
                navController.navigate("registroScreen") // Navegar a la pantalla de registro
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }
    }
}
