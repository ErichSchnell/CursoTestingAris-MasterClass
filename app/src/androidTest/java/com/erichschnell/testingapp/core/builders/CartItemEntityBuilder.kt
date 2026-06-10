package com.erichschnell.testingapp.core.builders

import com.erichschnell.testingapp.data.local.database.entity.CartItemEntity

class CartItemEntityBuilder {
    private var productId: String = ""
    private var quantity: Int = 0

    fun withProductId(productId: String) = apply { this.productId = productId }

    fun withQuantity(quantity: Int) = apply { this.quantity = quantity }

    fun build() = CartItemEntity(productId, quantity)
}

fun cartItemEntity(block: CartItemEntityBuilder.() -> Unit = {}) = CartItemEntityBuilder().apply(block).build()
