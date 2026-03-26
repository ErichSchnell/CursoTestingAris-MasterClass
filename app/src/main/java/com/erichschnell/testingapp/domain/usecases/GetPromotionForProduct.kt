package com.erichschnell.testingapp.domain.usecases

import com.erichschnell.testingapp.presentation.core.ex.roundTo2Decimals
import com.erichschnell.testingapp.domain.models.Product
import com.erichschnell.testingapp.domain.models.ProductPromotion
import com.erichschnell.testingapp.domain.models.Promotion
import com.erichschnell.testingapp.domain.models.PromotionType
import javax.inject.Inject
import kotlin.collections.filter

class GetPromotionForProduct @Inject constructor() {
    operator fun invoke( product: Product, activePromotions: List<Promotion>): ProductPromotion? {
        val productPromos = activePromotions.filter { it.productIds.contains(product.id) }

        val percentPromo = productPromos.filter { it.type == PromotionType.PERCENT }
            .maxByOrNull { it.value }

        if(percentPromo != null) {
            val percent = percentPromo.value.coerceIn(0.0, 100.0)
            val discountedPrice = (product.price * (1 - percent / 100.0)).roundTo2Decimals()
            return ProductPromotion.Percent(percent, discountedPrice, "-${percent.toInt()}%")
        }

        val buyPayPromo = productPromos.firstOrNull { it.type == PromotionType.BUY_X_PAY_Y }

        if (buyPayPromo != null) {
            val buyX = buyPayPromo.buyQuantity ?: return null
            val payY = buyPayPromo.value.toInt().coerceIn(0, buyX)
            return ProductPromotion.BuyXPayY(
                buyX,
                payY,
                "${buyX}x$payY"
            )
        }

        return null
    }
}