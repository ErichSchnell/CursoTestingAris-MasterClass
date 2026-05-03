package com.erichschnell.testingapp.data.local

import com.erichschnell.testingapp.data.local.database.dao.CartItemDao
import com.erichschnell.testingapp.data.local.database.entity.CartItemEntity
import com.erichschnell.testingapp.data.local.database.dao.ProductDao
import com.erichschnell.testingapp.data.local.database.dao.PromotionDao
import com.erichschnell.testingapp.data.local.database.entity.ProductEntity
import com.erichschnell.testingapp.data.local.database.entity.PromotionEntity
import com.erichschnell.testingapp.domain.core.model.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val promotionDao: PromotionDao,
    private val cartItemDao: CartItemDao,
) {
    fun getAllProducts() = productDao.getAllProducts()
    fun getProductsByIds(ids: Set<String>): Flow<List<ProductEntity>> {
        if (ids.isEmpty()) return flowOf(emptyList())
        return productDao.getProductsByIds(ids.toList()).catch { throw AppError.DatabaseError }
    }
    fun getProductById(id: String) = productDao.getProductById(id).catch { throw AppError.DatabaseError }
    suspend fun saveProducts(products: List<ProductEntity>) = productDao.replaceAll(products)

    fun getAllPromotions() = promotionDao.getAllPromotions().catch { throw AppError.DatabaseError }
    suspend fun savePromotions ( promotions: List<PromotionEntity>) = promotionDao.replaceAll(promotions)



    fun getAllCartItems() = cartItemDao.getAllCartItems().catch { throw AppError.DatabaseError }
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