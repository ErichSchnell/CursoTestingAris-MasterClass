package com.erichschnell.testingapp.domain.usecases

import com.erichschnell.testingapp.core.builders.product
import com.erichschnell.testingapp.core.builders.promotion
import com.erichschnell.testingapp.domain.models.ProductPromotion
import com.erichschnell.testingapp.domain.models.PromotionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GetPromotionForProductTest {
    private val useCase = GetPromotionForProduct()

    @Test
    fun `given no promotions when invoke then return null`() {
        // Given
        val product = product()

        // When
        val result = useCase(product, emptyList())

        // Then
        assertNull(result)
    }

    @Test
    fun `given percent promotion when invoke then return discounted price rounded to 2 decimals`() {
        // Given
        val product =
            product {
                withId("product-id")
                withPrice(10.0)
            }
        val promotion =
            promotion {
                withId("promotion-id")
                withType(PromotionType.PERCENT)
                withProductIds(listOf(product.id))
                withValue(15.0)
            }

        // When
        val response = useCase(product, listOf(promotion))

        // Then
        assertTrue(response is ProductPromotion.Percent)
        response as ProductPromotion.Percent
        assertEquals(8.50, response.discountedPrice, 0.001)
        assertEquals(15.0, response.percent, 0.001)
    }

    @Test
    fun `given same product with percent and buyPayPromo promotion when invoke then return ProductPromotion-Percent`() {
        // Given
        val product =
            product {
                withId("product-id")
                withPrice(10.0)
            }
        val promotionPercent =
            promotion {
                withId("promotion-percent")
                withType(PromotionType.PERCENT)
                withProductIds(listOf(product.id))
                withValue(15.0)
            }
        val promotionButPayPromo =
            promotion {
                withId("promotion-buy-pay-promo")
                withType(PromotionType.BUY_X_PAY_Y)
                withProductIds(listOf(product.id))
                withValue(1.0)
                withBuyQuantity(2)
            }

        // When
        val response = useCase(product, listOf(promotionPercent, promotionButPayPromo))

        // Then
        assertTrue(response is ProductPromotion.BuyXPayY)
        response as ProductPromotion.BuyXPayY
        assertEquals(2, response.buyX)
        assertEquals(1, response.payY)
        assertEquals("2x1", response.label)
    }

    @Test
    fun `given multiple percent promotions when invoke then return highest discounted`() {
        // Given
        val product =
            product {
                withId("product-id")
                withPrice(10.0)
            }
        val promotionPercent1 =
            promotion {
                withId("promotion-percent-1")
                withType(PromotionType.PERCENT)
                withProductIds(listOf(product.id))
                withValue(15.0)
            }
        val promotionPercent2 =
            promotion {
                withId("promotion-percent-2")
                withType(PromotionType.PERCENT)
                withProductIds(listOf(product.id))
                withValue(50.0)
            }

        // When
        val response = useCase(product, listOf(promotionPercent1, promotionPercent2))

        // Then
        assertTrue(response is ProductPromotion.Percent)
        response as ProductPromotion.Percent
        assertEquals(5.0, response.discountedPrice, 0.001)
        assertEquals(50.0, response.percent, 0.001)
    }

    @Test
    fun `given buyQuantity null when invoke then return null`() {
        // Given
        val product =
            product {
                withId("product-id")
                withPrice(10.0)
            }
        val promotionPercent =
            promotion {
                withType(PromotionType.PERCENT)
                withProductIds(listOf(product.id))
                withValue(50.0)
            }
        val promotionBroken =
            promotion {
                withType(PromotionType.BUY_X_PAY_Y)
                withProductIds(listOf(product.id))
                withBuyQuantity(null)
            }

        // When
        val response = useCase(product, listOf(promotionPercent, promotionBroken))

        // Then
        assertNull(response)
    }
}
