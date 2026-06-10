package com.erichschnell.testingapp.presentation.cart.model

sealed interface CartAction {
    data class IncreaseQuantity(
        val productId: String,
        val quantity: Int,
    ) : CartAction

    data class DecreaseQuantity(
        val productId: String,
        val quantity: Int,
    ) : CartAction

    data class RemoveCartItem(
        val productId: String,
    ) : CartAction
}
