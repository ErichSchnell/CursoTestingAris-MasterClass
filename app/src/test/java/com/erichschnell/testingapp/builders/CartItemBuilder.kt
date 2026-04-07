package com.erichschnell.testingapp.builders

import com.erichschnell.testingapp.domain.models.CartItem

class CartItemBuilder (){
    private var productId: String = ""
    private var quantity: Int = 0

    fun withProductId(productId: String) = apply { this.productId = productId}
    fun withQuantity(quantity: Int) = apply { this.quantity = quantity}

    fun build () = CartItem(productId, quantity)
}

fun cartItem (block: CartItemBuilder.() -> Unit = {}) = CartItemBuilder().apply(block).build()