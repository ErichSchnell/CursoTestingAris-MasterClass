package com.erichschnell.testingapp.cart.presentation.model

import com.erichschnell.testingapp.cart.domain.models.CartItem
import com.erichschnell.testingapp.productList.domain.models.ProductWithPromotion

data class CartItemWithPromotion(
    val cartItem: CartItem,
    val item: ProductWithPromotion
)