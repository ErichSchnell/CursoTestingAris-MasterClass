package com.erichschnell.testingapp.domain.usecases

import com.erichschnell.testingapp.core.builders.product
import com.erichschnell.testingapp.core.builders.promotion
import com.erichschnell.testingapp.fakes.FakeProductRepository
import com.erichschnell.testingapp.fakes.FakePromotionRepository
import com.erichschnell.testingapp.fakes.FakeSystemClock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetProductDetailWithPromotionUseCaseTest {

    private lateinit var productRepo: FakeProductRepository
    private lateinit var promotionRepo: FakePromotionRepository
    private lateinit var clock: FakeSystemClock

    @Before
    fun setUp() {
        productRepo = FakeProductRepository()
        promotionRepo = FakePromotionRepository()
        clock = FakeSystemClock()
    }

    private fun useCase() = GetProductDetailWithPromotionUseCase(
        productRepository = productRepo,
        promotionRepository = promotionRepo,
        getPromotionForProduct = GetPromotionForProduct(),
        clock = clock
    )

    @Test
    fun `given existing product with active promotion when invoke then return product with promotion`() = runTest {
        val product = product { withId("product-id") }
        val promotion = promotion {
            withProductIds(listOf(product.id))
            withId("promo-id")
            withStartTime(clock.now().minusSeconds(10))
            withEndTime(clock.now().plusSeconds(10))
        }

        productRepo.setProducts(listOf(product))
        promotionRepo.setPromotions(listOf(promotion))

        val result = useCase()(product.id).first()

        assertNotNull(result)
        assertEquals(product.id, result?.product?.id)
        assertNotNull(result?.promotion)
    }

    @Test
    fun `given existing product without promotion when invoke then return product without promotion`() = runTest {
        val product = product { withId("product-id") }
        val promotion = promotion { withProductIds(listOf("other-id")) }

        productRepo.setProducts(listOf(product))
        promotionRepo.setPromotions(listOf(promotion))

        val result = useCase()(product.id).first()

        assertNotNull(result)
        assertEquals(product.id, result?.product?.id)
        assertNull(result?.promotion)
    }

    @Test
    fun `given existing product with expired promotion when invoke then return product without promotion`() = runTest {
        val product = product { withId("product-id") }
        val promotion = promotion {
            withProductIds(listOf(product.id))
            withId("promo-id")
            withStartTime(clock.now().minusSeconds(10))
            withEndTime(clock.now().minusSeconds(1))
        }

        productRepo.setProducts(listOf(product))
        promotionRepo.setPromotions(listOf(promotion))

        val result = useCase()(product.id).first()

        assertNotNull(result)
        assertEquals(product.id, result?.product?.id)
        assertNull(result?.promotion)
    }

    @Test
    fun `given existing product with promotion when invoke and promotion expired then return product without promotion`() = runTest {
        val product = product { withId("product-id") }
        val promotion = promotion {
            withProductIds(listOf(product.id))
            withId("promo-id")
            withStartTime(clock.now().minusSeconds(10))
            withEndTime(clock.now().plusSeconds(5))
        }

        productRepo.setProducts(listOf(product))
        promotionRepo.setPromotions(listOf(promotion))

        val flowUseCase = useCase()(product.id)

        val firstResult = flowUseCase.first()
        assertNotNull(firstResult)
        assertEquals(product.id, firstResult?.product?.id)
        assertNotNull(firstResult?.promotion)

        clock.advanceTime(6)

        val secondResult = flowUseCase.first()
        assertNotNull(secondResult)
        assertEquals(product.id, secondResult?.product?.id)
        assertNull(secondResult?.promotion)
    }

    @Test
    fun `given non-existing product when invoke then return null`() = runTest {
        productRepo.setProducts(emptyList())

        val result = useCase()("product.id").first()

        assertNull(result)
    }
}