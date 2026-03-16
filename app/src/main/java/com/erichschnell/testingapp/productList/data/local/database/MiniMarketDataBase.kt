package com.erichschnell.testingapp.productList.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.erichschnell.testingapp.productList.data.local.database.dao.ProductDao
import com.erichschnell.testingapp.productList.data.local.database.dao.PromotionDao
import com.erichschnell.testingapp.productList.data.local.database.entity.ProductEntity
import com.erichschnell.testingapp.productList.data.local.database.entity.PromotionEntity

@Database(
    entities = [ProductEntity::class, PromotionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MiniMarketDataBase: RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun promotionDao(): PromotionDao
}