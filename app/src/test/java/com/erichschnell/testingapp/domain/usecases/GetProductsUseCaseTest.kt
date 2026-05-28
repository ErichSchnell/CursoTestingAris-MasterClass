package com.erichschnell.testingapp.domain.usecases

import com.erichschnell.testingapp.core.builders.product
import com.erichschnell.testingapp.core.builders.promotion
import com.erichschnell.testingapp.fakes.FakeProductRepository
import com.erichschnell.testingapp.fakes.FakePromotionRepository
import com.erichschnell.testingapp.fakes.FakeSettingRepository
import com.erichschnell.testingapp.fakes.FakeSystemClock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class GetProductsUseCaseTest {

    private fun useCase(
        products: FakeProductRepository = FakeProductRepository(),
        promos: FakePromotionRepository = FakePromotionRepository(),
        setting: FakeSettingRepository = FakeSettingRepository(),
        clock: FakeSystemClock = FakeSystemClock()
    ) = GetProductsUseCase(
        productRepository = products,
        promotionRepository = promos,
        getPromotionForProduct = GetPromotionForProduct(),
        settingsRepository = setting,
        clock = clock
    ).invoke()

    @Test
    fun `given promotion ending now when invoke then it should be included in the result`() = runTest {
        //Given
        val now = Instant.parse("2026-04-03T10:15:30.00Z")
        val clock = FakeSystemClock().apply { setTime(now) }

        val product = product { withId("product-id") }
        val promotion = promotion {
            withProductIds(listOf(product.id))
            withStartTime(now.minusSeconds(60))
            withEndTime(now)
        }

        val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }
        val promotionRepository = FakePromotionRepository().apply { setPromotions(listOf(promotion)) }

        //When
        val result = (useCase(
            products = productRepository,
            promos = promotionRepository,
            clock = clock
        )).first()

        //Then
        assertEquals(1, result.size)
        assertEquals(product.id, result.first().product.id)
    }

    @Test
    fun `given active promotion when time advances then promotion no be longer be returned`() = runTest {
        //Given
        val now = Instant.parse("2026-04-03T10:15:30.00Z")
        val clock = FakeSystemClock().apply { setTime(now) }

        val product = product { withId("product-id") }
        val promotion = promotion {
            withProductIds(listOf(product.id))
            withStartTime(now)
            withEndTime(now.plusSeconds(5))
        }

        val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }
        val promotionRepository = FakePromotionRepository().apply { setPromotions(listOf(promotion)) }

        //When
        val firstResult = (useCase(
            products = productRepository,
            promos = promotionRepository,
            clock = clock
        )).first()
        clock.advanceTime(6)
        val secondResult = (useCase(
            products = productRepository,
            promos = promotionRepository,
            clock = clock
        )).first()

        //Then
        assertNotNull(firstResult.first().promotion)
        assertNull(secondResult.first().promotion)
    }

    @Test
    fun `given show promotion in stock only when stock is 0 then promotion should not be returned`() = runTest {
        //Given
        val product = product { withId("product-id"); withStock(0) }
        val setting = FakeSettingRepository().apply { setInStockOnly(true) }

        //When
        val result = useCase(setting = setting).first()

        //Then
        assertTrue(result.isEmpty())
    }
}