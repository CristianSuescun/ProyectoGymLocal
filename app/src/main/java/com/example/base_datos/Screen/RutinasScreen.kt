package com.example.base_datos.Screen

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    // Variables de estado para manejar los datos y la UI
    var rutinas by remember { mutableStateOf<List<Rutina>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showFormDialog by remember { mutableStateOf(false) }
    var showRutinasSheet by remember { mutableStateOf(false) }
    var selectedRutina by remember { mutableStateOf<Rutina?>(null) }
    var errorMessage by remember { mutableStateOf("") } // Variable para el mensaje de error
    val coroutineScope = rememberCoroutineScope()

    // Carga las rutinas para un usuario específico
    LaunchedEffect(usuarioId) {
        rutinas = rutinasRepository.getRutinasByUsuarioId(usuarioId) // Cargar rutinas por usuarioId
        isLoading = false
    }

    // Interfaz principal que contiene botones y manejo de estado
    Column(
        modifier = Modifier
            .fillMaxSize() // Asegura que la columna ocupe todo el tamaño disponible
            .background(Color(0xFFEBF8FF))  // Fondo entre azul claro y blanco
            .padding(20.dp),  // Ajuste de padding para espacio alrededor
        verticalArrangement = Arrangement.Top  // Asegura que los elementos se alineen desde la parte superior
    ) {
        // Título con espacio superior
        Text(
            text = "Rutinas",
            style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFF1E3A8A)),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 40.dp, bottom = 16.dp) // Ajusta el top para bajar el título
        )

        // Si está cargando, mostrar un indicador de progreso
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // Si no hay rutinas, mostrar mensaje
            if (rutinas.isEmpty()) {
                Text(
                    text = "No hay rutinas disponibles",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                // Botón para mostrar las rutinas
                Button(
                    onClick = { showRutinasSheet = true },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally) // Centra el botón
                        .padding(bottom = 16.dp),  // Aumenta el padding inferior
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    Text("Ver Rutinas", color = Color.White)
                }
            }
        }

        // Botón para abrir el formulario de creación de una nueva rutina
        Button(
            onClick = { showFormDialog = true },
            modifier = Modifier
                .align(Alignment.CenterHorizontally) // Centra el botón
                .padding(bottom = 16.dp),  // Aumenta el padding inferior
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Text("Crear Nueva Rutina", color = Color.White)
        }

        // Botón para navegar a la pantalla de selección de ejercicios
        Button(
            onClick = {
                navController.navigate("RutinasEjerciciosScreen/$usuarioId")
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally) // Centra el botón
                .padding(bottom = 16.dp),  // Aumenta el padding inferior
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Text("Seleccionar Ejercicio para la Rutina", color = Color.White)
        }

        // Mostrar mensaje de error si existe
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Red),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)  // Agrega padding para mayor claridad
            )
        }
    }

    // Dialogo para agregar o editar una rutina
    if (showFormDialog) {
        RutinaFormDialog(
            rutina = selectedRutina,
            usuarioId = usuarioId,
            onDismiss = {
                showFormDialog = false
                selectedRutina = null
                errorMessage = "" // Limpiar el mensaje de error al cerrar el formulario
            },
            onSave = { rutina ->
                coroutineScope.launch {
                    // Validación de campos vacíos antes de guardar
                    if (rutina.nombre.isEmpty() || rutina.descripcion.isNullOrEmpty() || rutina.dia.isEmpty() || rutina.dia == "Seleccionar Fecha") {
                        errorMessage = "Por favor, ingresa todos los campos requeridos."  // Mensaje de error más claro
                    } else {
                        // Guardar o actualizar la rutina
                        if (selectedRutina != null) {
                            rutinasRepository.update(rutina)
                        } else {
                            rutinasRepository.insert(rutina)
                        }
                        rutinas = rutinasRepository.getRutinasByUsuarioId(usuarioId)
                        showFormDialog = false
                        selectedRutina = null
                        errorMessage = "" // Limpiar mensaje de error si la rutina se guarda correctamente
                    }
                }
            }
        )
    }

    // Mostrar las rutinas en una hoja inferior modal
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
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Nombre: ${rutina.nombre}", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFD6D8DE)))
        Text(text = "Descripción: ${rutina.descripcion}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Día: ${rutina.dia}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "¿Completada? ${if (rutina.completado) "Sí" else "No"}", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onDelete,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Eliminar", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
        ) {
            Text("Editar", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            Button(
                onClick = {
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
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
            ) {
                Text(if (rutina == null) "Guardar" else "Actualizar", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = {
            Text(
                if (rutina == null) "Crear Rutina" else "Editar Rutina",
                style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFFD1D3D5))
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la Rutina") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color(0xFFB3B3B3)
                    )
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color(0xFFB3B3B3)
                    )
                )
                Button(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text(dia, color = Color.White)
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

// Función para crear el diálogo del selector de fechas
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
