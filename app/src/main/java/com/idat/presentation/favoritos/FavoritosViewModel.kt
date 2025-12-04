package com.idat.presentation.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idat.data.local.preferences.UserPreferencesManager
import com.idat.domain.model.Producto
import com.idat.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritosViewModel @Inject constructor(
    private val repository: ProductoRepository,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _favoritos = MutableStateFlow<List<Producto>>(emptyList())
    val favoritos: StateFlow<List<Producto>> = _favoritos

    val isDarkTheme = userPreferencesManager.isDarkTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    init {
        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        viewModelScope.launch {
            repository.obtenerFavoritos().collect { lista ->
                _favoritos.value = lista
            }
        }
    }

    fun eliminarDeFavoritos(productoId: Int) {
        viewModelScope.launch {
            repository.eliminarDeFavoritos(productoId)
        }
    }
}
