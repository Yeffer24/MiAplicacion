package com.idat.presentation.catalogo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.idat.domain.model.Producto
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    navController: NavHostController,
    viewModel: CatalogoViewModel = hiltViewModel(),
    openDrawer: Boolean = false
) {
    val productos by viewModel.productos.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val categoriaSeleccionada by viewModel.categoriaSeleccionada.collectAsState()
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarProductos()
    }
    
    LaunchedEffect(openDrawer) {
        if (openDrawer) {
            drawerState.open()
        }
    }

    val backgroundColor = if (isDarkTheme) Color(0xFF1C1C1C) else Color(0xFFFAFAFA)
    val cardColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val textPrimaryColor = if (isDarkTheme) Color(0xFFFAFAFA) else Color(0xFF222222)
    val textSecondaryColor = if (isDarkTheme) Color(0xFFCCCCCC) else Color(0xFF666666)
    val dividerColor = if (isDarkTheme) Color(0xFF404040) else Color(0xFFEEEEEE)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerPerfil(
                    viewModel = viewModel,
                    onCerrarSesion = {
                        scope.launch {
                            drawerState.close()
                        }
                        mostrarDialogoCerrarSesion = true
                    },
                    onClose = {
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    onNavigateToFavoritos = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate("favoritos/fromDrawer")
                    },
                    onNavigateToPersonalizacion = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate("personalizacion/fromDrawer")
                    },
                    onNavigateToConfiguracion = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate("configuracion/fromDrawer")
                    },
                    onNavigateToGestion = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate("gestion/fromDrawer")
                    },
                    onNavigateToAyuda = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate("ayuda/fromDrawer")
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Catálogo", color = textPrimaryColor, fontWeight = FontWeight.Bold) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = cardColor
                        ),
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Menú",
                                    tint = textPrimaryColor
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate("carrito") }) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Bolsa",
                                    tint = textPrimaryColor
                                )
                            }
                        }
                    )
                },
                containerColor = backgroundColor
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Barra de búsqueda
                    OutlinedTextField(
                        value = textoBusqueda,
                        onValueChange = { viewModel.actualizarBusqueda(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Buscar productos...", color = textSecondaryColor) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = textSecondaryColor
                            )
                        },
                        trailingIcon = {
                            if (textoBusqueda.isNotEmpty()) {
                                IconButton(onClick = { viewModel.actualizarBusqueda("") }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Limpiar",
                                        tint = textSecondaryColor
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(0.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF222222),
                            unfocusedBorderColor = Color(0xFFCCCCCC),
                            focusedTextColor = textPrimaryColor,
                            unfocusedTextColor = textPrimaryColor,
                            cursorColor = Color(0xFFE50010)
                        )
                    )
                    
                    // Categorías horizontales
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(categorias.size) { index ->
                            val categoria = categorias[index]
                            FilterChip(
                                selected = categoriaSeleccionada == categoria,
                                onClick = { viewModel.seleccionarCategoria(categoria) },
                                label = { 
                                    Text(
                                        categoria,
                                        fontWeight = if (categoriaSeleccionada == categoria) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF222222),
                                    selectedLabelColor = Color.White,
                                    containerColor = if (isDarkTheme) cardColor else Color.White,
                                    labelColor = textPrimaryColor
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = categoriaSeleccionada == categoria,
                                    borderColor = Color(0xFFCCCCCC),
                                    selectedBorderColor = Color(0xFF222222),
                                    disabledBorderColor = Color(0xFFCCCCCC),
                                    disabledSelectedBorderColor = Color(0xFF222222),
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 1.dp
                                ),
                                shape = RoundedCornerShape(0.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Lista de productos
                    if (productos.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color(0xFFCCCCCC)
                                )
                                Text(
                                    "No se encontraron productos",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = textSecondaryColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else if (viewMode == "grid") {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(productos) { producto ->
                                ProductoGridItem(
                                    producto = producto,
                                    onAgregarAlCarrito = { viewModel.agregarAlCarrito(producto) },
                                    onClick = { navController.navigate("detalle/${producto.id}") },
                                    onToggleFavorito = { viewModel.toggleFavorito(producto) },
                                    viewModel = viewModel
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(productos) { producto ->
                                ProductoCard(
                                    producto = producto,
                                    onAgregarAlCarrito = { viewModel.agregarAlCarrito(producto) },
                                    onClick = { navController.navigate("detalle/${producto.id}") }
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Diálogo de Confirmación para Cerrar Sesión
        if (mostrarDialogoCerrarSesion) {
            DialogoConfirmacionCerrarSesion(
                onConfirmar = {
                    mostrarDialogoCerrarSesion = false
                    viewModel.cerrarSesion()
                    navController.navigate("login") {
                        popUpTo("catalogo") { inclusive = true }
                    }
                },
                onCancelar = { mostrarDialogoCerrarSesion = false }
            )
        }
    }
}

@Composable
fun DrawerPerfil(
    viewModel: CatalogoViewModel,
    onCerrarSesion: () -> Unit,
    onClose: () -> Unit,
    onNavigateToFavoritos: () -> Unit,
    onNavigateToPersonalizacion: () -> Unit,
    onNavigateToConfiguracion: () -> Unit,
    onNavigateToGestion: () -> Unit,
    onNavigateToAyuda: () -> Unit
) {
    val usuarioEmail = viewModel.obtenerEmailUsuario() ?: "usuario@ejemplo.com"
    val usuarioNombre = usuarioEmail.substringBefore("@")
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val drawerBgColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val textColor = if (isDarkTheme) Color(0xFFFAFAFA) else Color(0xFF222222)
    val textSecondaryColor = if (isDarkTheme) Color(0xFFCCCCCC) else Color(0xFF666666)
    val dividerColor = if (isDarkTheme) Color(0xFF404040) else Color(0xFFEEEEEE)
    
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = drawerBgColor
    ) {
        // Header con avatar y nombre
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(drawerBgColor)
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Avatar circular
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFE50010), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = usuarioNombre.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Nombre
            Text(
                text = usuarioNombre.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            
            // Email
            Text(
                text = usuarioEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondaryColor
            )
        }
        
        Divider(color = dividerColor)
        
        // Opciones del menú
        DrawerMenuItem(
            icon = Icons.Default.Favorite,
            text = "Favoritos",
            onClick = onNavigateToFavoritos
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Person,
            text = "Personalización",
            onClick = {
                onClose()
                onNavigateToPersonalizacion()
            }
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Settings,
            text = "Configuración",
            onClick = {
                onClose()
                onNavigateToConfiguracion()
            }
        )
        
        DrawerMenuItem(
            icon = Icons.Default.ShoppingCart,
            text = "Gestión de Productos",
            onClick = {
                onClose()
                onNavigateToGestion()
            }
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Help,
            text = "Ayuda",
            onClick = {
                onClose()
                onNavigateToAyuda()
            },
            showArrow = true
        )
        
        Divider(
            color = dividerColor,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        // Cerrar sesión
        DrawerMenuItem(
            icon = Icons.Default.Logout,
            text = "Cerrar sesión",
            onClick = onCerrarSesion,
            textColor = Color(0xFFE50010)
        )
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    textColor: Color = Color(0xFF222222),
    showArrow: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            
            if (showArrow) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Ir",
                    tint = Color(0xFF999999),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun DialogoPerfil(
    onDismiss: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: CatalogoViewModel
) {
    val usuarioEmail = viewModel.obtenerEmailUsuario()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Person,
                contentDescription = "Perfil",
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF7F00FF)
            )
        },
        title = {
            Text(
                text = "Perfil de Usuario",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Email del usuario
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Email: ",
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = usuarioEmail ?: "No disponible",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                Text(
                    text = "Estado: Activo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF00C853)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onCerrarSesion,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE100FF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = "Cerrar Sesión",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun DialogoConfirmacionCerrarSesion(
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            Text(
                text = "Cerrar Sesión",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF222222)
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que deseas cerrar sesión?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF222222),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Cerrar Sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onCancelar,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF222222)
                ),
                border = BorderStroke(1.dp, Color(0xFFCCCCCC)),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoGridItem(
    producto: Producto,
    onAgregarAlCarrito: () -> Unit,
    onClick: () -> Unit,
    onToggleFavorito: () -> Unit,
    viewModel: CatalogoViewModel
) {
    var esFavorito by remember { mutableStateOf(false) }
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val cardColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val textColor = if (isDarkTheme) Color(0xFFFAFAFA) else Color(0xFF222222)

    LaunchedEffect(producto.id) {
        esFavorito = viewModel.esFavorito(producto.id)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen con corazón de favorito
            Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(model = producto.imagen),
                    contentDescription = producto.nombre,
                    modifier = Modifier.fillMaxSize()
                )
                // Icono de favorito en la esquina superior derecha
                IconButton(
                    onClick = {
                        onToggleFavorito()
                        esFavorito = !esFavorito
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorito",
                        tint = if (esFavorito) Color(0xFFE50010) else Color(0xFFCCCCCC),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Información del producto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Nombre del producto
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Precio
                Text(
                    text = "S/ ${producto.precio}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onAgregarAlCarrito: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Imagen
            Image(
                painter = rememberAsyncImagePainter(model = producto.imagen),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .size(120.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nombre
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF222222)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Descripción
                Text(
                    text = producto.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF666666)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Precio
                Text(
                    text = "S/ ${producto.precio}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )
            }
        }
    }
}
