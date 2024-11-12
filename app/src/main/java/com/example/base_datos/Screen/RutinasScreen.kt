package com.example.base_datos.Screen

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.base_datos.Model.Rutina
import com.example.base_datos.Repository.RutinasRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutinasScreen(
    rutinasRepository: RutinasRepository,
    navController: NavController,
    usuarioId: Int
) {
    var rutinas by remember { mutableStateOf<List<Rutina>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showFormDialog by remember { mutableStateOf(false) }
    var showRutinasSheet by remember { mutableStateOf(false) }
    var selectedRutina by remember { mutableStateOf<Rutina?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(usuarioId) {
        rutinas = rutinasRepository.getRutinasByUsuarioId(usuarioId) // Cargar rutinas por usuarioId
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Rutinas", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            if (rutinas.isEmpty()) {
                Text(text = "No hay rutinas disponibles", style = MaterialTheme.typography.bodyLarge)
            } else {
                Button(
                    onClick = { showRutinasSheet = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver Rutinas")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showFormDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear Nueva Rutina")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Navegar a la pantalla de seleccionar ejercicios
                navController.navigate("RutinasEjerciciosScreen/$usuarioId") // Se pasa el usuarioId a la nueva pantalla
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar Ejercicio para la Rutina")
        }
    }

    if (showFormDialog) {
        RutinaFormDialog(
            rutina = selectedRutina,
            usuarioId = usuarioId,
            onDismiss = {
                showFormDialog = false
                selectedRutina = null
            },
            onSave = { rutina ->
                coroutineScope.launch {
                    if (selectedRutina != null) {
                        rutinasRepository.update(rutina)
                    } else {
                        rutinasRepository.insert(rutina)
                    }
                    rutinas = rutinasRepository.getRutinasByUsuarioId(usuarioId)
                    showFormDialog = false
                    selectedRutina = null
                }
            }
        )
    }

    if (showRutinasSheet) {
        ModalBottomSheet(onDismissRequest = { showRutinasSheet = false }) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(rutinas) { rutina ->
                    RutinaItem(
                        rutina = rutina,
                        onDelete = {
                            coroutineScope.launch {
                                rutinasRepository.delete(rutina)
                                rutinas = rutinasRepository.getRutinasByUsuarioId(usuarioId)
                            }
                        },
                        onEdit = {
                            selectedRutina = rutina
                            showFormDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RutinaItem(
    rutina: Rutina,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "Nombre: ${rutina.nombre}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Descripción: ${rutina.descripcion}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Día: ${rutina.dia}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "¿Completada? ${if (rutina.completado) "Sí" else "No"}", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onDelete,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Eliminar", color = MaterialTheme.colorScheme.onError)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar")
        }
    }
}

@Composable
fun RutinaFormDialog(
    rutina: Rutina?,
    usuarioId: Int,
    onDismiss: () -> Unit,
    onSave: (Rutina) -> Unit
) {
    var nombre by remember { mutableStateOf(rutina?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(rutina?.descripcion ?: "") }
    var dia by remember { mutableStateOf(rutina?.dia ?: "Seleccionar Fecha") }
    var completado by remember { mutableStateOf(rutina?.completado ?: false) }
    val context = LocalContext.current

    val datePickerDialog = createDatePickerDialog(context) { year, month, dayOfMonth ->
        dia = "$dayOfMonth/${month + 1}/$year"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val nuevaRutina = Rutina(
                    id = rutina?.id,
                    nombre = nombre,
                    descripcion = descripcion,
                    dia = dia,
                    completado = completado,
                    fechaCreacion = rutina?.fechaCreacion ?: LocalDateTime.now().toString(),
                    usuarioId = usuarioId
                )
                onSave(nuevaRutina)
            }) {
                Text(if (rutina == null) "Guardar" else "Actualizar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text(if (rutina == null) "Crear Rutina" else "Editar Rutina") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la Rutina") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(dia)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = completado,
                        onCheckedChange = { completado = it }
                    )
                    Text(text = "¿Rutina Completada?", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    )
}

fun createDatePickerDialog(context: Context, onDateSet: (Int, Int, Int) -> Unit): DatePickerDialog {
    val calendar = Calendar.getInstance()
    return DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSet(year, month, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}
