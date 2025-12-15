package com.idat.presentation.gestion

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Productos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogoCrear = true },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { viewModel.setBusqueda(it) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                placeholder = { Text("Buscar productos...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    if (busqueda.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setBusqueda("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar")
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (productos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = "Sin productos",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                        Text(
                            text = "No hay productos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Presiona (+) para agregar el primero.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(productos, key = { it.id }) { producto ->
                        ProductoGestionItem(
                            producto = producto,
                            onEditar = { mostrarDialogoEditar = producto },
                            onEliminar = { mostrarDialogoEliminar = producto }
                        )
                    }
                }
            }
        }

        if (mostrarDialogoCrear) {
            ProductoFormDialog(
                titulo = "Nuevo Producto",
                producto = null,
                onDismiss = { mostrarDialogoCrear = false },
                onGuardar = { nombre, precio, descripcion, categoria, imagen, calificacion, cantidadCalif ->
                    viewModel.crearProducto(
                        nombre = nombre, precio = precio, descripcion = descripcion, categoria = categoria,
                        imagen = imagen, calificacion = calificacion, cantidadCalificaciones = cantidadCalif,
                        onSuccess = {
                            mostrarDialogoCrear = false
                            scope.launch { snackbarHostState.showSnackbar("Producto creado exitosamente") }
                        },
                        onError = { error -> scope.launch { snackbarHostState.showSnackbar(error) } }
                    )
                }
            )
        }

        mostrarDialogoEditar?.let { producto ->
            ProductoFormDialog(
                titulo = "Editar Producto",
                producto = producto,
                onDismiss = { mostrarDialogoEditar = null },
                onGuardar = { nombre, precio, descripcion, categoria, imagen, calificacion, cantidadCalif ->
                    val productoActualizado = producto.copy(
                        nombre = nombre, precio = precio, descripcion = descripcion, categoria = categoria,
                        imagen = imagen, calificacion = calificacion, cantidadCalificaciones = cantidadCalif
                    )
                    viewModel.actualizarProducto(
                        producto = productoActualizado,
                        onSuccess = {
                            mostrarDialogoEditar = null
                            scope.launch { snackbarHostState.showSnackbar("Producto actualizado") }
                        },
                        onError = { error -> scope.launch { snackbarHostState.showSnackbar(error) } }
                    )
                }
            )
        }

        mostrarDialogoEliminar?.let { producto ->
            AlertDialog(
                onDismissRequest = { mostrarDialogoEliminar = null },
                title = { Text("Eliminar Producto") },
                text = { Text("¿Estás seguro de eliminar '${producto.nombre}'? Esta acción no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.eliminarProducto(
                                productoId = producto.id,
                                onSuccess = {
                                    mostrarDialogoEliminar = null
                                    scope.launch { snackbarHostState.showSnackbar("Producto eliminado") }
                                },
                                onError = { error -> scope.launch { snackbarHostState.showSnackbar(error) } }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar")
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(shape = RoundedCornerShape(8.dp)){
                 Image(
                    painter = rememberAsyncImagePainter(model = producto.imagen),
                    contentDescription = producto.nombre,
                    modifier = Modifier.size(80.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "S/ ${producto.precio}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "ID: ${producto.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
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
        title = { Text(titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Los OutlinedTextField por defecto ya se adaptan al tema
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, modifier = Modifier.fillMaxWidth(), label = {Text("Nombre*")}, singleLine = true)
                OutlinedTextField(value = precio, onValueChange = { precio = it }, modifier = Modifier.fillMaxWidth(), label = {Text("Precio*")}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = categoria, onValueChange = { categoria = it }, modifier = Modifier.fillMaxWidth(), label = {Text("Categoría")}, singleLine = true)
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, modifier = Modifier.fillMaxWidth(), label = {Text("Descripción")}, maxLines = 3)
                OutlinedTextField(value = imagen, onValueChange = { imagen = it }, modifier = Modifier.fillMaxWidth(), label = {Text("URL de Imagen")}, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = calificacion, onValueChange = { calificacion = it }, modifier = Modifier.weight(1f), label = {Text("Calificación")}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                    OutlinedTextField(value = cantidadCalificaciones, onValueChange = { cantidadCalificaciones = it }, modifier = Modifier.weight(1f), label = {Text("Reseñas")}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val precioDouble = precio.toDoubleOrNull() ?: 0.0
                    val calificacionDouble = calificacion.toDoubleOrNull() ?: 0.0
                    val cantidadInt = cantidadCalificaciones.toIntOrNull() ?: 0
                    onGuardar(nombre, precioDouble, descripcion, categoria, imagen, calificacionDouble, cantidadInt)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar")
            }
        }
    )
}
