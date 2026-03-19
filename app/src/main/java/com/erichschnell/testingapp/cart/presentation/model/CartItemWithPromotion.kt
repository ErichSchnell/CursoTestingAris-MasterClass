package com.erichschnell.testingapp.cart.presentation.model

import com.erichschnell.testingapp.cart.domain.models.CartItem
import com.erichschnell.testingapp.productList.domain.models.Product
import com.erichschnell.testingapp.productList.domain.models.Promotion

data class CartItemWithPromotion(
    val cartItem: CartItem,
    val product: Product
)