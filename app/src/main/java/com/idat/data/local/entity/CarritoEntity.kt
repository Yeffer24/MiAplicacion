package com.idat.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "carrito",
    primaryKeys = ["id", "userId"]
)
data class CarritoEntity(
    val id: Int,
    val userId: String,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val cantidad: Int = 1
)
