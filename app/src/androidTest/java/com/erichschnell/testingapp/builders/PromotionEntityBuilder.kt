package com.erichschnell.testingapp.builders

import com.erichschnell.testingapp.data.local.database.entity.PromotionEntity
import com.erichschnell.testingapp.domain.models.Promotion
import com.erichschnell.testingapp.domain.models.PromotionType
import java.time.Instant

class PromotionEntityBuilder {
    private var id: String = "promo-id"
    private var productIds: String = ""
    private var type: String = PromotionType.PERCENT.name
    private var percent: Int? = null
    private var buyX: Int? = null
    private var payY: Int? = null
    private var startAtEpoch: Long = 0
    private var endAtEpoch: Long = 0

    fun withId(id: String) = apply { this.id = id}
    fun withProductIds(productIds: String) =  apply { this.productIds = productIds}
    fun withType(type: String) = apply { this.type = type}
    fun withPercent(percent: Int?) = apply { this.percent = percent}
    fun withBuyX(buyX: Int?) = apply { this.buyX = buyX}
    fun withPayY(payY: Int?) =  apply { this.payY = payY}
    fun withStartAtEpoch(startAtEpoch: Long) = apply { this.startAtEpoch = startAtEpoch}
    fun withEndAtEpoch(endAtEpoch: Long) = apply { this.endAtEpoch = endAtEpoch}

    fun build() = PromotionEntity(
        id = id,
        productIds = productIds,
        type = type,
        percent = percent,
        buyX = buyX,
        payY = payY,
        startAtEpoch = startAtEpoch,
        endAtEpoch = endAtEpoch
    )
}

fun promotionEntity (block: PromotionEntityBuilder.() -> Unit = {}) = PromotionEntityBuilder().apply(block).build()