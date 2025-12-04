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

    @Query("SELECT * FROM carrito")
    fun obtenerCarrito(): Flow<List<CarritoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(item: CarritoEntity)

    @Update
    suspend fun actualizar(item: CarritoEntity)

    @Query("DELETE FROM carrito WHERE id = :productoId")
    suspend fun eliminarPorId(productoId: Int)

    @Query("DELETE FROM carrito")
    suspend fun limpiarCarrito()

    @Query("SELECT * FROM carrito WHERE id = :productoId")
    suspend fun obtenerPorId(productoId: Int): CarritoEntity?
}
