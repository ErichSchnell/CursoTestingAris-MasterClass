package com.erichschnell.testingapp.productList.domain.usecases

import com.erichschnell.testingapp.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCartItemsQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(): Flow<Int> {
        return cartRepository.getCartItems().map { cartItems ->
            if (cartItems.isEmpty()){
                return@map 0
            }
            cartItems.sumOf { it.quantity }
        }
    }
}