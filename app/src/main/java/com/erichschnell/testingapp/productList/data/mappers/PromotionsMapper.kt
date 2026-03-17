package com.erichschnell.testingapp.productList.data.mappers

import android.util.Log
import com.erichschnell.testingapp.productList.data.local.database.entity.PromotionEntity
import com.erichschnell.testingapp.productList.data.remote.reponse.PromotionResponse
import com.erichschnell.testingapp.productList.domain.models.Promotion
import com.erichschnell.testingapp.productList.domain.models.PromotionType
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.sql.Date
import java.time.Instant

fun PromotionEntity.toDomain(json: Json): Promotion? {

    val decodedProductIds = runCatching {
        json.decodeFromString(
            ListSerializer(String.serializer()), productIds
        )
    }.getOrNull()

    val finalType = runCatching {
        PromotionType.valueOf(
            type.trim().uppercase()
        )
    }.getOrNull()

    if (finalType == null || decodedProductIds == null) return null

    val finalOfferValue = when (finalType) {
        PromotionType.PERCENT -> percent
        PromotionType.BUY_X_PAY_Y -> payY
    }?.toDouble()

    finalOfferValue ?: return null

    return Promotion(
        id = id,
        productIds = decodedProductIds,
        type = finalType,
        value = finalOfferValue,
        buyQuantity = buyX,
        startTime = Instant.ofEpochSecond(startAtEpoch),
        endTime = Instant.ofEpochSecond(endAtEpoch)
    )
}

fun PromotionResponse.toEntity(json: Json): PromotionEntity? {

    if (startAtEpoch == null || endAtEpoch == null) return null

    val productIds = listOf(productId)
    val productIdsJson = json.encodeToString(
        serializer = ListSerializer(String.serializer()), value = productIds
    )

    return PromotionEntity(
        id = id,
        productIds = productIdsJson,
        type = type,
        percent = percent,
        buyX = buyX,
        payY = payY,
        startAtEpoch = startAtEpoch,
        endAtEpoch = endAtEpoch
    )
}