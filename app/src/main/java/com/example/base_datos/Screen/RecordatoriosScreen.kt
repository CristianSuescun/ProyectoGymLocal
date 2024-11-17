package com.example.base_datos.Screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.base_datos.Model.Recordatorio
import com.example.base_datos.Repository.RecordatoriosRepository
import kotlinx.coroutines.launch
import java.util.*

/**
 * Pantalla principal para gestionar recordatorios de una rutina.
 * Permite ver, crear y editar recordatorios.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordatoriosScreen(
    recordatoriosRepository: RecordatoriosRepository,
    navController: NavController,
    rutinaId: Int
) {
    var recordatorios by remember { mutableStateOf<List<Recordatorio>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showFormDialog by remember { mutableStateOf(false) }
    var showRecords by remember { mutableStateOf(false) }
    var selectedRecordatorio by remember { mutableStateOf<Recordatorio?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Cargar los recordatorios para la rutina cuando cambia rutinaId
    LaunchedEffect(rutinaId) {
        recordatorios = recordatoriosRepository.getRecordatoriosByRutinaId(rutinaId) // Cargar recordatorios por rutinaId
        isLoading = false
    }

    // Diseño de la pantalla
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
        Spacer(modifier = Modifier.height(40.dp)) // Espacio adicional para evitar que el contenido se corte

        // Título de la pantalla
        Text(
            text = "Recordatorios",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color(0xFF0D47A1), // Color azul oscuro
                fontSize = 26.sp
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Indicador de carga mientras se obtienen los recordatorios
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.White)
        }

        // Botón para crear un nuevo recordatorio
        Button(
            onClick = { showFormDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)) // Azul
        ) {
            Text("Crear Nuevo Recordatorio", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para alternar la visibilidad de los registros de recordatorios
        Button(
            onClick = { showRecords = !showRecords },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)) // Azul
        ) {
            Text(if (showRecords) "Ocultar Registros" else "Ver Registros", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar los recordatorios si showRecords es verdadero
        if (showRecords) {
            if (!isLoading && recordatorios.isEmpty()) {
                Text(text = "No hay recordatorios disponibles", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(recordatorios) { recordatorio ->
                        RecordatorioItem(
                            recordatorio = recordatorio,
                            onDelete = {
                                coroutineScope.launch {
                                    recordatoriosRepository.delete(recordatorio)
                                    recordatorios = recordatoriosRepository.getRecordatoriosByRutinaId(rutinaId)
                                }
                            },
                            onEdit = {
                                selectedRecordatorio = recordatorio
                                showFormDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Mostrar el formulario de diálogo para crear o editar un recordatorio
    if (showFormDialog) {
        RecordatorioFormDialog(
            recordatorio = selectedRecordatorio,
            rutinaId = rutinaId,
            onDismiss = {
                showFormDialog = false
                selectedRecordatorio = null
            },
            onSave = { recordatorio ->
                coroutineScope.launch {
                    if (selectedRecordatorio != null) {
                        recordatoriosRepository.update(recordatorio)
                    } else {
                        recordatoriosRepository.insert(recordatorio)
                    }
                    recordatorios = recordatoriosRepository.getRecordatoriosByRutinaId(rutinaId)
                    showFormDialog = false
                    selectedRecordatorio = null
                }
            }
        )
    }
}

/**
 * Componente que muestra la información de un recordatorio individual, con opciones para eliminar o editar.
 */
@Composable
fun RecordatorioItem(
    recordatorio: Recordatorio,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        // Mostrar los detalles del recordatorio
        Text(text = "Mensaje: ${recordatorio.mensaje}", style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black))
        Text(text = "Fecha y Hora: ${recordatorio.fechaHora}", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))
        Text(text = "Repetir: ${if (recordatorio.repetir) "Sí" else "No"}", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))
        Text(text = "Frecuencia: ${recordatorio.frecuencia}", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para eliminar el recordatorio
        Button(
            onClick = onDelete,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)), // Rojo para eliminar
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Eliminar", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para editar el recordatorio
        Button(
            onClick = onEdit,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)), // Azul
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar", color = Color.White)
        }
    }
}

