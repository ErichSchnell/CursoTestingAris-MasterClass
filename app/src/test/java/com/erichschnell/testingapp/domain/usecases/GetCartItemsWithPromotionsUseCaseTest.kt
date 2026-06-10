package com.erichschnell.testingapp.domain.usecases

import com.erichschnell.testingapp.core.builders.cartItem
import com.erichschnell.testingapp.core.builders.product
import com.erichschnell.testingapp.core.builders.promotion
import com.erichschnell.testingapp.fakes.FakeCartItemRepository
import com.erichschnell.testingapp.fakes.FakeProductRepository
import com.erichschnell.testingapp.fakes.FakePromotionRepository
import com.erichschnell.testingapp.fakes.FakeSystemClock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class GetCartItemsWithPromotionsUseCaseTest {
    private val clock = FakeSystemClock().apply { setTime(Instant.parse("2026-04-03T10:15:30.00Z")) }

    private fun useCase(
        cartRepository: FakeCartItemRepository = FakeCartItemRepository(),
        productRepository: FakeProductRepository = FakeProductRepository(),
        promotionRepository: FakePromotionRepository = FakePromotionRepository(),
        getPromotionForProduct: GetPromotionForProduct = GetPromotionForProduct(),
        clock: FakeSystemClock = this.clock,
    ) = GetCartItemsWithPromotionsUseCase(
        cartRepository = cartRepository,
        productRepository = productRepository,
        promotionRepository = promotionRepository,
        getPromotionForProduct = getPromotionForProduct,
        clock = clock,
    ).invoke()

    @Test
    fun `given cart with no items when invoke then it should return empty list`() =
        runTest {
            // Given
            val cartRepo = FakeCartItemRepository().apply { setCartItems(emptyList()) }

            // When
            val result = useCase(cartRepository = cartRepo).first()

            // Then
            assertTrue(result.isEmpty())
        }

    @Test
    fun `given existing cart item with active promotion when invoke then it should return cart item with promotion`() =
        runTest {
            // Given
            val now = clock.now()
            val product = product { withId("product-id") }
            val promotion =
                promotion {
                    withProductIds(listOf(product.id))
                    withStartTime(now.minusSeconds(10))
                    withEndTime(now.plusSeconds(10))
                }
            val cartItem = cartItem { withProductId(product.id) }

            val productRepo = FakeProductRepository().apply { setProducts(listOf(product)) }
            val promotionRepo = FakePromotionRepository().apply { setPromotions(listOf(promotion)) }
            val cartRepo = FakeCartItemRepository().apply { setCartItems(listOf(cartItem)) }

            // When
            val result =
                useCase(
                    cartRepository = cartRepo,
                    productRepository = productRepo,
                    promotionRepository = promotionRepo,
                ).first()

            // Then
            assertEquals(1, result.size)
            assertNotNull(result.first().item.promotion)
        }

    @Test
    fun `given cart item without matching product when invoke then skip item`() =
        runTest {
            // Given
            val productRepo = FakeProductRepository().apply { setProducts(listOf(product { withId("other-id") })) }
            val cartRepo = FakeCartItemRepository().apply { setCartItems(listOf(cartItem { withProductId("gosth-id") })) }

            // When
            val result =
                useCase(
                    cartRepository = cartRepo,
                    productRepository = productRepo,
                ).first()

            // Then
            assertTrue(result.isEmpty())
        }

    @Test
    fun `given promotion ending exactly now when invoke then it must be included in the result`() =
        runTest {
            // Given
            val now = clock.now()
            val product = product { withId("product-id") }
            val promotion =
                promotion {
                    withProductIds(listOf(product.id))
                    withStartTime(now.minusSeconds(10))
                    withEndTime(now)
                }
            val cartItem = cartItem { withProductId(product.id) }

            val productRepo = FakeProductRepository().apply { setProducts(listOf(product)) }
            val promotionRepo = FakePromotionRepository().apply { setPromotions(listOf(promotion)) }
            val cartRepo = FakeCartItemRepository().apply { setCartItems(listOf(cartItem)) }

            // When
            val result =
                useCase(
                    cartRepository = cartRepo,
                    productRepository = productRepo,
                    promotionRepository = promotionRepo,
                ).first()

            // Then
            assertEquals(1, result.size)
            assertNotNull(result.first().item.promotion)
        }

    @Test
    fun `given promotion ended when invoke then item remains but without promotion`() =
        runTest {
            // Given
            val now = clock.now()
            val product = product { withId("product-id") }
            val promotion =
                promotion {
                    withProductIds(listOf(product.id))
                    withStartTime(now.minusSeconds(10))
                    withEndTime(now.minusSeconds(1))
                }
            val cartItem = cartItem { withProductId(product.id) }

            val productRepo = FakeProductRepository().apply { setProducts(listOf(product)) }
            val promotionRepo = FakePromotionRepository().apply { setPromotions(listOf(promotion)) }
            val cartRepo = FakeCartItemRepository().apply { setCartItems(listOf(cartItem)) }

            // When
            val result =
                useCase(
                    cartRepository = cartRepo,
                    productRepository = productRepo,
                    promotionRepository = promotionRepo,
                ).first()

            // Then
            assertEquals(1, result.size)
            assertNull(result.first().item.promotion)
        }

    @Test
    fun `given active promotion when time advances then flow emits update list without promotion`() =
        runTest {
            // Given
            val now = clock.now()
            val product = product { withId("product-id") }
            val promotion =
                promotion {
                    withProductIds(listOf(product.id))
                    withStartTime(now.minusSeconds(10))
                    withEndTime(now.plusSeconds(5))
                }
            val cartItem = cartItem { withProductId(product.id) }

            val productRepo = FakeProductRepository().apply { setProducts(listOf(product)) }
            val promotionRepo = FakePromotionRepository().apply { setPromotions(listOf(promotion)) }
            val cartRepo = FakeCartItemRepository().apply { setCartItems(listOf(cartItem)) }

            val myUseCase =
                useCase(
                    cartRepository = cartRepo,
                    productRepository = productRepo,
                    promotionRepository = promotionRepo,
                )

            // When
            val firstEmission = myUseCase.first()
            assertNotNull(firstEmission.first().item.promotion)

            clock.advanceTime(6)

            val secondEmission = myUseCase.first()
            assertNull(secondEmission.first().item.promotion)
        }
}
