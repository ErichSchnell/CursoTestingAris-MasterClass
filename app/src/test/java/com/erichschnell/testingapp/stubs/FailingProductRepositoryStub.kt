package com.erichschnell.testingapp.stubs

import com.erichschnell.testingapp.domain.models.Product
import com.erichschnell.testingapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FailingProductRepositoryStub(private val exception: Throwable): ProductRepository {
    override fun getProducts(): Flow<List<Product>> = flow { throw exception}

    override fun getProductsByIds(ids: Set<String>): Flow<List<Product>> = flowOf()

    override fun getProductById(id: String): Flow<Product?> = flowOf()

    override suspend fun refreshProduct() {}

}