package com.erichschnell.testingapp.di

import android.content.Context
import androidx.room.Room
import com.erichschnell.testingapp.core.data.DefaultDispatchersProvider
import com.erichschnell.testingapp.core.domain.coroutines.DispatchersProvider
import com.erichschnell.testingapp.productList.data.local.database.MiniMarketDataBase
import com.erichschnell.testingapp.productList.data.local.database.dao.ProductDao
import com.erichschnell.testingapp.productList.data.local.database.dao.PromotionDao
import com.erichschnell.testingapp.productList.data.remote.MiniMarketApiService
import com.erichschnell.testingapp.productList.data.repository.ProductRepositoryImpl
import com.erichschnell.testingapp.productList.data.repository.PromotionRepositoryImpl
import com.erichschnell.testingapp.productList.domain.repository.ProductRepository
import com.erichschnell.testingapp.productList.domain.repository.PromotionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

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


    @Provides
    @Singleton
    fun providePromotionRepository(promotionRepositoryImpl: PromotionRepositoryImpl): PromotionRepository {
        return promotionRepositoryImpl
    }

    @Provides
    fun providesProductDao(database: MiniMarketDataBase): ProductDao {
        return database.productDao()
    }

    @Provides
    fun providesPromotionDao(database: MiniMarketDataBase): PromotionDao {
        return database.promotionDao()
    }

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): MiniMarketDataBase{
        return Room.databaseBuilder(
            context = context,
            klass = MiniMarketDataBase::class.java,
            name = "minimarket_database"
        ).build()
    }
}