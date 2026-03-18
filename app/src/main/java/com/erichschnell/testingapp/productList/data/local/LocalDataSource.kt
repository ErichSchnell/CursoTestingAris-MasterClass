package com.erichschnell.testingapp.productList.data.local

import com.erichschnell.testingapp.cart.data.local.database.dao.CartItemDao
import com.erichschnell.testingapp.cart.data.local.database.entity.CartItemEntity
import com.erichschnell.testingapp.productList.data.local.database.dao.ProductDao
import com.erichschnell.testingapp.productList.data.local.database.dao.PromotionDao
import com.erichschnell.testingapp.productList.data.local.database.entity.ProductEntity
import com.erichschnell.testingapp.productList.data.local.database.entity.PromotionEntity
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val promotionDao: PromotionDao,
    private val cartItemDao: CartItemDao,
) {
    fun getAllProducts() = productDao.getAllProducts()
    fun getProductById(id: String) = productDao.getProductById(id)
    suspend fun saveProducts(products: List<ProductEntity>) = productDao.replaceAll(products)

    fun getAllPromotions() = promotionDao.getAllPromotions()
    suspend fun savePromotions ( promotions: List<PromotionEntity>) = promotionDao.replaceAll(promotions)



    fun getllCartItems() = cartItemDao.getAllCartItems()
    suspend fun getCartItemById(productId: String) = cartItemDao.getCartItemById(productId)
    suspend fun insertCartItem(cartItem: CartItemEntity): Result<Unit> {
        return try {
            cartItemDao.insertCartItem(cartItem)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updateCartItem(cartItem: CartItemEntity): Result<Unit> {
        return try {
            cartItemDao.updateCartItem(cartItem)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun deleteCartItem(cartItem: CartItemEntity): Result<Unit> {
        return try {
            cartItemDao.deleteCartItem(cartItem)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun clearCartItem(): Result<Unit> {
        return try {
            cartItemDao.clearCart()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




}