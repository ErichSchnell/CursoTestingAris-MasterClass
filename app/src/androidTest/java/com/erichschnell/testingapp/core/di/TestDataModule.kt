package com.erichschnell.testingapp.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.erichschnell.testingapp.data.DefaultDispatchersProvider
import com.erichschnell.testingapp.data.local.database.MiniMarketDataBase
import com.erichschnell.testingapp.data.local.database.dao.CartItemDao
import com.erichschnell.testingapp.data.local.database.dao.ProductDao
import com.erichschnell.testingapp.data.local.database.dao.PromotionDao
import com.erichschnell.testingapp.data.repository.CartRepositoryImpl
import com.erichschnell.testingapp.data.repository.ProductRepositoryImpl
import com.erichschnell.testingapp.data.repository.PromotionRepositoryImpl
import com.erichschnell.testingapp.data.repository.SettingsRepositoryImpl
import com.erichschnell.testingapp.data.util.SystemClock
import com.erichschnell.testingapp.di.DataModule
import com.erichschnell.testingapp.domain.core.coroutines.DispatchersProvider
import com.erichschnell.testingapp.domain.repository.CartRepository
import com.erichschnell.testingapp.domain.repository.ProductRepository
import com.erichschnell.testingapp.domain.repository.PromotionRepository
import com.erichschnell.testingapp.domain.repository.SettingsRepository
import com.erichschnell.testingapp.domain.util.Clock
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

private val Context.testingDatastore: DataStore<Preferences> by preferencesDataStore(name = "testing_settings")

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
object TestDataModule {

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
    fun providesDatabase(): MiniMarketDataBase{
        val context = ApplicationProvider.getApplicationContext<Context>()
        return Room.inMemoryDatabaseBuilder(
            context = context,
            klass = MiniMarketDataBase::class.java,
        ).build()
    }

    @Provides
    @Singleton
    fun provideDataStore(): DataStore<Preferences> {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return context.testingDatastore
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

    @Provides
    @Singleton
    fun provideSystemClock(systemClock: SystemClock): Clock {
        return systemClock
    }
}