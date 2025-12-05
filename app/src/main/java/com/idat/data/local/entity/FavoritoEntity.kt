package com.idat.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "favoritos",
    primaryKeys = ["id", "userId"]
)
data class FavoritoEntity(
    val id: Int,
    val userId: String,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String
)
