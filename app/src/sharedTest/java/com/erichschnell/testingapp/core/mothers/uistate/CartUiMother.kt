package com.erichschnell.testingapp.core.mothers.uistate

import com.erichschnell.testingapp.core.mothers.CartItemMother
import com.erichschnell.testingapp.core.mothers.ProductMother
import com.erichschnell.testingapp.domain.models.CartSummary
import com.erichschnell.testingapp.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.presentation.cart.model.CartItemWithPromotion
import com.erichschnell.testingapp.presentation.cart.model.CartUiState

object CartUiMother {
    fun success(
        cartItems: List<CartItemWithPromotion> =
            listOf(
                CartItemWithPromotion(
                    cartItem = CartItemMother.bread(),
                    item = ProductWithPromotion(ProductMother.bread()),
                ),
                CartItemWithPromotion(
                    cartItem = CartItemMother.eggs(),
                    item = ProductWithPromotion(ProductMother.eggs()),
                ),
                CartItemWithPromotion(
                    cartItem = CartItemMother.milk(),
                    item = ProductWithPromotion(ProductMother.milk()),
                ),
                CartItemWithPromotion(
                    cartItem = CartItemMother.soda(),
                    item = ProductWithPromotion(ProductMother.soda()),
                ),
            ),
        summary: CartSummary = CartSummary(100.0, 10.0, 90.0),
    ) = CartUiState.Success(
        cartItems = cartItems,
        summary = summary,
    )
}
