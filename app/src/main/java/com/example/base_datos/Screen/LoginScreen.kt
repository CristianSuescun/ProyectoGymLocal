package com.example.base_datos.Screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.base_datos.Repository.UsuariosRepository
import kotlinx.coroutines.launch
@Composable
fun LoginScreen(
    usuariosRepository: UsuariosRepository,
    navController: NavController
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E88E5), Color(0xFFFFA726)) // Azul y naranja
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título con estilo moderno
        Text(
            text = "Bienvenido al Gimnasio",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White,
                fontSize = 28.sp
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Campo de correo
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico", color = Color(0xFF1565C0)) }, // Color azul más oscuro
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Icono de correo", tint = Color(0xFF1565C0)) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5)) // Gris suave de fondo
            ,
            textStyle = LocalTextStyle.current.copy(color = Color.Black) // Texto negro
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color = Color(0xFF1565C0)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Icono de contraseña", tint = Color(0xFF1565C0)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5)), // Gris suave de fondo
            textStyle = LocalTextStyle.current.copy(color = Color.Black) // Texto negro
        )

        Spacer(modifier = Modifier.height(24.dp))

// Mensaje de error
        if (errorMessage.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color(0xFFFFF9C4), RoundedCornerShape(8.dp)) // Fondo amarillo suave
                    .padding(12.dp) // Espaciado interno para el mensaje
            ) {
                Text(
                    text = errorMessage,
                    color = Color(0xFF795548), // Color marrón suave para el texto
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Botón de inicio de sesión
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Por favor ingrese ambos campos"
                    return@Button
                }

                showLoading = true
                coroutineScope.launch {
                    try {
                        val usuarios = usuariosRepository.getAllUsuarios()
                        if (usuarios.isEmpty()) {
                            errorMessage = "No hay usuarios registrados"
                            showLoading = false
                            return@launch
                        }

                        val usuario = usuarios.find { it.email == email && it.password == password }
                        if (usuario != null) {
                            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            navController.navigate("inicioScreen/${usuario.id}")
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
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .shadow(8.dp, RoundedCornerShape(16.dp)), // Sombra para botón
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)) // Naranja como color complementario
        ) {
            if (showLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Iniciar sesión", color = Color.White, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón de registro
        Button(
            onClick = { navController.navigate("registroScreen") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4)) // Azul sólido para el fondo
        ) {
            Text(
                text = "Registrarse",
                color = Color.White, // Color blanco para el texto
                fontSize = 18.sp
            )
        }
    }
}


