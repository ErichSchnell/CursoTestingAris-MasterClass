package com.erichschnell.testingapp.productList.domain.usecases

import com.erichschnell.testingapp.cart.domain.ex.activeAt
import com.erichschnell.testingapp.productList.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.productList.domain.repository.ProductRepository
import com.erichschnell.testingapp.productList.domain.repository.PromotionRepository
import com.erichschnell.testingapp.productList.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProduct: GetPromotionForProduct,
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<List<ProductWithPromotion>> {
        return combine(
            productRepository.getProducts(),
            promotionRepository.getActivePromotions(),
            settingsRepository.inStockOnly
        ) { products, promotions, inStockOnly ->

            val activePromotions = promotions.activeAt(Instant.now())

            val filteredProducts = if (inStockOnly) {
                products.filter { it.stock > 0 }
            } else {
                products
            }


            filteredProducts.map { product ->
                val promotion = getPromotionForProduct(product, activePromotions)
                ProductWithPromotion(product, promotion)
            }
        }
    }
}