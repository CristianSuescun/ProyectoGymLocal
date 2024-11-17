package com.example.base_datos.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.base_datos.Repository.UsuariosRepository

@Composable
fun InicioScreen(
    navController: NavController,
    usuarioId: Int,
    usuariosRepository: UsuariosRepository
) {
    var usuario by remember { mutableStateOf<com.example.base_datos.Model.Usuario?>(null) }
    var nombreUsuario by remember { mutableStateOf("Usuario") }
    var isAdmin by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(usuarioId) {
        try {
            usuario = usuariosRepository.getUsuarioById(usuarioId)
            if (usuario != null) {
                nombreUsuario = usuario?.nombre ?: "Usuario"
                isAdmin = usuario?.esAdmin == true
            } else {
                errorMessage = "Usuario no encontrado"
            }
        } catch (e: Exception) {
            errorMessage = "Error al cargar el usuario: ${e.message}"
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFF90CAF9))
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (loading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                )
            } else {
                Text(
                    text = "Bienvenido, $nombreUsuario!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color(0xFF0D47A1),
                        fontSize = 28.sp
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                val buttonModifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(2.dp, RoundedCornerShape(12.dp))

                Button(
                    onClick = { navController.navigate("ejerciciosScreen/$usuarioId") },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5))
                ) {
                    Text("Ejercicios", color = Color.White, fontSize = 18.sp)
                }

                Button(
                    onClick = { navController.navigate("rutinasScreen/$usuarioId") },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                ) {
                    Text("Rutinas", color = Color.White, fontSize = 18.sp)
                }

                Button(
                    onClick = { navController.navigate("recordatoriosScreen/$usuarioId") },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Text("Recordatorios", color = Color.White, fontSize = 18.sp)
                }

                if (isAdmin) {
                    Text(
                        text = "Tienes permisos de administrador, puedes crear, editar y eliminar ejercicios.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF004D40)),
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .background(Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}
