package com.erichschnell.testingapp.cart.domain.usecase

import com.erichschnell.testingapp.cart.domain.repository.CartRepository
import com.erichschnell.testingapp.core.domain.model.AppError
import com.erichschnell.testingapp.productList.domain.repository.ProductRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String, newQuantity: Int) {
        if (newQuantity < 0) throw AppError.Validation.QuantityMustBePositive
        if (newQuantity == 0){
            cartRepository.removeCartItem(productId)
            return
        }

        val product = productRepository.getProductById(productId).first() ?: throw AppError.NotFoundError

        if (newQuantity > product.stock) throw AppError.Validation.InsufficientStock(product.stock)

        cartRepository.updateQuantity(productId, newQuantity)
    }
}