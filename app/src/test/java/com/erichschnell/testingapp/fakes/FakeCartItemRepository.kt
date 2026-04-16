package com.erichschnell.testingapp.fakes

import com.erichschnell.testingapp.domain.core.model.AppError
import com.erichschnell.testingapp.domain.models.CartItem
import com.erichschnell.testingapp.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeCartRepository: CartRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    fun setCartItems(cartItems: List<CartItem>){
        _cartItems.value = cartItems
    }


    override fun getCartItems(): Flow<List<CartItem>> = _cartItems.asStateFlow()

    override suspend fun getCartItemById(productId: String): CartItem? = _cartItems.value.find { it.productId == productId }

    override suspend fun addToCart(productId: String, quantity: Int) {
        val cartItemsMutable = _cartItems.value.toMutableList()
        val existingItems = _cartItems.value.indexOfFirst { it.productId == productId }
        if (existingItems >= 0){
            val item = cartItemsMutable[existingItems]
            cartItemsMutable[existingItems] = item.copy(quantity = item.quantity + quantity)
        } else {
            cartItemsMutable.add(CartItem(productId, quantity))
        }
        _cartItems.value = cartItemsMutable
    }

    override suspend fun removeCartItem(productId: String) {
        val cartItemsMutable = _cartItems.value.toMutableList()
        val existingItems = _cartItems.value.indexOfFirst { it.productId == productId }
        if (existingItems >= 0){
            cartItemsMutable.removeAt(existingItems)
            _cartItems.value = cartItemsMutable
        } else {
            throw AppError.NotFoundError
        }
    }

    override suspend fun updateQuantity(productId: String, newQuantity: Int) {
        val cartItemsMutable = _cartItems.value.toMutableList()
        val existingItems = _cartItems.value.indexOfFirst { it.productId == productId }
        if (existingItems >= 0){
            cartItemsMutable[existingItems] = cartItemsMutable[existingItems].copy(quantity = newQuantity)
            _cartItems.value = cartItemsMutable
        } else {
            throw AppError.NotFoundError
        }
    }

    override suspend fun clearCart() {
        _cartItems.value = emptyList()
    }

}