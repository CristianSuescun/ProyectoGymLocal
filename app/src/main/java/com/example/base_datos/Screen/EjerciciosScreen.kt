package com.example.base_datos.Screen

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.base_datos.Model.Ejercicio
import com.example.base_datos.Repository.EjerciciosRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjerciciosScreen(
    ejerciciosRepository: EjerciciosRepository,
    navController: NavController,
    usuarioId: Int
) {
    var ejercicios by remember { mutableStateOf<List<Ejercicio>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showFormDialog by remember { mutableStateOf(false) }
    var selectedEjercicio by remember { mutableStateOf<Ejercicio?>(null) }
    var showExercisesList by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Ejercicios", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                showExercisesList = !showExercisesList
                if (showExercisesList && ejercicios.isEmpty()) {
                    coroutineScope.launch {
                        isLoading = true
                        ejercicios = ejerciciosRepository.getAllEjercicios(usuarioId)
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Ejercicios")
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
                                    val ejercicioId = ejercicio.id
                                    if (ejercicioId != null) {
                                        coroutineScope.launch {
                                            ejerciciosRepository.deleteById(ejercicioId, usuarioId)
                                            ejercicios = ejerciciosRepository.getAllEjercicios(usuarioId)
                                        }
                                    } else {
                                        Toast.makeText(context, "Error: ID de ejercicio no válido", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onEdit = {
                                    selectedEjercicio = ejercicio
                                    showFormDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showFormDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear Nuevo Ejercicio")
        }
    }

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
                    ejercicios = ejerciciosRepository.getAllEjercicios(usuarioId)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Nombre: ${ejercicio.nombre}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Descripción: ${ejercicio.descripcion}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Duración: ${ejercicio.duracion} minutos", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Eliminar", color = MaterialTheme.colorScheme.onError)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar", color = MaterialTheme.colorScheme.onPrimary)
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
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val duracionInt = duracion.toIntOrNull() ?: 0
                if (nombre.isBlank() || duracionInt <= 0) {
                    Toast.makeText(context, "Por favor ingresa un nombre y duración válida", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val nuevoEjercicio = Ejercicio(
                    id = ejercicio?.id,
                    nombre = nombre,
                    descripcion = descripcion,
                    duracion = duracionInt,
                    usuarioId = usuarioId
                )
                onSave(nuevoEjercicio)
            }) {
                Text(if (ejercicio == null) "Guardar" else "Actualizar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text(if (ejercicio == null) "Crear Ejercicio" else "Editar Ejercicio") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Ejercicio") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = duracion,
                    onValueChange = { duracion = it },
                    label = { Text("Duración (minutos)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
