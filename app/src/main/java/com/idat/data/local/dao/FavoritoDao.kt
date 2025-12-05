package com.idat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.idat.data.local.entity.FavoritoEntity

@Dao
interface FavoritoDao {

    @Query("SELECT * FROM favoritos WHERE userId = :userId")
    fun obtenerFavoritos(userId: String): Flow<List<FavoritoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(item: FavoritoEntity)

    @Query("DELETE FROM favoritos WHERE id = :productoId AND userId = :userId")
    suspend fun eliminarPorId(productoId: Int, userId: String)

    @Query("SELECT * FROM favoritos WHERE id = :productoId AND userId = :userId")
    suspend fun obtenerPorId(productoId: Int, userId: String): FavoritoEntity?

    @Query("DELETE FROM favoritos WHERE userId = :userId")
    suspend fun limpiarFavoritos(userId: String)
}
