package com.idat.presentation.components



import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.idat.domain.model.Producto

@Composable
fun ProductItem(producto: Producto) {
    Text(text = producto.nombre)
}
