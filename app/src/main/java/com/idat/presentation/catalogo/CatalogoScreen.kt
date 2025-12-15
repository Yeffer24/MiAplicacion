package com.idat.presentation.catalogo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
fun CatalogoScreen(
    navController: NavHostController,
    viewModel: CatalogoViewModel = hiltViewModel(),
) {
    val productos by viewModel.productos.collectAsState()
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerPerfil(
                viewModel = viewModel,
                onCerrarSesion = {
                    scope.launch { drawerState.close() }
                    mostrarDialogoCerrarSesion = true
                },
                onClose = { scope.launch { drawerState.close() } },
                onNavigateToFavoritos = { navController.navigate("favoritos/fromDrawer") },
                onNavigateToPersonalizacion = { navController.navigate("personalizacion/fromDrawer") },
                onNavigateToConfiguracion = { navController.navigate("configuracion/fromDrawer") },
                onNavigateToGestion = { navController.navigate("gestion/fromDrawer") },
                onNavigateToAyuda = { navController.navigate("ayuda/fromDrawer") }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Catálogo", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menú") } },
                    actions = { IconButton(onClick = { navController.navigate("carrito") }) { Icon(Icons.Default.ShoppingCart, "Bolsa") } }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                OutlinedTextField(
                    value = textoBusqueda, onValueChange = { viewModel.actualizarBusqueda(it) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Buscar productos...") },
                    leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
                    trailingIcon = { if (textoBusqueda.isNotEmpty()) { IconButton(onClick = { viewModel.actualizarBusqueda("") }) { Icon(Icons.Default.Close, "Limpiar") } } },
                    singleLine = true, shape = RoundedCornerShape(8.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(categorias) { categoria ->
                        FilterChip(selected = categoriaSeleccionada == categoria, onClick = { viewModel.seleccionarCategoria(categoria) },
                            label = { Text(categoria, fontWeight = FontWeight.SemiBold) }, shape = RoundedCornerShape(8.dp))
                    }
                }

                if (productos.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Search, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                            Text("No se encontraron productos", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }
                } else {
                    if (viewMode == "grid") {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(productos, key = { it.id }) { producto ->
                                ProductoGridItem(producto = producto, onClick = { navController.navigate("detalle/${producto.id}") }, onToggleFavorito = { viewModel.toggleFavorito(producto) }, viewModel = viewModel)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(productos, key = { it.id }) { producto ->
                                ProductoCard(producto = producto, onClick = { navController.navigate("detalle/${producto.id}") })
                            }
                        }
                    }
                }
            }
        }

        if (mostrarDialogoCerrarSesion) {
            DialogoConfirmacionCerrarSesion(
                onConfirmar = {
                    mostrarDialogoCerrarSesion = false
                    viewModel.cerrarSesion()
                    navController.navigate("login") { popUpTo("catalogo") { inclusive = true } }
                },
                onCancelar = { mostrarDialogoCerrarSesion = false }
            )
        }
    }
}

@Composable
fun DrawerPerfil(
    viewModel: CatalogoViewModel, onCerrarSesion: () -> Unit, onClose: () -> Unit, onNavigateToFavoritos: () -> Unit,
    onNavigateToPersonalizacion: () -> Unit, onNavigateToConfiguracion: () -> Unit, onNavigateToGestion: () -> Unit, onNavigateToAyuda: () -> Unit
) {
    val usuarioEmail = viewModel.obtenerEmailUsuario() ?: "usuario@ejemplo.com"
    val usuarioNombre = usuarioEmail.substringBefore("@")

    ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.Start) {
            Box(modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.secondary, CircleShape), contentAlignment = Alignment.Center) {
                Text(usuarioNombre.take(1).uppercase(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(usuarioNombre.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(usuarioEmail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
        Divider()
        DrawerMenuItem(icon = Icons.Default.Favorite, text = "Favoritos", onClick = onNavigateToFavoritos)
        DrawerMenuItem(icon = Icons.Default.Person, text = "Personalización", onClick = onNavigateToPersonalizacion)
        DrawerMenuItem(icon = Icons.Default.Settings, text = "Configuración", onClick = onNavigateToConfiguracion)
        DrawerMenuItem(icon = Icons.Default.ShoppingCart, text = "Gestión de Productos", onClick = onNavigateToGestion)
        DrawerMenuItem(icon = Icons.Default.Help, text = "Ayuda", onClick = onNavigateToAyuda, showArrow = true)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        DrawerMenuItem(icon = Icons.Default.Logout, text = "Cerrar sesión", onClick = onCerrarSesion, textColor = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun DrawerMenuItem(icon: ImageVector, text: String, onClick: () -> Unit, textColor: Color = Color.Unspecified, showArrow: Boolean = false) {
    val color = if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.onSurface
    Surface(color = Color.Transparent, onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, text, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge, color = color, modifier = Modifier.weight(1f))
            if (showArrow) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Ir", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun DialogoConfirmacionCerrarSesion(onConfirmar: () -> Unit, onCancelar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Cerrar Sesión", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold) },
        text = { Text("¿Estás seguro de que deseas cerrar sesión?", style = MaterialTheme.typography.bodyMedium) },
        confirmButton = { Button(onClick = onConfirmar, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary), modifier = Modifier.fillMaxWidth()) { Text("Cerrar Sesión") } },
        dismissButton = { OutlinedButton(onClick = onCancelar, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") } },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun ProductoGridItem(
    producto: Producto, onClick: () -> Unit, onToggleFavorito: () -> Unit, viewModel: CatalogoViewModel
) {
    var esFavorito by remember { mutableStateOf(false) }
    LaunchedEffect(producto.id) { esFavorito = viewModel.esFavorito(producto.id) }

    Card(modifier = Modifier.fillMaxWidth().height(320.dp).clickable { onClick() }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                Image(rememberAsyncImagePainter(model = producto.imagen), producto.nombre, modifier = Modifier.fillMaxSize())
                IconButton(onClick = { onToggleFavorito(); esFavorito = !esFavorito }, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                    Icon(Icons.Default.Favorite, "Favorito", tint = if (esFavorito) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(24.dp))
                }
            }
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Text(producto.nombre, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text("S/ ${producto.precio}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProductoCard(producto: Producto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Card(shape = RoundedCornerShape(8.dp)) {
                Image(rememberAsyncImagePainter(model = producto.imagen), producto.nombre, modifier = Modifier.size(100.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("S/ ${producto.precio}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                Text(producto.categoria, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
        }
    }
}
