package com.idat.presentation.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.idat.data.local.preferences.UserPreferencesManager
import com.idat.domain.model.Producto
import com.idat.domain.usecase.ObtenerProductosUseCase
import com.idat.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogoViewModel @Inject constructor(
    private val useCase: ObtenerProductosUseCase,
    private val repository: ProductoRepository,
    private val firebaseAuth: FirebaseAuth,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    val isDarkTheme = userPreferencesManager.isDarkTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val viewMode = userPreferencesManager.viewMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "grid"
    )

    fun cargarProductos() {
        viewModelScope.launch {
            val productos = useCase.ejecutar()
            _productos.value = productos
        }
    }

    fun agregarAlCarrito(producto: Producto) {
        viewModelScope.launch {
            repository.agregarProductoAlCarrito(producto)
        }
    }

    fun toggleFavorito(producto: Producto) {
        viewModelScope.launch {
            if (repository.esFavorito(producto.id)) {
                repository.eliminarDeFavoritos(producto.id)
            } else {
                repository.agregarAFavoritos(producto)
            }
        }
    }

    suspend fun esFavorito(productoId: String): Boolean {
        return repository.esFavorito(productoId)
    }

    fun cerrarSesion() {
        firebaseAuth.signOut()
    }

    fun obtenerEmailUsuario(): String? {
        return firebaseAuth.currentUser?.email
    }
}
