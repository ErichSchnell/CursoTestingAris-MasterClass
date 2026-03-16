package com.erichschnell.testingapp.productList.data.local

import com.erichschnell.testingapp.productList.data.local.database.dao.ProductDao
import com.erichschnell.testingapp.productList.data.local.database.dao.PromotionDao
import com.erichschnell.testingapp.productList.data.local.database.entity.ProductEntity
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val promotionDao: PromotionDao
) {
    fun getAllProducts() = productDao.getAllProducts()
    fun getProductById(id: String) = productDao.getProductById(id)
    suspend fun saveProducts(products: List<ProductEntity>) = productDao.replaceAll(products)
}