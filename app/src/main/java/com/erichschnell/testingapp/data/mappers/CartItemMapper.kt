package com.erichschnell.testingapp.data.mappers

import com.erichschnell.testingapp.data.local.database.entity.CartItemEntity
import com.erichschnell.testingapp.domain.models.CartItem

fun CartItemEntity.toDomain(): CartItem =
    CartItem(
        productId = productId,
        quantity = quantity,
    )

fun CartItem.toEntity(): CartItemEntity =
    CartItemEntity(
        productId = productId,
        quantity = quantity,
    )
