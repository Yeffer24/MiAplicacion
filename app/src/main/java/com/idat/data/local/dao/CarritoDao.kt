package com.idat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.idat.data.local.entity.CarritoEntity

@Dao
interface CarritoDao {

    @Query("SELECT * FROM carrito WHERE userId = :userId")
    fun obtenerCarrito(userId: String): Flow<List<CarritoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(item: CarritoEntity)

    @Update
    suspend fun actualizar(item: CarritoEntity)

    @Query("DELETE FROM carrito WHERE id = :productoId AND userId = :userId")
    suspend fun eliminarPorId(productoId: Int, userId: String)

    @Query("DELETE FROM carrito WHERE userId = :userId")
    suspend fun limpiarCarrito(userId: String)

    @Query("SELECT * FROM carrito WHERE id = :productoId AND userId = :userId")
    suspend fun obtenerPorId(productoId: Int, userId: String): CarritoEntity?
}
