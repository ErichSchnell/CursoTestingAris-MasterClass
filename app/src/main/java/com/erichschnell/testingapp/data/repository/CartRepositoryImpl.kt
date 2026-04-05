package com.erichschnell.testingapp.data.repository

import com.erichschnell.testingapp.domain.core.model.AppError
import com.erichschnell.testingapp.data.local.LocalDataSource
import com.erichschnell.testingapp.data.mappers.toDomain
import com.erichschnell.testingapp.data.mappers.toEntity
import com.erichschnell.testingapp.domain.models.CartItem
import com.erichschnell.testingapp.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
): CartRepository {
    override fun getCartItems(): Flow<List<CartItem>> = localDataSource.getAllCartItems()
        .map { cartItems -> cartItems.map { it.toDomain() } }

    override suspend fun getCartItemById(productId: String): CartItem? {
        return localDataSource.getCartItemById(productId)?.toDomain()
    }

    override suspend fun addToCart(productId: String, quantity: Int) {
        val existingItems = localDataSource.getCartItemById(productId)
        if (existingItems != null){
            val newQuantity = existingItems.quantity + quantity
            localDataSource.updateCartItem(existingItems.copy(quantity = newQuantity))
        } else {
            localDataSource.insertCartItem(CartItem(productId, quantity).toEntity())
        }
    }

    override suspend fun removeCartItem(productId: String) {
        val item = localDataSource.getCartItemById(productId) ?: throw AppError.NotFoundError
        localDataSource.deleteCartItem(item)
    }

    override suspend fun updateQuantity(productId: String, newQuantity: Int) {
        val item = localDataSource.getCartItemById(productId) ?: throw AppError.NotFoundError
        localDataSource.updateCartItem(item.copy(quantity = newQuantity))
    }

    override suspend fun clearCart() {
        localDataSource.clearCartItem()
    }

}