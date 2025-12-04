package com.idat.presentation.gestion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.idat.domain.model.Producto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionProductosScreen(
    navController: NavHostController,
    viewModel: GestionProductosViewModel = hiltViewModel()
) {
    val productos by viewModel.productos.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()
    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var mostrarDialogoEditar by remember { mutableStateOf<Producto?>(null) }
    var mostrarDialogoEliminar by remember { mutableStateOf<Producto?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7F00FF).copy(alpha = 0.4f),
                        Color(0xFFE100FF).copy(alpha = 0.35f),
                        Color(0xFF00C6FF).copy(alpha = 0.35f)
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gestión de Productos", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { mostrarDialogoCrear = true },
                    containerColor = Color(0xFFE100FF),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Barra de búsqueda
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { viewModel.setBusqueda(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar productos...", color = Color.White.copy(alpha = 0.7f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
                    },
                    trailingIcon = {
                        if (busqueda.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setBusqueda("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = Color.White)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de productos
                if (productos.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Sin productos",
                                modifier = Modifier.size(100.dp),
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "No hay productos",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Presiona + para agregar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(productos) { producto ->
                            ProductoGestionItem(
                                producto = producto,
                                onEditar = { mostrarDialogoEditar = producto },
                                onEliminar = { mostrarDialogoEliminar = producto }
                            )
                        }
                    }
                }
            }
        }

        // Diálogo Crear Producto
        if (mostrarDialogoCrear) {
            ProductoFormDialog(
                titulo = "Nuevo Producto",
                producto = null,
                onDismiss = { mostrarDialogoCrear = false },
                onGuardar = { nombre, precio, descripcion, categoria, imagen, calificacion, cantidadCalif ->
                    viewModel.crearProducto(
                        nombre = nombre,
                        precio = precio,
                        descripcion = descripcion,
                        categoria = categoria,
                        imagen = imagen,
                        calificacion = calificacion,
                        cantidadCalificaciones = cantidadCalif,
                        onSuccess = {
                            mostrarDialogoCrear = false
                            scope.launch {
                                snackbarHostState.showSnackbar("Producto creado exitosamente")
                            }
                        },
                        onError = { error ->
                            scope.launch {
                                snackbarHostState.showSnackbar(error)
                            }
                        }
                    )
                }
            )
        }

        // Diálogo Editar Producto
        mostrarDialogoEditar?.let { producto ->
            ProductoFormDialog(
                titulo = "Editar Producto",
                producto = producto,
                onDismiss = { mostrarDialogoEditar = null },
                onGuardar = { nombre, precio, descripcion, categoria, imagen, calificacion, cantidadCalif ->
                    val productoActualizado = producto.copy(
                        nombre = nombre,
                        precio = precio,
                        descripcion = descripcion,
                        categoria = categoria,
                        imagen = imagen,
                        calificacion = calificacion,
                        cantidadCalificaciones = cantidadCalif
                    )
                    viewModel.actualizarProducto(
                        producto = productoActualizado,
                        onSuccess = {
                            mostrarDialogoEditar = null
                            scope.launch {
                                snackbarHostState.showSnackbar("Producto actualizado")
                            }
                        },
                        onError = { error ->
                            scope.launch {
                                snackbarHostState.showSnackbar(error)
                            }
                        }
                    )
                }
            )
        }

        // Diálogo Eliminar Producto
        mostrarDialogoEliminar?.let { producto ->
            AlertDialog(
                onDismissRequest = { mostrarDialogoEliminar = null },
                title = { Text("Eliminar Producto") },
                text = { Text("¿Estás seguro de eliminar '${producto.nombre}'?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.eliminarProducto(
                                productoId = producto.id,
                                onSuccess = {
                                    mostrarDialogoEliminar = null
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Producto eliminado")
                                    }
                                },
                                onError = { error ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(error)
                                    }
                                }
                            )
                        }
                    ) {
                        Text("Eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoEliminar = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun ProductoGestionItem(
    producto: Producto,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen
            Image(
                painter = rememberAsyncImagePainter(model = producto.imagen),
                contentDescription = producto.nombre,
                modifier = Modifier.size(80.dp)
            )

            // Información
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "S/ ${producto.precio}",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFE100FF),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "ID: ${producto.id} | ${producto.categoria}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Botones de acción
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onEditar,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF00C6FF)
                    )
                }
                IconButton(
                    onClick = onEliminar,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun ProductoFormDialog(
    titulo: String,
    producto: Producto?,
    onDismiss: () -> Unit,
    onGuardar: (String, Double, String, String, String, Double, Int) -> Unit
) {
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var precio by remember { mutableStateOf(producto?.precio?.toString() ?: "") }
    var descripcion by remember { mutableStateOf(producto?.descripcion ?: "") }
    var categoria by remember { mutableStateOf(producto?.categoria ?: "") }
    var imagen by remember { mutableStateOf(producto?.imagen ?: "") }
    var calificacion by remember { mutableStateOf(producto?.calificacion?.toString() ?: "0.0") }
    var cantidadCalificaciones by remember { mutableStateOf(producto?.cantidadCalificaciones?.toString() ?: "0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titulo) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = imagen,
                    onValueChange = { imagen = it },
                    label = { Text("URL de Imagen") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = calificacion,
                        onValueChange = { calificacion = it },
                        label = { Text("Calificación (0-5)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = cantidadCalificaciones,
                        onValueChange = { cantidadCalificaciones = it },
                        label = { Text("# Calificaciones") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val precioDouble = precio.toDoubleOrNull() ?: 0.0
                    val calificacionDouble = calificacion.toDoubleOrNull() ?: 0.0
                    val cantidadInt = cantidadCalificaciones.toIntOrNull() ?: 0
                    onGuardar(nombre, precioDouble, descripcion, categoria, imagen, calificacionDouble, cantidadInt)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
