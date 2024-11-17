package com.example.base_datos.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.base_datos.Repository.UsuariosRepository
import com.example.base_datos.Model.Usuario
import java.time.LocalDate
import kotlinx.coroutines.launch

/**
 * Pantalla de registro de usuario que permite a un nuevo usuario crear su cuenta
 * introduciendo su nombre, correo electrónico y una contraseña.
 * Los datos se validan antes de realizar el registro.
 *
 * @param usuariosRepository El repositorio que maneja las operaciones de usuarios.
 * @param navController El controlador de navegación para manejar la transición entre pantallas.
 */
@Composable
fun RegistroScreen(
    usuariosRepository: UsuariosRepository,
    navController: NavController
) {
    // Variables de estado para los campos de entrada
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fechaRegistro by remember { mutableStateOf(LocalDate.now().toString()) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Creación del alcance para las corutinas
    val scope = rememberCoroutineScope()

    // Estructura de la pantalla, con un fondo degradado
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFF90CAF9)) // Azul claro a azul más fuerte
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Espacio adicional para evitar que el contenido se corte con la cámara
        Spacer(modifier = Modifier.height(40.dp))

        // Título de la pantalla
        Text(
            text = "Registro de Usuario",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color(0xFF0D47A1),
                fontSize = 26.sp
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campo de entrada para el nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre", color = Color(0xFF1565C0)) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5)), // Gris suave de fondo
            textStyle = LocalTextStyle.current.copy(color = Color.Black) // Texto negro
        )

        // Campo de entrada para el correo electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico", color = Color(0xFF1565C0)) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5)),
            textStyle = LocalTextStyle.current.copy(color = Color.Black) // Texto negro
        )

        // Campo de entrada para la contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color = Color(0xFF1565C0)) },
            visualTransformation = PasswordVisualTransformation(), // Transforma la contraseña en texto oculto
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5)),
            textStyle = LocalTextStyle.current.copy(color = Color.Black) // Texto negro
        )

        // Campo de fecha de registro (no editable, se obtiene automáticamente la fecha actual)
        OutlinedTextField(
            value = fechaRegistro,
            onValueChange = { fechaRegistro = it },
            label = { Text("Fecha de Registro", color = Color(0xFF1565C0)) },
            enabled = false, // Campo deshabilitado
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5)),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        // Mensaje de error, si existe
        if (errorMessage.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Indicador de carga, se muestra mientras se procesa la solicitud
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White
            )
        }

        // Botón para registrar el usuario
        Button(
            onClick = {
                // Validación: Asegura que todos los campos estén completos
                if (nombre.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    val nuevoUsuario = Usuario(
                        nombre = nombre,
                        email = email,
                        password = password,
                        fechaRegistro = fechaRegistro
                    )

                    // Llamada a la corutina para insertar el nuevo usuario
                    scope.launch {
                        try {
                            isLoading = true
                            // Inserta el nuevo usuario en la base de datos
                            usuariosRepository.insert(nuevoUsuario)
                            // Regresa a la pantalla anterior una vez registrado el usuario
                            navController.popBackStack()
                        } catch (e: Exception) {
                            // Si ocurre un error, muestra un mensaje de error
                            errorMessage = "Error al registrar el usuario: ${e.message}"
                        } finally {
                            isLoading = false // Deja de mostrar el indicador de carga
                        }
                    }
                } else {
                    // Si los campos están vacíos, muestra un mensaje de error
                    errorMessage = "Por favor, complete todos los campos."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5))
        ) {
            Text("Registrar", color = Color.White, fontSize = 18.sp)
        }
    }
}
