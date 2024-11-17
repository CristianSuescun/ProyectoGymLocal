package com.example.base_datos.Screen

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.base_datos.Model.Ejercicio
import com.example.base_datos.Repository.EjerciciosRepository
import com.example.base_datos.Repository.UsuariosRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjerciciosScreen(
    ejerciciosRepository: EjerciciosRepository,
    usuariosRepository: UsuariosRepository,
    navController: NavController,
    usuarioId: Int
) {
    var ejercicios by remember { mutableStateOf<List<Ejercicio>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showFormDialog by remember { mutableStateOf(false) }
    var selectedEjercicio by remember { mutableStateOf<Ejercicio?>(null) }
    var showExercisesList by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Verificar si el usuario es administrador
    LaunchedEffect(usuarioId) {
        val usuario = usuariosRepository.getUsuarioById(usuarioId)
        isAdmin = usuario?.esAdmin == true
    }

    // Obtener la lista de ejercicios según los permisos
    LaunchedEffect(isAdmin) {
        coroutineScope.launch {
            isLoading = true
            ejercicios = ejerciciosRepository.getEjercicios(usuarioId)
            isLoading = false
        }
    }

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
        Spacer(modifier = Modifier.height(50.dp)) // Ajuste para bajar el título

        Text(
            text = "Ejercicios",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color(0xFF0D47A1), // Azul oscuro
                fontSize = 26.sp
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                showExercisesList = !showExercisesList
                if (showExercisesList && ejercicios.isEmpty()) {
                    coroutineScope.launch {
                        isLoading = true
                        ejercicios = ejerciciosRepository.getEjercicios(usuarioId)
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)) // Azul
        ) {
            Text("Ver Ejercicios", color = Color.White, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Crossfade(targetState = isLoading) { loading ->
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (showExercisesList) {
                if (ejercicios.isEmpty()) {
                    Text(text = "No hay ejercicios disponibles", style = MaterialTheme.typography.bodyLarge)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(ejercicios) { ejercicio ->
                            EjercicioItem(
                                ejercicio = ejercicio,
                                onDelete = {
                                    if (isAdmin) {
                                        val ejercicioId = ejercicio.id
                                        if (ejercicioId != null) {
                                            coroutineScope.launch {
                                                ejerciciosRepository.delete(ejercicio, usuarioId)
                                                ejercicios = ejerciciosRepository.getEjercicios(usuarioId)
                                            }
                                        } else {
                                            Toast.makeText(context, "Error: ID de ejercicio no válido", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "No tienes permisos para eliminar este ejercicio.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onEdit = {
                                    if (isAdmin) {
                                        selectedEjercicio = ejercicio
                                        showFormDialog = true
                                    } else {
                                        Toast.makeText(context, "No tienes permisos para editar este ejercicio.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Solo los administradores pueden crear ejercicios
        if (isAdmin) {
            Button(
                onClick = { showFormDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)) // Azul
            ) {
                Text("Crear Nuevo Ejercicio", color = Color.White, fontSize = 18.sp)
            }
        }
    }

    // Mostrar el formulario para crear o editar ejercicios
    if (showFormDialog) {
        EjercicioFormDialog(
            ejercicio = selectedEjercicio,
            usuarioId = usuarioId,
            onDismiss = {
                showFormDialog = false
                selectedEjercicio = null
            },
            onSave = { ejercicio ->
                coroutineScope.launch {
                    if (ejercicio.id != null) {
                        ejerciciosRepository.update(ejercicio, usuarioId)
                    } else {
                        ejerciciosRepository.insert(ejercicio, usuarioId)
                    }
                    ejercicios = ejerciciosRepository.getEjercicios(usuarioId)
                    showFormDialog = false
                    selectedEjercicio = null
                }
            }
        )
    }
}

@Composable
fun EjercicioItem(
    ejercicio: Ejercicio,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    // Contenedor de cada item de la lista
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Información del ejercicio
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Nombre: ${ejercicio.nombre}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Descripción: ${ejercicio.descripcion}", style = MaterialTheme.typography.bodySmall)
                // Mostrar duración del ejercicio
                Text(text = "Duración: ${ejercicio.duracion} min", style = MaterialTheme.typography.bodySmall)
            }

            // Botones de acción (editar y eliminar)
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar ejercicio")
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar ejercicio")
                }
            }
        }
    }
}

@Composable
fun EjercicioFormDialog(
    ejercicio: Ejercicio?,
    usuarioId: Int,
    onDismiss: () -> Unit,
    onSave: (Ejercicio) -> Unit
) {
    var nombre by remember { mutableStateOf(ejercicio?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(ejercicio?.descripcion ?: "") }
    var duracion by remember { mutableStateOf(ejercicio?.duracion?.toString() ?: "") }
    var errorMessage by remember { mutableStateOf("") } // Mensaje de error si faltan campos

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (ejercicio != null) "Editar Ejercicio" else "Crear Ejercicio") },
        text = {
            Column {
                // Campo de nombre
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Campo de descripción
                TextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Campo de duración
                TextField(
                    value = duracion,
                    onValueChange = { duracion = it },
                    label = { Text("Duración (min)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                // Mostrar mensaje de error si no se llenan los campos
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validar si los campos están completos y si la duración es válida
                    if (nombre.isEmpty() || descripcion.isEmpty() || duracion.isEmpty()) {
                        errorMessage = "Todos los campos son obligatorios."
                    } else {
                        val duracionInt = duracion.toIntOrNull() ?: 0
                        if (duracionInt <= 0) {
                            errorMessage = "La duración debe ser un número mayor que 0."
                        } else {
                            // Crear un nuevo ejercicio
                            val nuevoEjercicio = Ejercicio(
                                id = ejercicio?.id,
                                nombre = nombre,
                                descripcion = descripcion,
                                duracion = duracionInt,
                                usuarioId = usuarioId
                            )
                            onSave(nuevoEjercicio)
                            onDismiss() // Cerrar el formulario después de guardar
                        }
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
