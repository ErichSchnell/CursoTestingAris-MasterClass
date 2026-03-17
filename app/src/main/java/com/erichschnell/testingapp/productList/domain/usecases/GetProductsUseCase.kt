package com.erichschnell.testingapp.productList.domain.usecases

import android.util.Log
import com.erichschnell.testingapp.productList.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.productList.domain.repository.ProductRepository
import com.erichschnell.testingapp.productList.domain.repository.PromotionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProduct: GetPromotionForProduct,
) {
    operator fun invoke(): Flow<List<ProductWithPromotion>> {
        return combine(
            productRepository.getProducts(),
            promotionRepository.getActivePromotions()
        ) { products, promotions ->

            val now = Instant.now()
            val activePromotions = promotions.filter { it.startTime <= now &&
                it.endTime >= now }

            products.map { product ->
                val promotion = getPromotionForProduct(product, activePromotions)
                ProductWithPromotion(product, promotion)
            }
        }
    }
}