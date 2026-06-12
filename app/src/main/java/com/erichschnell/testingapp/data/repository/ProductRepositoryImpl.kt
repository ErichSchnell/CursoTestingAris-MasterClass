package com.erichschnell.testingapp.data.repository

import com.erichschnell.testingapp.data.local.LocalDataSource
import com.erichschnell.testingapp.data.mappers.toDomain
import com.erichschnell.testingapp.data.mappers.toEntity
import com.erichschnell.testingapp.data.remote.RemoteDataSource
import com.erichschnell.testingapp.domain.core.coroutines.DispatchersProvider
import com.erichschnell.testingapp.domain.models.Product
import com.erichschnell.testingapp.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl
    @Inject
    constructor(
        private val remoteDataSource: RemoteDataSource,
        private val localDataSource: LocalDataSource,
        private val dispatchers: DispatchersProvider,
    ) : ProductRepository {
        private val refreshScope = CoroutineScope(SupervisorJob() + dispatchers.io)
        private val refreshMutex = Mutex()

        override fun getProducts(): Flow<List<Product>> {
            return localDataSource
                .getAllProducts()
                .map { entity -> entity.mapNotNull { it.toDomain() } }
                .onStart {
                    refreshScope.launch {
                        if (!refreshMutex.tryLock()) return@launch
                        try {
                            refreshProduct()
                        } catch (e: Exception) {
                        } finally {
                            refreshMutex.unlock()
                        }
                    }
                }.catch {
                }
        }

        override fun getProductsByIds(ids: Set<String>): Flow<List<Product>> =
            localDataSource
                .getProductsByIds(ids)
                .map { entity -> entity.mapNotNull { it.toDomain() } }

        override fun getProductById(id: String): Flow<Product?> =
            localDataSource
                .getProductById(id)
                .map { it?.toDomain() }

        override suspend fun refreshProduct() {
            withContext(dispatchers.io) {
                val products = remoteDataSource.getProducts().getOrThrow()
                val productsEntity = products.map { it.toEntity() }
                localDataSource.saveProducts(productsEntity)
            }
        }
    }
