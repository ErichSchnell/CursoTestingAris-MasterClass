package com.erichschnell.testingapp.cart.domain.repository

import com.erichschnell.testingapp.cart.domain.models.CartItem
import kotlinx.coroutines.flow.Flow

interface CartItemRepository {
    fun getCartItems(): Flow<List<CartItem>>

    suspend fun addToCart(productId: String, quantity: Int)
    suspend fun removeCartItem(productId: String)
    suspend fun updateQuantity(productId: String, newQuantity: Int)
    suspend fun clearCart()
}