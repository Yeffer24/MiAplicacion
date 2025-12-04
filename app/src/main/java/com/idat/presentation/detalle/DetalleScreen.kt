package com.idat.presentation.detalle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    navController: NavHostController,
    productoId: Int,
    viewModel: DetalleViewModel = hiltViewModel()
) {
    val producto by viewModel.producto.collectAsState()
    val esFavorito by viewModel.esFavorito.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val backgroundColor = if (isDarkTheme) Color(0xFF1C1C1C) else Color(0xFFFAFAFA)
    val cardColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val textColor = if (isDarkTheme) Color(0xFFFAFAFA) else Color(0xFF222222)
    val textSecondaryColor = if (isDarkTheme) Color(0xFFCCCCCC) else Color(0xFF666666)

    LaunchedEffect(productoId) {
        viewModel.cargarProducto(productoId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalle del Producto", color = textColor, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = cardColor
                    ),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color(0xFF222222)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.toggleFavorito() }) {
                            Icon(
                                imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                                tint = if (esFavorito) Color(0xFFE50010) else textColor
                            )
                        }
                    }
                )
            },
            containerColor = Color(0xFFFAFAFA),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            producto?.let { prod ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card con imagen
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(0.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = prod.imagen),
                            contentDescription = prod.nombre,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Card con información
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(0.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Nombre
                            Text(
                                text = prod.nombre,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Normal,
                                color = textColor
                            )

                            // Precio
                            Text(
                                text = "S/ ${String.format("%.2f", prod.precio)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )

                            // Categoría
                            Text(
                                text = prod.categoria,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDarkTheme) Color(0xFFCCCCCC) else Color(0xFF666666)
                            )

                            Divider(
                                color = if (isDarkTheme) Color(0xFF404040) else Color(0xFFEEEEEE),
                                modifier = Modifier.padding(vertical = 12.dp)
                            )

                            // Descripción
                            Text(
                                text = "Descripción",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )

                            Text(
                                text = prod.descripcion,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF666666),
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón agregar al carrito
                    Button(
                        onClick = {
                            viewModel.agregarAlCarrito(prod)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Producto agregado al carrito",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE50010),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Agregar",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Agregar a la Bolsa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFE50010))
            }
        }
    }
}
