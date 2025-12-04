package com.idat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow
import com.idat.data.local.entity.ProductoEntity

@Dao
interface ProductoDao {

    // ========== LEER (Read) ==========
    @Query("SELECT * FROM productos")
    fun obtenerTodos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :productoId")
    suspend fun obtenerPorId(productoId: Int): ProductoEntity?

    @Query("SELECT * FROM productos WHERE category = :categoria")
    fun obtenerPorCategoria(categoria: String): Flow<List<ProductoEntity>>

    // ========== CREAR (Create) ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: ProductoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(productos: List<ProductoEntity>)

    // ========== ACTUALIZAR (Update) ==========
    @Update
    suspend fun actualizar(producto: ProductoEntity)

    @Query("UPDATE productos SET title = :title, price = :price, description = :description, category = :category, image = :image, rating = :rating, ratingCount = :ratingCount WHERE id = :id")
    suspend fun actualizarProducto(
        id: Int,
        title: String,
        price: Double,
        description: String,
        category: String,
        image: String,
        rating: Double,
        ratingCount: Int
    )

    // ========== ELIMINAR (Delete) ==========
    @Delete
    suspend fun eliminar(producto: ProductoEntity)

    @Query("DELETE FROM productos WHERE id = :productoId")
    suspend fun eliminarPorId(productoId: Int)

    @Query("DELETE FROM productos")
    suspend fun limpiarTodos()

    // ========== BUSCAR ==========
    @Query("SELECT * FROM productos WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun buscarProductos(query: String): Flow<List<ProductoEntity>>
}
