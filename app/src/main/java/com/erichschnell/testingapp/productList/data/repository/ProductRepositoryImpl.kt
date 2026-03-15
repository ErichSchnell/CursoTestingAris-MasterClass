package com.erichschnell.testingapp.productList.data.repository

import com.erichschnell.testingapp.core.domain.coroutines.DispatchersProvider
import com.erichschnell.testingapp.productList.data.remote.RemoteDataSource
import com.erichschnell.testingapp.productList.domain.models.Product
import com.erichschnell.testingapp.productList.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val dispatchers: DispatchersProvider
): ProductRepository{
    override fun getProducts(): Flow<List<Product>> {
        TODO("Not yet implemented")
    }

    override fun getProductById(id: String): Flow<Product?> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshProduct() {
        withContext(dispatchers.io){
            remoteDataSource.getProducts()
        }
    }
}