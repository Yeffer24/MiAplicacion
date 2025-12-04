package com.idat.presentation.carrito

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.idat.domain.model.ItemCarrito

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    navController: NavHostController,
    viewModel: CarritoViewModel = hiltViewModel()
) {
    val productos by viewModel.productos.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val total = productos.sumOf { it.precio * it.cantidad }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Bolsa de Compras", color = Color(0xFF222222), fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color(0xFF222222)
                            )
                        }
                    }
                )
            },
            containerColor = Color(0xFFFAFAFA)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (productos.isEmpty()) {
                    // Carrito vacío
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
                                contentDescription = "Bolsa vacía",
                                modifier = Modifier.size(100.dp),
                                tint = Color(0xFFCCCCCC)
                            )
                            Text(
                                text = "Tu bolsa está vacía",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color(0xFF222222),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Agrega productos para continuar",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                } else {
                    // Lista de productos
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(productos) { item ->
                            ProductoCarritoItem(
                                item = item,
                                onEliminar = { viewModel.eliminarDelCarrito(item.id) },
                                onIncrementar = { viewModel.incrementarCantidad(item) },
                                onDecrementar = { viewModel.decrementarCantidad(item) }
                            )
                        }
                    }

                    // Card de Total y Botón de Compra
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(0.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total:",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF222222)
                                )
                                Text(
                                    text = "QAR ${String.format("%.2f", total)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF222222)
                                )
                            }

                            Button(
                                onClick = { /* TODO: Procesar compra */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE50010),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "Proceder al Pago",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoCarritoItem(
    item: ItemCarrito,
    onEliminar: () -> Unit,
    onIncrementar: () -> Unit,
    onDecrementar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen del producto
            Image(
                painter = rememberAsyncImagePainter(model = item.imagen),
                contentDescription = item.nombre,
                modifier = Modifier.size(100.dp)
            )

            // Información del producto
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF222222)
                )

                Text(
                    text = "QAR ${item.precio}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )

                // Controles de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onDecrementar,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrementar",
                            tint = Color(0xFF222222),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "${item.cantidad}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF222222),
                        modifier = Modifier.widthIn(min = 30.dp)
                    )

                    IconButton(
                        onClick = onIncrementar,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Incrementar",
                            tint = Color(0xFF222222),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Botón eliminar
            IconButton(
                onClick = onEliminar,
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFF999999)
                )
            }
        }
    }
    Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
}
