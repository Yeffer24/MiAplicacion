package com.idat.presentation.favoritos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
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
import com.idat.domain.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    navController: NavHostController,
    viewModel: FavoritosViewModel = hiltViewModel()
) {
    val productos by viewModel.favoritos.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mis Favoritos", color = Color(0xFF222222), fontWeight = FontWeight.Bold) },
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
            if (productos.isEmpty()) {
                // Sin favoritos
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Sin favoritos",
                            modifier = Modifier.size(100.dp),
                            tint = Color(0xFFCCCCCC)
                        )
                        Text(
                            text = "No tienes favoritos",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF222222),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Agrega productos que te gusten",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF666666)
                        )
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Lista de favoritos
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productos) { producto ->
                        FavoritoItem(
                            producto = producto,
                            onEliminar = { viewModel.eliminarDeFavoritos(producto.id) },
                            onClick = {
                                navController.navigate("detalle/${producto.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritoItem(
    producto: Producto,
    onEliminar: () -> Unit,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen
            Image(
                painter = rememberAsyncImagePainter(model = producto.imagen),
                contentDescription = producto.nombre,
                modifier = Modifier.size(100.dp)
            )

            // Información
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF222222)
                )

                Text(
                    text = "QAR ${producto.precio}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )

                Text(
                    text = producto.categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
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
