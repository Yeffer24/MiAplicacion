package com.idat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.idat.data.local.entity.FavoritoEntity

@Dao
interface FavoritoDao {

    @Query("SELECT * FROM favoritos")
    fun obtenerFavoritos(): Flow<List<FavoritoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(item: FavoritoEntity)

    @Query("DELETE FROM favoritos WHERE id = :productoId")
    suspend fun eliminarPorId(productoId: Int)

    @Query("SELECT * FROM favoritos WHERE id = :productoId")
    suspend fun obtenerPorId(productoId: Int): FavoritoEntity?

    @Query("DELETE FROM favoritos")
    suspend fun limpiarFavoritos()
}
