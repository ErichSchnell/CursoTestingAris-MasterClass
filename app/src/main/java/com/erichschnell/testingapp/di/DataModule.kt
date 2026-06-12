package com.erichschnell.testingapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
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
import com.erichschnell.testingapp.domain.core.coroutines.DispatchersProvider
import com.erichschnell.testingapp.domain.repository.CartRepository
import com.erichschnell.testingapp.domain.repository.ProductRepository
import com.erichschnell.testingapp.domain.repository.PromotionRepository
import com.erichschnell.testingapp.domain.repository.SettingsRepository
import com.erichschnell.testingapp.domain.util.Clock
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
    fun provideDispatchersProvider(defaultDispatchersProvider: DefaultDispatchersProvider): DispatchersProvider = defaultDispatchersProvider

    @Provides
    @Singleton
    fun provideProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository = productRepositoryImpl

    @Provides
    @Singleton
    fun providePromotionRepository(promotionRepositoryImpl: PromotionRepositoryImpl): PromotionRepository = promotionRepositoryImpl

    @Provides
    fun providesProductDao(database: MiniMarketDataBase): ProductDao = database.productDao()

    @Provides
    fun providesPromotionDao(database: MiniMarketDataBase): PromotionDao = database.promotionDao()

    @Provides
    fun providesCartItemDao(database: MiniMarketDataBase): CartItemDao = database.cartItemDao()

    @Provides
    @Singleton
    fun providesDatabase(
        @ApplicationContext context: Context,
    ): MiniMarketDataBase =
        Room
            .databaseBuilder(
                context = context,
                klass = MiniMarketDataBase::class.java,
                name = "minimarket_database",
            ).build()

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.datastore

    @Provides
    @Singleton
    fun provideSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository = settingsRepositoryImpl

    @Provides
    @Singleton
    fun provideCartItemRepository(cartItemRepositoryImpl: CartRepositoryImpl): CartRepository = cartItemRepositoryImpl

    @Provides
    @Singleton
    fun provideSystemClock(systemClock: SystemClock): Clock = systemClock
}
