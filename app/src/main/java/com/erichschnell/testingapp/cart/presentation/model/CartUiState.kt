package com.erichschnell.testingapp.cart.presentation.model

import com.erichschnell.testingapp.cart.domain.models.CartSummary

sealed class CartUiState{

    data class Success(
        val summary: CartSummary,
        val cartItems: List<CartItemWithPromotion>,
        val isLoading: Boolean = true
    ): CartUiState()

    data class Error(val message: String): CartUiState()

    data object Loading: CartUiState()


}