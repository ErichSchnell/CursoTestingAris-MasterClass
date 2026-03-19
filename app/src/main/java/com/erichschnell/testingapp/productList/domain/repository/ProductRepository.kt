package com.erichschnell.testingapp.productList.domain.repository

import com.erichschnell.testingapp.productList.domain.models.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>

    fun getProductsByIds(ids: Set<String>): Flow<List<Product>>
    fun getProductById(id: String): Flow<Product?>
    suspend fun refreshProduct()
}