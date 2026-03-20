package com.erichschnell.testingapp.cart.domain.usecase

import com.erichschnell.testingapp.cart.domain.ex.activeAt
import com.erichschnell.testingapp.cart.domain.repository.CartItemRepository
import com.erichschnell.testingapp.cart.presentation.model.CartItemWithPromotion
import com.erichschnell.testingapp.productList.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.productList.domain.repository.ProductRepository
import com.erichschnell.testingapp.productList.domain.repository.PromotionRepository
import com.erichschnell.testingapp.productList.domain.usecases.GetPromotionForProduct
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetCartItemsWithPromotionsUseCase @Inject constructor(
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProduct: GetPromotionForProduct
) {
    operator fun invoke(): Flow<List<CartItemWithPromotion>> {
        return cartItemRepository.getCartItems().flatMapLatest { cartItems ->
            val ids = cartItems.mapTo(mutableSetOf()) { it.productId }
            if (ids.isEmpty()){
                flowOf(emptyList())
            } else {
                combine(
                    productRepository.getProductsByIds(ids),
                    promotionRepository.getActivePromotions()
                ) { products, promotions ->
                    val activePromotions = promotions.activeAt(Instant.now())

                    val productsById = products.associateBy { it.id }

                    cartItems.mapNotNull {
                        val product = productsById[it.productId] ?: return@mapNotNull null
                        val promotion = getPromotionForProduct(product, activePromotions)
                        val productWithPromotion = ProductWithPromotion(product, promotion)
                        CartItemWithPromotion(it, productWithPromotion)
                    }

                }
            }
        }
    }
}