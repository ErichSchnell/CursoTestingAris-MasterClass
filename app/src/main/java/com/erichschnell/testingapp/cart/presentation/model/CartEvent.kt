package com.erichschnell.testingapp.cart.presentation.model

sealed interface CartEvent {
    data class ShowMessage(val message: String) : CartEvent

    data object Input {
        data class IncreaseQuantity(val productId: String, val quantity: Int) : CartEvent
        data class DecreaseQuantity(val productId: String, val quantity: Int) : CartEvent

    }

    data object Action {
        data class RemoveCartItem(val productId: String) : CartEvent
    }
}