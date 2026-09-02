package com.example.implementacioncrud

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.implementacioncrud.ui.theme.ImplementacionCrudTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImplementacionCrudTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EquipoApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var equipos by remember { mutableStateOf(listOf<Equipo>()) }
    var isLoading by remember { mutableStateOf(false) }
    
    var nombre by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("") }
    var editingEquipo by remember { mutableStateOf<Equipo?>(null) }

    fun fetchEquipos() {
        scope.launch {
            isLoading = true
            try {
                equipos = RetrofitClient.instance.getEquipos()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al cargar: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchEquipos()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("CRUD Equipos de Fútbol") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Formulario
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Equipo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = pais,
                onValueChange = { pais = it },
                label = { Text("País") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    if (nombre.isNotBlank() && pais.isNotBlank()) {
                        scope.launch {
                            try {
                                if (editingEquipo == null) {
                                    // Crear
                                    RetrofitClient.instance.createEquipo(Equipo(nombre = nombre, pais = pais))
                                    Toast.makeText(context, "Equipo creado", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Actualizar
                                    RetrofitClient.instance.updateEquipo(
                                        editingEquipo!!.id!!,
                                        Equipo(nombre = nombre, pais = pais)
                                    )
                                    Toast.makeText(context, "Equipo actualizado", Toast.LENGTH_SHORT).show()
                                    editingEquipo = null
                                }
                                nombre = ""
                                pais = ""
                                fetchEquipos()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (editingEquipo == null) "Registrar Equipo" else "Actualizar Equipo")
            }

            if (editingEquipo != null) {
                TextButton(
                    onClick = {
                        editingEquipo = null
                        nombre = ""
                        pais = ""
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Cancelar Edición")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(equipos) { equipo ->
                        EquipoItem(
                            equipo = equipo,
                            onEdit = {
                                editingEquipo = equipo
                                nombre = equipo.nombre ?: ""
                                pais = equipo.pais ?: ""
                            },
                            onDelete = {
                                scope.launch {
                                    try {
                                        RetrofitClient.instance.deleteEquipo(equipo.id!!)
                                        Toast.makeText(context, "Equipo eliminado", Toast.LENGTH_SHORT).show()
                                        fetchEquipos()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EquipoItem(equipo: Equipo, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = equipo.nombre ?: "Sin nombre", style = MaterialTheme.typography.titleMedium)
                Text(text = equipo.pais ?: "Sin país", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}
