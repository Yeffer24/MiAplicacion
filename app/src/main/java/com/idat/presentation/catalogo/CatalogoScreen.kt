package com.idat.presentation.catalogo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    navController: NavHostController,
    viewModel: CatalogoViewModel = hiltViewModel()
) {
    val productos by viewModel.productos.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarProductos()
    }

    // Colores dinámicos según el tema
    val gradientColors = if (isDarkTheme) {
        listOf(
            Color(0xFF1A1A2E),
            Color(0xFF16213E),
            Color(0xFF0F3460)
        )
    } else {
        listOf(
            Color(0xFF7F00FF).copy(alpha = 0.4f),
            Color(0xFFE100FF).copy(alpha = 0.35f),
            Color(0xFF00C6FF).copy(alpha = 0.35f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = gradientColors))
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
                        navController.navigate("favoritos")
                    },
                    onNavigateToPersonalizacion = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate("personalizacion")
                    },
                    onNavigateToConfiguracion = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate("configuracion")
                    },
                    onNavigateToGestion = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate("gestion")
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Catálogo", color = Color.White) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
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
                                    tint = Color.White
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate("carrito") }) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Carrito",
                                    tint = Color.White
                                )
                            }
                        }
                    )
                },
                containerColor = Color.Transparent
            ) { paddingValues ->
                if (viewMode == "grid") {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(productos) { producto ->
                            ProductoGridItem(
                                producto = producto,
                                onAgregarAlCarrito = { viewModel.agregarAlCarrito(producto) },
                                onClick = { navController.navigate("detalle/${producto.id}") }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(paddingValues)
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
    onNavigateToGestion: () -> Unit
) {
    val usuarioEmail = viewModel.obtenerEmailUsuario() ?: "usuario@ejemplo.com"
    val usuarioNombre = usuarioEmail.substringBefore("@")
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    
    val gradientColors = if (isDarkTheme) {
        listOf(
            Color(0xFF1A1A2E).copy(alpha = 0.9f),
            Color(0xFF16213E).copy(alpha = 0.85f),
            Color(0xFF0F3460).copy(alpha = 0.85f)
        )
    } else {
        listOf(
            Color(0xFF7F00FF).copy(alpha = 0.45f),
            Color(0xFFE100FF).copy(alpha = 0.4f),
            Color(0xFF00C6FF).copy(alpha = 0.4f)
        )
    }
    
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = Color.Transparent
    ) {
        // Header con avatar y nombre
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors = gradientColors))
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Avatar circular
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF7F00FF), CircleShape),
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
                color = Color.White
            )
            
            // Email
            Text(
                text = usuarioEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
        
        Divider(color = Color.White.copy(alpha = 0.1f))
        
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
            onClick = { /* TODO */ },
            showArrow = true
        )
        
        Divider(
            color = Color.White.copy(alpha = 0.1f),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        // Cerrar sesión
        DrawerMenuItem(
            icon = Icons.Default.Logout,
            text = "Cerrar sesión",
            onClick = onCerrarSesion,
            textColor = Color(0xFFFF4444)
        )
    }
}

@Composable
fun DrawerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    textColor: Color = Color.White,
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = textColor.copy(alpha = 0.8f),
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
                    tint = Color.White.copy(alpha = 0.5f),
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
        icon = {
            Icon(
                Icons.Default.Logout,
                contentDescription = "Cerrar Sesión",
                modifier = Modifier.size(48.dp),
                tint = Color(0xFFE100FF)
            )
        },
        title = {
            Text(
                text = "¿Cerrar Sesión?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que deseas cerrar sesión? Tendrás que volver a iniciar sesión para acceder a tu cuenta.",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE100FF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Sí, cerrar sesión")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar", color = Color.Gray)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoGridItem(
    producto: Producto,
    onAgregarAlCarrito: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Imagen
            Image(
                painter = rememberAsyncImagePainter(model = producto.imagen),
                contentDescription = "Imagen de ${producto.nombre}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            // Precio
            Text(
                text = "S/ ${producto.precio}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFE100FF)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Botón agregar
            Button(
                onClick = onAgregarAlCarrito,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.9f),
                    contentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Agregar",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodySmall)
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
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() }, // sombra suave
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Imagen
            Image(
                painter = rememberAsyncImagePainter(model = producto.imagen),
                contentDescription = "Imagen de ${producto.nombre}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Precio
            Text(
                text = "S/ ${producto.precio}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFE100FF)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Text(
                text = producto.descripcion,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botón agregar al carrito
            Button(
                onClick = onAgregarAlCarrito,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.9f),
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Agregar",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar al carrito", fontWeight = FontWeight.Medium)
            }
        }
    }
}
