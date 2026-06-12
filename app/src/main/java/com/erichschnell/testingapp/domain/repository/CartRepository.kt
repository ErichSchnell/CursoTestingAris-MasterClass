package com.erichschnell.testingapp.domain.repository

import com.erichschnell.testingapp.domain.models.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>

    suspend fun getCartItemById(productId: String): CartItem?

    suspend fun addToCart(
        productId: String,
        quantity: Int,
    )

    suspend fun removeCartItem(productId: String)

    suspend fun updateQuantity(
        productId: String,
        newQuantity: Int,
    )

    suspend fun clearCart()
}
