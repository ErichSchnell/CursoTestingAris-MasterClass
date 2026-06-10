package com.erichschnell.testingapp.domain.models

class ProductWithPromotion(
    val product: Product,
    val promotion: ProductPromotion? = null,
)

sealed interface ProductPromotion {
    data class Percent(
        val percent: Double,
        val discountedPrice: Double,
        val label: String,
    ) : ProductPromotion

    data class BuyXPayY(
        val buyX: Int,
        val payY: Int,
        val label: String,
    ) : ProductPromotion
}
