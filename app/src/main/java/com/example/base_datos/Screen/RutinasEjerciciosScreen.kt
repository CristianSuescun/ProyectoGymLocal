package com.example.base_datos.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.base_datos.Model.Rutina
import com.example.base_datos.Model.Ejercicio
import com.example.base_datos.Model.RutinaEjercicio
import com.example.base_datos.Repository.RutinasRepository
import com.example.base_datos.Repository.EjerciciosRepository
import com.example.base_datos.Repository.RutinaEjercicioRepository
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign

/**
 * Pantalla principal para gestionar rutinas y ejercicios de un usuario.
 *
 * @param rutinasRepository Repositorio que maneja las rutinas.
 * @param ejerciciosRepository Repositorio que maneja los ejercicios.
 * @param rutinaEjercicioRepository Repositorio que maneja las relaciones entre rutinas y ejercicios.
 * @param navController Controlador de navegación para moverse entre pantallas.
 * @param usuarioId ID del usuario actual.
 */
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
    var showRegistro by remember { mutableStateOf(false) }
    var rutinasEjerciciosList by remember { mutableStateOf<List<RutinaEjercicio>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()

    // Cargar rutinas, ejercicios y registros cuando la pantalla se carga
    LaunchedEffect(usuarioId) {
        rutinas = rutinasRepository.getRutinasByUsuarioId(usuarioId)
        ejercicios = ejerciciosRepository.getEjercicios(usuarioId)
        rutinasEjerciciosList = rutinaEjercicioRepository.getAll()
        isLoading = false
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF64B5F6),
                    Color.White // Blanco
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título de la pantalla
            Text(
                "Rutinas y Ejercicios",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp), // Baja el título
                textAlign = TextAlign.Center // Centrado del título
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar un indicador de carga si los datos están siendo cargados
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                if (rutinas.isEmpty()) {
                    Text("No hay rutinas disponibles", style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black))
                } else {
                    // Botón para ver las rutinas y asignar ejercicios
                    Button(
                        onClick = { showRutinasList = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF648CAF),
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Seleccionar Rutina y Asignar Ejercicio")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para ver/ocultar los registros de rutinas y ejercicios asignados
            Button(
                onClick = {
                    showRegistro = !showRegistro
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0288D1),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Ver/OCULTAR Registros de Rutinas y Ejercicios Asignados")
            }

            // Mostrar la lista de rutinas disponibles
            if (showRutinasList) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(rutinas) { rutina ->
                        RutinaItem(
                            rutina = rutina,
                            isSelected = selectedRutina == rutina,
                            onSelect = {
                                selectedRutina = rutina
                                showRutinasList = false
                                showEjercicioForm = true
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
                            rutinasEjerciciosList = rutinaEjercicioRepository.getAll()
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
                            RegistroItem(
                                rutina = rutina,
                                ejercicio = ejercicio,
                                relacion = relacion
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Componente que muestra la información de una rutina.
 *
 * @param rutina Rutina a mostrar.
 * @param isSelected Indica si esta rutina está seleccionada.
 * @param onSelect Función que se ejecuta al seleccionar la rutina.
 */
@Composable
fun RutinaItem(
    rutina: Rutina,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFBBDEFB) else Color.Transparent

    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(backgroundColor, shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onSelect)
            .padding(16.dp)
    ) {
        Text(text = "Nombre: ${rutina.nombre}", style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black))
        Text(text = "Descripción: ${rutina.descripcion}", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))
        Text(text = "Día: ${rutina.dia}", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))
        Text(text = "¿Completada? ${if (rutina.completado) "Sí" else "No"}", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSelect,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0288D1),
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Seleccionar esta rutina")
        }
    }
}

/**
 * Diálogo para seleccionar un ejercicio y asignar detalles como series y repeticiones.
 *
 * @param ejerciciosList Lista de ejercicios disponibles para seleccionar.
 * @param series Número de series.
 * @param repeticiones Número de repeticiones.
 * @param onSeriesChange Función que se ejecuta cuando el número de series cambia.
 * @param onRepeticionesChange Función que se ejecuta cuando el número de repeticiones cambia.
 * @param onDismiss Función que se ejecuta al cerrar el diálogo.
 * @param onSave Función que se ejecuta al guardar la relación.
 */
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
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFF3F3F3),
        confirmButton = {
            Button(
                onClick = {
                    // Verificar si los campos están llenos antes de guardar
                    if (selectedEjercicio != null && series > 0 && repeticiones > 0) {
                        onSave(selectedEjercicio!!)
                        showError = false
                    } else {
                        showError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Asignar Ejercicio")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0288D1),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Cancelar")
            }
        },
        title = { Text("Seleccionar Ejercicio y Asignar", color = Color.Black) },
        text = {
            Column {
                Text("Selecciona un ejercicio:", color = Color.Black)
                LazyColumn {
                    items(ejerciciosList) { ejercicio ->
                        Text(
                            text = ejercicio.nombre,
                            modifier = Modifier
                                .clickable {
                                    selectedEjercicio = ejercicio
                                }
                                .padding(8.dp)
                                .background(Color(0xFF0288D1).copy(alpha = 0.1f), MaterialTheme.shapes.small),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Black,
                                fontWeight = if (selectedEjercicio == ejercicio) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = series.toString(),
                    onValueChange = { newText -> newText.toIntOrNull()?.let { onSeriesChange(it) } },
                    label = { Text("Series", color = Color.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = repeticiones.toString(),
                    onValueChange = { newText -> newText.toIntOrNull()?.let { onRepeticionesChange(it) } },
                    label = { Text("Repeticiones", color = Color.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                // Mostrar un mensaje de error si algún campo no está lleno
                if (showError) {
                    Text(
                        "Por favor, completa todos los campos.",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    )
}

/**
 * Componente que muestra un registro de una rutina con su ejercicio asociado.
 *
 * @param rutina Rutina asociada al ejercicio.
 * @param ejercicio Ejercicio asociado a la rutina.
 * @param relacion Relación entre la rutina y el ejercicio.
 */
@Composable
fun RegistroItem(
    rutina: Rutina,
    ejercicio: Ejercicio,
    relacion: RutinaEjercicio
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0288D1).copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Rutina: ${rutina.nombre}", style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black))
            Text("Ejercicio: ${ejercicio.nombre}", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))
            Text("Series: ${relacion.series}", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))
            Text("Repeticiones: ${relacion.repeticiones}", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black))
        }
    }
}
