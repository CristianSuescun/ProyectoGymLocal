package com.example.base_datos.Screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.base_datos.Model.Rutina
import com.example.base_datos.Model.Ejercicio
import com.example.base_datos.Model.RutinaEjercicio
import com.example.base_datos.Repository.RutinasRepository
import com.example.base_datos.Repository.EjerciciosRepository
import com.example.base_datos.Repository.RutinaEjercicioRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutinasEjerciciosScreen(
    rutinasRepository: RutinasRepository,
    ejerciciosRepository: EjerciciosRepository,
    rutinaEjercicioRepository: RutinaEjercicioRepository,
    navController: NavController,
    usuarioId: Int
) {
    var rutinas by remember { mutableStateOf<List<Rutina>>(emptyList()) }
    var ejercicios by remember { mutableStateOf<List<Ejercicio>>(emptyList()) }
    var selectedRutina by remember { mutableStateOf<Rutina?>(null) }
    var selectedEjercicio by remember { mutableStateOf<Ejercicio?>(null) }
    var series by remember { mutableStateOf(0) }
    var repeticiones by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var showRutinasList by remember { mutableStateOf(false) }
    var showEjercicioForm by remember { mutableStateOf(false) }
    var showRegistro by remember { mutableStateOf(false) } // Estado para mostrar el registro
    var rutinasEjerciciosList by remember { mutableStateOf<List<RutinaEjercicio>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()

    // Cargar rutinas, ejercicios y registros cuando la pantalla se carga
    LaunchedEffect(usuarioId) {
        rutinas = rutinasRepository.getRutinasByUsuarioId(usuarioId)
        ejercicios = ejerciciosRepository.getEjercicios(usuarioId)
        rutinasEjerciciosList = rutinaEjercicioRepository.getAll() // Obtener registros existentes
        isLoading = false
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Rutinas y Ejercicios", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            if (rutinas.isEmpty()) {
                Text("No hay rutinas disponibles", style = MaterialTheme.typography.bodyLarge)
            } else {
                // Un solo botón para ver rutinas y asignar ejercicio
                Button(
                    onClick = { showRutinasList = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seleccionar Rutina y Asignar Ejercicio")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para ver los registros de rutinas y ejercicios asignados
        Button(
            onClick = { showRegistro = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Registros de Rutinas y Ejercicios Asignados")
        }

        // Mostrar la lista de rutinas disponibles
        if (showRutinasList) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(rutinas) { rutina ->
                    RutinaItem(
                        rutina = rutina,
                        onSelect = {
                            selectedRutina = rutina
                            showRutinasList = false // Ocultar la lista de rutinas
                            showEjercicioForm = true // Mostrar el formulario de ejercicio
                        }
                    )
                }
            }
        }

        // Mostrar el formulario para seleccionar un ejercicio y asignar detalles (series, repeticiones)
        if (showEjercicioForm && selectedRutina != null) {
            EjercicioFormDialog(
                ejerciciosList = ejercicios,
                series = series,
                repeticiones = repeticiones,
                onSeriesChange = { series = it },
                onRepeticionesChange = { repeticiones = it },
                onDismiss = { showEjercicioForm = false },
                onSave = { ejercicio ->
                    val nuevaRelacion = RutinaEjercicio(
                        rutinaId = selectedRutina?.id ?: 0,
                        ejercicioId = ejercicio.id!!,
                        series = series,
                        repeticiones = repeticiones
                    )
                    coroutineScope.launch {
                        rutinaEjercicioRepository.insert(nuevaRelacion)
                        showEjercicioForm = false
                        rutinasEjerciciosList = rutinaEjercicioRepository.getAll() // Actualizar la lista de registros
                    }
                }
            )
        }

        // Mostrar el registro de rutinas y ejercicios asignados
        if (showRegistro) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(rutinasEjerciciosList) { relacion ->
                    val rutina = rutinas.find { it.id == relacion.rutinaId }
                    val ejercicio = ejercicios.find { it.id == relacion.ejercicioId }
                    if (rutina != null && ejercicio != null) {
                        Text("Rutina: ${rutina.nombre}, Ejercicio: ${ejercicio.nombre}, " +
                                "Series: ${relacion.series}, Repeticiones: ${relacion.repeticiones}",
                            style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun RutinaItem(
    rutina: Rutina,
    onSelect: () -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "Nombre: ${rutina.nombre}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Descripción: ${rutina.descripcion}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Día: ${rutina.dia}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "¿Completada? ${if (rutina.completado) "Sí" else "No"}", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSelect,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar esta rutina")
        }
    }
}

@Composable
fun EjercicioFormDialog(
    ejerciciosList: List<Ejercicio>,
    series: Int,
    repeticiones: Int,
    onSeriesChange: (Int) -> Unit,
    onRepeticionesChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    onSave: (Ejercicio) -> Unit
) {
    var selectedEjercicio by remember { mutableStateOf<Ejercicio?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (selectedEjercicio != null) {
                    onSave(selectedEjercicio!!)

                }
            }) {
                Text("Asignar Ejercicio")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Seleccionar Ejercicio y Asignar") },
        text = {
            Column {
                // Selección de ejercicio
                Text("Selecciona un ejercicio:")
                LazyColumn {
                    items(ejerciciosList) { ejercicio ->
                        Text(
                            text = ejercicio.nombre,
                            modifier = Modifier
                                .clickable {
                                    selectedEjercicio = ejercicio
                                }
                                .padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selección de series
                OutlinedTextField(
                    value = series.toString(),
                    onValueChange = { onSeriesChange(it.toIntOrNull() ?: 0) },
                    label = { Text("Series") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Selección de repeticiones
                OutlinedTextField(
                    value = repeticiones.toString(),
                    onValueChange = { onRepeticionesChange(it.toIntOrNull() ?: 0) },
                    label = { Text("Repeticiones") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
