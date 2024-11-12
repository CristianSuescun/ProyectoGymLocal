package com.example.base_datos.Screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.base_datos.Model.Recordatorio
import com.example.base_datos.Repository.RecordatoriosRepository
import kotlinx.coroutines.launch
import java.util.*
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
    var selectedRecordatorio by remember { mutableStateOf<Recordatorio?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Cargar los recordatorios para la rutina cuando cambia rutinaId
    LaunchedEffect(rutinaId) {
        recordatorios = recordatoriosRepository.getRecordatoriosByRutinaId(rutinaId) // Cargar recordatorios por rutinaId
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Recordatorios", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // Botón para crear un nuevo recordatorio, visible incluso si no hay recordatorios
        Button(
            onClick = { showFormDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear Nuevo Recordatorio")
        }

        Spacer(modifier = Modifier.height(16.dp))

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

    // Mostrar el formulario de creación/edición cuando showFormDialog es verdadero
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

@Composable
fun RecordatorioItem(
    recordatorio: Recordatorio,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "Mensaje: ${recordatorio.mensaje}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Fecha y Hora: ${recordatorio.fechaHora}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Repetir: ${if (recordatorio.repetir) "Sí" else "No"}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Frecuencia: ${recordatorio.frecuencia}", style = MaterialTheme.typography.bodyMedium)

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

    // Estado para controlar si el DatePicker y TimePicker están visibles
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }
    var selectedTime by remember { mutableStateOf<Calendar?>(null) }

    // Formato de fecha y hora
    val dateFormatter = remember { android.text.format.DateFormat.getDateFormat(context) }
    val timeFormatter = remember { android.text.format.DateFormat.getTimeFormat(context) }

    // Función para mostrar el DatePicker
    fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar
                fechaHora = "${dateFormatter.format(calendar.time)} ${timeFormatter.format(calendar.time)}"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Función para mostrar el TimePicker
    fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                selectedTime = calendar
                fechaHora = "${dateFormatter.format(calendar.time)} ${timeFormatter.format(calendar.time)}"
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val nuevoRecordatorio = Recordatorio(
                    id = recordatorio?.id,
                    rutinaId = rutinaId,
                    fechaHora = fechaHora,
                    mensaje = mensaje,
                    repetir = repetir,
                    frecuencia = frecuencia
                )
                onSave(nuevoRecordatorio)
            }) {
                Text(if (recordatorio == null) "Guardar" else "Actualizar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text(if (recordatorio == null) "Crear Recordatorio" else "Editar Recordatorio") },
        text = {
            Column {
                OutlinedTextField(
                    value = mensaje,
                    onValueChange = { mensaje = it },
                    label = { Text("Mensaje del Recordatorio") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Botón para seleccionar la fecha
                Button(onClick = { showDatePickerDialog() }) {
                    Text("Seleccionar Fecha")
                }

                // Botón para seleccionar la hora
                Button(onClick = { showTimePickerDialog() }) {
                    Text("Seleccionar Hora")
                }

                // Mostrar la fecha y hora seleccionada
                Text("Fecha y Hora seleccionada: $fechaHora", style = MaterialTheme.typography.bodyMedium)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = repetir,
                        onCheckedChange = { repetir = it }
                    )
                    Text(text = "¿Repetir?", modifier = Modifier.padding(start = 8.dp))
                }

                // Selección de frecuencia usando RadioButtons
                Text("Frecuencia")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = frecuencia == "diaria",
                        onClick = { frecuencia = "diaria" }
                    )
                    Text("Diaria", modifier = Modifier.padding(start = 8.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = frecuencia == "mensual",
                        onClick = { frecuencia = "mensual" }
                    )
                    Text("Mensual", modifier = Modifier.padding(start = 8.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = frecuencia == "anual",
                        onClick = { frecuencia = "anual" }
                    )
                    Text("Anual", modifier = Modifier.padding(start = 8.dp))
                }

                Text("Frecuencia seleccionada: $frecuencia")
            }
        }
    )
}

