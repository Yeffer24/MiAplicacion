package com.idat.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.idat.data.local.dao.ProductoDao
import com.idat.data.local.dao.CarritoDao
import com.idat.data.local.dao.FavoritoDao
import com.idat.data.local.entity.ProductoEntity
import com.idat.data.local.entity.CarritoEntity
import com.idat.data.local.entity.FavoritoEntity

@Database(
    entities = [ProductoEntity::class, CarritoEntity::class, FavoritoEntity::class],
    version = 6,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun carritoDao(): CarritoDao
    abstract fun favoritoDao(): FavoritoDao
}
