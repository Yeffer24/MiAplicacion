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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
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
                        title = { Text("Catálogo", color = Color(0xFF222222), fontWeight = FontWeight.Bold) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White
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
                                    tint = Color(0xFF222222)
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate("carrito") }) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Bolsa",
                                    tint = Color(0xFF222222)
                                )
                            }
                        }
                    )
                },
                containerColor = Color(0xFFFAFAFA)
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
    
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = Color.White
    ) {
        // Header con avatar y nombre
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
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
                color = Color(0xFF222222)
            )
            
            // Email
            Text(
                text = usuarioEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )
        }
        
        Divider(color = Color(0xFFEEEEEE))
        
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
            color = Color(0xFFEEEEEE),
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            .height(320.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorito",
                    tint = Color(0xFF222222),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(24.dp)
                )
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
                    color = Color(0xFF222222)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Precio
                Text(
                    text = "QAR ${producto.precio}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
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
                    text = "QAR ${producto.precio}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )
            }
        }
    }
}
