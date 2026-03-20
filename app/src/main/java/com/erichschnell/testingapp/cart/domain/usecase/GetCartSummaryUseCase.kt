package com.erichschnell.testingapp.cart.domain.usecase

import com.erichschnell.testingapp.cart.domain.ex.activeAt
import com.erichschnell.testingapp.cart.domain.models.CartItem
import com.erichschnell.testingapp.cart.domain.models.CartSummary
import com.erichschnell.testingapp.cart.domain.repository.CartItemRepository
import com.erichschnell.testingapp.productList.domain.models.Product
import com.erichschnell.testingapp.productList.domain.models.ProductPromotion
import com.erichschnell.testingapp.productList.domain.models.Promotion
import com.erichschnell.testingapp.productList.domain.repository.ProductRepository
import com.erichschnell.testingapp.productList.domain.repository.PromotionRepository
import com.erichschnell.testingapp.productList.domain.usecases.GetPromotionForProduct
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.Instant
import javax.inject.Inject

class GetCartSummaryUseCase @Inject constructor(
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProductUseCase: GetPromotionForProduct
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<CartSummary> {

        return cartItemRepository.getCartItems()
            .flatMapLatest { cartItems ->
                val ids = cartItems.mapTo(mutableSetOf()) { it.productId }
                if (ids.isEmpty()) flowOf(CartSummary(0.0,0.0,0.0))
                else {
                    combine(
                        productRepository.getProductsByIds(ids),
                        promotionRepository.getActivePromotions()
                    ) { products, promotions ->
                        calculateSummary(cartItems, products, promotions)
                    }
                }
            }
    }

    private fun calculateSummary(
        cartItems: List<CartItem>,
        products: List<Product>,
        promotions: List<Promotion>
    ): CartSummary{
        val activePromotions = promotions.activeAt(Instant.now())

        val productsById = products.associateBy { it.id }
        var subtotal = 0.0
        var discountTotal = 0.0

        for(cartItem in cartItems){
            val product = productsById[cartItem.productId] ?: continue
            val itemTotal = product.price * cartItem.quantity
            subtotal += itemTotal

            discountTotal += calculateDiscountForProduct(
                product,
                cartItem.quantity,
                activePromotions
            )
        }
        val total = (subtotal - discountTotal).coerceAtLeast(0.0)

        return CartSummary(
            subtotal = subtotal,
            discountsTotal = discountTotal,
            finalTotal = total
        )
    }

    private fun calculateDiscountForProduct(
        product: Product,
        quantity: Int,
        activePromotions: List<Promotion>,
    ): Double {
        val selectedPromotion = getPromotionForProductUseCase(product, activePromotions)

        return when(selectedPromotion){
            is ProductPromotion.BuyXPayY -> {
                val buy = selectedPromotion.buyX
                val pay = selectedPromotion.payY
                val freePerGroup = (buy - pay).coerceAtLeast(0)

                val groups = quantity / buy
                val freeItems = groups * freePerGroup

                product.price * freeItems
            }
            is ProductPromotion.Percent -> {
                val itemSubTotal = product.price * quantity
                itemSubTotal * (selectedPromotion.percent / 100)
            }
            null -> 0.0
        }

    }
}