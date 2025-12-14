package com.idat.presentation.gestion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var mostrarDialogoEditar by remember { mutableStateOf<Producto?>(null) }
    var mostrarDialogoEliminar by remember { mutableStateOf<Producto?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val backgroundColor = if (isDarkTheme) Color(0xFF1C1C1C) else Color(0xFFFAFAFA)
    val cardColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val textColor = if (isDarkTheme) Color(0xFFFAFAFA) else Color(0xFF222222)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gestión de Productos", color = textColor, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = cardColor
                    ),
                    navigationIcon = {
                        IconButton(onClick = { 
                            navController.navigate("catalogo?openDrawer=true") {
                                popUpTo("catalogo") { inclusive = true }
                            }
                        }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = textColor
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { mostrarDialogoCrear = true },
                    containerColor = Color(0xFFE50010),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color(0xFFFAFAFA)
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
                    placeholder = { Text("Buscar productos...", color = Color(0xFF666666)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color(0xFF222222))
                    },
                    trailingIcon = {
                        if (busqueda.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setBusqueda("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = Color(0xFF222222))
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF222222),
                        unfocusedTextColor = Color(0xFF222222),
                        focusedBorderColor = Color(0xFF222222),
                        unfocusedBorderColor = Color(0xFFCCCCCC),
                        cursorColor = Color(0xFF222222)
                    ),
                    shape = RoundedCornerShape(4.dp)
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
                                tint = Color(0xFFCCCCCC)
                            )
                            Text(
                                text = "No hay productos",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color(0xFF222222),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Presiona + para agregar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666)
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
                title = {
                    Text(
                        text = "Eliminar Producto",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF222222)
                    )
                },
                text = {
                    Text(
                        text = "¿Estás seguro de eliminar '${producto.nombre}'? Esta acción no se puede deshacer.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )
                },
                confirmButton = {
                    Button(
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
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE50010),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Eliminar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { mostrarDialogoEliminar = null },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF222222)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCCCCCC)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Cancelar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(0.dp)
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
                    color = Color(0xFF222222),
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
        title = {
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF222222)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Nombre
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Nombre *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF222222)
                    )
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF222222),
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        )
                    )
                }

                // Precio
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Precio *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF222222)
                    )
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF222222),
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        )
                    )
                }

                // Categoría
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Categoría",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF222222)
                    )
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = { categoria = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF222222),
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        )
                    )
                }

                // Descripción
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Descripción",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF222222)
                    )
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF222222),
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        )
                    )
                }

                // URL de Imagen
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "URL de Imagen",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF222222)
                    )
                    OutlinedTextField(
                        value = imagen,
                        onValueChange = { imagen = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF222222),
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        )
                    )
                }

                // Calificación y Cantidad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Calificación",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF222222)
                        )
                        OutlinedTextField(
                            value = calificacion,
                            onValueChange = { calificacion = it },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF222222),
                                unfocusedBorderColor = Color(0xFFCCCCCC)
                            )
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Reseñas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF222222)
                        )
                        OutlinedTextField(
                            value = cantidadCalificaciones,
                            onValueChange = { cantidadCalificaciones = it },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF222222),
                                unfocusedBorderColor = Color(0xFFCCCCCC)
                            )
                        )
                    }
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF222222),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Guardar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF222222)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCCCCCC)),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Cancelar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(0.dp)
    )
}
