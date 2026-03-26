package com.erichschnell.testingapp.presentation.cart.model

import com.erichschnell.testingapp.domain.models.CartItem
import com.erichschnell.testingapp.domain.models.ProductWithPromotion

data class CartItemWithPromotion(
    val cartItem: CartItem,
    val item: ProductWithPromotion
)