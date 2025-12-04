package com.idat.data.repository

import com.idat.domain.model.Producto
import com.idat.domain.model.ItemCarrito
import com.idat.domain.repository.ProductoRepository
import com.idat.data.local.dao.ProductoDao
import com.idat.data.local.dao.CarritoDao
import com.idat.data.local.dao.FavoritoDao
import com.idat.data.remote.service.ProductoApiService
import com.idat.data.local.entity.ProductoEntity
import com.idat.data.local.entity.CarritoEntity
import com.idat.data.local.entity.FavoritoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductoRepositoryImpl @Inject constructor(
    private val productoDao: ProductoDao,
    private val carritoDao: CarritoDao,
    private val favoritoDao: FavoritoDao,
    private val apiService: ProductoApiService
) : ProductoRepository {

    override suspend fun obtenerProductos(): List<Producto> {
        // Obtener de la API y guardar en local
        val productosDto = apiService.getProducts()
        val entities = productosDto.map {
            ProductoEntity(
                id = it.id,
                title = it.title,
                price = it.price,
                description = it.description ?: "",
                category = it.category ?: "",
                image = it.image ?: "",
                rating = it.rating?.rate ?: 0.0,
                ratingCount = it.rating?.count ?: 0
            )
        }
        productoDao.insertarTodos(entities)
        
        // Devolver desde local
        return productoDao.obtenerTodos().first().map {
            Producto(
                id = it.id,
                nombre = it.title,
                precio = it.price,
                descripcion = it.description,
                categoria = it.category,
                imagen = it.image,
                calificacion = it.rating,
                cantidadCalificaciones = it.ratingCount
            )
        }
    }

    override suspend fun agregarProductoAlCarrito(producto: Producto) {
        // Verificar si el producto ya existe en el carrito
        val itemExistente = carritoDao.obtenerPorId(producto.id)
        
        if (itemExistente != null) {
            // Si existe, incrementar la cantidad
            val itemActualizado = itemExistente.copy(cantidad = itemExistente.cantidad + 1)
            carritoDao.actualizar(itemActualizado)
        } else {
            // Si no existe, agregarlo con cantidad 1
            val entity = CarritoEntity(
                id = producto.id,
                title = producto.nombre,
                price = producto.precio,
                description = producto.descripcion,
                category = producto.categoria,
                image = producto.imagen,
                cantidad = 1
            )
            carritoDao.insertar(entity)
        }
    }

    override suspend fun obtenerCarrito(): Flow<List<ItemCarrito>> {
        return carritoDao.obtenerCarrito().map { lista ->
            lista.map {
                ItemCarrito(
                    id = it.id,
                    nombre = it.title,
                    precio = it.price,
                    descripcion = it.description,
                    categoria = it.category,
                    imagen = it.image,
                    cantidad = it.cantidad
                )
            }
        }
    }

    override suspend fun eliminarProductoDelCarrito(productoId: Int) {
        carritoDao.eliminarPorId(productoId)
    }

    override suspend fun actualizarCantidad(productoId: Int, cantidad: Int) {
        val item = carritoDao.obtenerPorId(productoId)
        item?.let {
            if (cantidad > 0) {
                val itemActualizado = it.copy(cantidad = cantidad)
                carritoDao.actualizar(itemActualizado)
            } else {
                // Si la cantidad es 0, eliminar del carrito
                carritoDao.eliminarPorId(productoId)
            }
        }
    }

    override suspend fun obtenerProductoPorId(productoId: Int): Producto? {
        return productoDao.obtenerTodos().first().find { it.id == productoId }?.let {
            Producto(
                id = it.id,
                nombre = it.title,
                precio = it.price,
                descripcion = it.description,
                categoria = it.category,
                imagen = it.image,
                calificacion = it.rating,
                cantidadCalificaciones = it.ratingCount
            )
        }
    }

    override suspend fun agregarAFavoritos(producto: Producto) {
        val entity = FavoritoEntity(
            id = producto.id,
            title = producto.nombre,
            price = producto.precio,
            description = producto.descripcion,
            category = producto.categoria,
            image = producto.imagen
        )
        favoritoDao.insertar(entity)
    }

    override suspend fun eliminarDeFavoritos(productoId: Int) {
        favoritoDao.eliminarPorId(productoId)
    }

    override suspend fun obtenerFavoritos(): Flow<List<Producto>> {
        return favoritoDao.obtenerFavoritos().map { lista ->
            lista.map {
                Producto(
                    id = it.id,
                    nombre = it.title,
                    precio = it.price,
                    descripcion = it.description,
                    categoria = it.category,
                    imagen = it.image,
                    calificacion = 0.0,
                    cantidadCalificaciones = 0
                )
            }
        }
    }

    override suspend fun esFavorito(productoId: Int): Boolean {
        return favoritoDao.obtenerPorId(productoId) != null
    }

    // ========== NUEVOS MÉTODOS CRUD ==========
    
    override fun obtenerProductosFlow(): Flow<List<Producto>> {
        return productoDao.obtenerTodos().map { lista ->
            lista.map {
                Producto(
                    id = it.id,
                    nombre = it.title,
                    precio = it.price,
                    descripcion = it.description,
                    categoria = it.category,
                    imagen = it.image,
                    calificacion = it.rating,
                    cantidadCalificaciones = it.ratingCount
                )
            }
        }
    }

    override suspend fun crearProducto(producto: Producto): Long {
        val entity = ProductoEntity(
            id = 0, // Auto-increment
            title = producto.nombre,
            price = producto.precio,
            description = producto.descripcion,
            category = producto.categoria,
            image = producto.imagen,
            rating = producto.calificacion,
            ratingCount = producto.cantidadCalificaciones
        )
        return productoDao.insertar(entity)
    }

    override suspend fun actualizarProducto(producto: Producto) {
        val entity = ProductoEntity(
            id = producto.id,
            title = producto.nombre,
            price = producto.precio,
            description = producto.descripcion,
            category = producto.categoria,
            image = producto.imagen,
            rating = producto.calificacion,
            ratingCount = producto.cantidadCalificaciones
        )
        productoDao.actualizar(entity)
    }

    override suspend fun eliminarProducto(productoId: Int) {
        productoDao.eliminarPorId(productoId)
    }

    override fun buscarProductos(query: String): Flow<List<Producto>> {
        return productoDao.buscarProductos(query).map { lista ->
            lista.map {
                Producto(
                    id = it.id,
                    nombre = it.title,
                    precio = it.price,
                    descripcion = it.description,
                    categoria = it.category,
                    imagen = it.image,
                    calificacion = it.rating,
                    cantidadCalificaciones = it.ratingCount
                )
            }
        }
    }
}
