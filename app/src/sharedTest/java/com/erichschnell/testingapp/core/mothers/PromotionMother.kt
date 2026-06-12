package com.erichschnell.testingapp.core.mothers

import com.erichschnell.testingapp.domain.models.ProductPromotion

class PromotionMother {
    fun percent(
        percent: Double = 10.0,
        discountPrice: Double = 100.0,
        label: String = "10% descuento",
    ) = ProductPromotion.Percent(
        percent = percent,
        discountedPrice = discountPrice,
        label = label,
    )

    fun buyXPayY(
        buyX: Int = 2,
        payY: Int = 1,
        label: String = "2x1",
    ) = ProductPromotion.BuyXPayY(
        buyX = buyX,
        payY = payY,
        label = label,
    )
}
