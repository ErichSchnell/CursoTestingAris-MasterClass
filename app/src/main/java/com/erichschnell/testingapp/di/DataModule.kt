package com.erichschnell.testingapp.di

import com.erichschnell.testingapp.core.data.DefaultDispatchersProvider
import com.erichschnell.testingapp.core.domain.coroutines.DispatchersProvider
import com.erichschnell.testingapp.productList.data.remote.MiniMarketApiService
import com.erichschnell.testingapp.productList.data.repository.ProductRepositoryImpl
import com.erichschnell.testingapp.productList.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDispatchersProvider(defaultDispatchersProvider: DefaultDispatchersProvider): DispatchersProvider{
        return defaultDispatchersProvider
    }

    @Provides
    @Singleton
    fun provideProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository {
        return productRepositoryImpl
    }
}