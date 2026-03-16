package com.erichschnell.testingapp.productList.data.local.database.entity

import androidx.room.Entity

@Entity(tableName = "products")
data class ProductEntity (
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val category: String?,
    val stock: Int?,
    val imageUrl: String? = null,
)