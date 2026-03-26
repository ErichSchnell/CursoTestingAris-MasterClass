package com.erichschnell.testingapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.erichschnell.testingapp.data.local.database.dao.CartItemDao
import com.erichschnell.testingapp.data.repository.CartRepositoryImpl
import com.erichschnell.testingapp.domain.repository.CartRepository
import com.erichschnell.testingapp.data.DefaultDispatchersProvider
import com.erichschnell.testingapp.domain.core.coroutines.DispatchersProvider
import com.erichschnell.testingapp.data.local.database.MiniMarketDataBase
import com.erichschnell.testingapp.data.local.database.dao.ProductDao
import com.erichschnell.testingapp.data.local.database.dao.PromotionDao
import com.erichschnell.testingapp.data.repository.ProductRepositoryImpl
import com.erichschnell.testingapp.data.repository.PromotionRepositoryImpl
import com.erichschnell.testingapp.data.repository.SettingsRepositoryImpl
import com.erichschnell.testingapp.domain.repository.ProductRepository
import com.erichschnell.testingapp.domain.repository.PromotionRepository
import com.erichschnell.testingapp.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "settings")

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
    fun providesCartItemDao(database: MiniMarketDataBase): CartItemDao {
        return database.cartItemDao()
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

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.datastore
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository {
        return settingsRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideCartItemRepository(cartItemRepositoryImpl: CartRepositoryImpl): CartRepository {
        return cartItemRepositoryImpl
    }
}