package com.erichschnell.testingapp.fakes

import com.erichschnell.testingapp.domain.models.Product
import com.erichschnell.testingapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class FakeProductRepository: ProductRepository  {

    private val _products = MutableStateFlow<List<Product>>(emptyList())

    fun setProducts( products: List<Product>){
        _products.value = products
    }

    override fun getProducts(): Flow<List<Product>> = _products.asStateFlow()

    override fun getProductsByIds(ids: Set<String>): Flow<List<Product>> {
        return _products.asStateFlow().map {product ->
            product.filter { it.id in ids }
        }
    }

    override fun getProductById(id: String): Flow<Product?> {
        return _products.asStateFlow().map { products ->
            products.find { it.id == id }
        }
    }

    override suspend fun refreshProduct() {
        //no effect
    }
}