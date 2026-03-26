package com.erichschnell.testingapp.data.mappers

import com.erichschnell.testingapp.data.local.database.entity.CartItemEntity
import com.erichschnell.testingapp.domain.models.CartItem

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