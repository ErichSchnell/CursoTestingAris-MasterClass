package com.erichschnell.testingapp.presentation.cart.model

import com.erichschnell.testingapp.domain.models.CartSummary

sealed class CartUiState{

    data class Success(
        val summary: CartSummary,
        val cartItems: List<CartItemWithPromotion>,
        val isLoading: Boolean = true
    ): CartUiState()

    data class Error(val message: String): CartUiState()

    data object Loading: CartUiState()


}