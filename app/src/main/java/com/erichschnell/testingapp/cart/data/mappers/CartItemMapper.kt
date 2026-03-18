package com.erichschnell.testingapp.cart.data.mappers

import com.erichschnell.testingapp.cart.data.local.database.entity.CartItemEntity
import com.erichschnell.testingapp.cart.domain.models.CartItem

fun CartItemEntity.toDomain(): CartItem{
    return CartItem(
        productId = productId,
        quantity = quantity
    )
}

fun CartItem.toEntity(): CartItemEntity{
    return CartItemEntity(
        productId = productId,
        quantity = quantity
    )
}