/**
 * Formulario de diálogo para crear o editar un recordatorio.
 * Incluye validación para asegurar que todos los campos estén llenos, excepto "Repetir".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordatorioFormDialog(
    recordatorio: Recordatorio?,
    rutinaId: Int,
    onDismiss: () -> Unit,
    onSave: (Recordatorio) -> Unit
) {
    var mensaje by remember { mutableStateOf(recordatorio?.mensaje ?: "") }
    var fechaHora by remember { mutableStateOf(recordatorio?.fechaHora ?: "") }
    var repetir by remember { mutableStateOf(recordatorio?.repetir ?: false) }
    var frecuencia by remember { mutableStateOf(recordatorio?.frecuencia ?: "diaria") }

    // Variables para gestionar la selección de fecha y hora
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Función para mostrar el DatePickerDialog
    fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                fechaHora = "$dayOfMonth/${month + 1}/$year $fechaHora"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Función para mostrar el TimePickerDialog
    fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val amPm = if (hourOfDay >= 12) "PM" else "AM"
                val hour12 = if (hourOfDay > 12) hourOfDay - 12 else if (hourOfDay == 0) 12 else hourOfDay
                fechaHora = "$fechaHora $hour12:$minute $amPm"
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // Formato 12 horas
        )
        timePickerDialog.show()
    }

    // Diálogo de alerta para crear o editar un recordatorio
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                // Validación de campos
                if (mensaje.isEmpty() || fechaHora.isEmpty() || frecuencia.isEmpty()) {
                    // Mostrar un mensaje si algún campo está vacío
                    Toast.makeText(context, "Todos los campos deben estar llenos, excepto 'Repetir'.", Toast.LENGTH_SHORT).show()
                } else {
                    val nuevoRecordatorio = Recordatorio(
                        id = recordatorio?.id,
                        rutinaId = rutinaId,
                        fechaHora = fechaHora,
                        mensaje = mensaje,
                        repetir = repetir,
                        frecuencia = frecuencia
                    )
                    onSave(nuevoRecordatorio)
                }
            }) {
                Text(if (recordatorio == null) "Guardar" else "Actualizar", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar", color = Color.White)
            }
        },
        title = { Text(if (recordatorio == null) "Crear Recordatorio" else "Editar Recordatorio", color = Color(0xFF0D47A1)) },
        text = {
            Column {
                OutlinedTextField(
                    value = mensaje,
                    onValueChange = { mensaje = it },
                    label = { Text("Mensaje del Recordatorio", color = Color(0xFF1565C0)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8F7F7)), // Fondo gris suave
                    textStyle = LocalTextStyle.current.copy(color = Color.Black) // Texto negro
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { showDatePicker() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5))) {
                    Text("Seleccionar Fecha", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { showTimePicker() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5))) {
                    Text("Seleccionar Hora", color = Color.White)
                }

                Text("Fecha y Hora seleccionada: $fechaHora", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = repetir,
                        onCheckedChange = { repetir = it }
                    )
                    Text(text = "¿Repetir?", modifier = Modifier.padding(start = 8.dp), color = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selección de frecuencia
                Text("Frecuencia", color = Color.Black)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = frecuencia == "diaria",
                        onClick = { frecuencia = "diaria" }
                    )
                    Text("Diaria", modifier = Modifier.padding(start = 8.dp), color = Color.Black)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = frecuencia == "mensual",
                        onClick = { frecuencia = "mensual" }
                    )
                    Text("Mensual", modifier = Modifier.padding(start = 8.dp), color = Color.Black)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = frecuencia == "anual",
                        onClick = { frecuencia = "anual" }
                    )
                    Text("Anual", modifier = Modifier.padding(start = 8.dp), color = Color.Black)
                }

                Text("Frecuencia seleccionada: $frecuencia", color = Color.Black)
            }
        },
        containerColor = Color(0xFFC3E9F3) // Fondo azul suave
    )
}
