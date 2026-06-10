package com.erichschnell.testingapp.domain.usecases

import com.erichschnell.testingapp.core.builders.cartItem
import com.erichschnell.testingapp.core.builders.product
import com.erichschnell.testingapp.core.builders.promotion
import com.erichschnell.testingapp.domain.models.PromotionType
import com.erichschnell.testingapp.fakes.FakeCartItemRepository
import com.erichschnell.testingapp.fakes.FakeProductRepository
import com.erichschnell.testingapp.fakes.FakePromotionRepository
import com.erichschnell.testingapp.fakes.FakeSystemClock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant

class GetCartSummaryUseCaseTest {
    private lateinit var clock: FakeSystemClock
    private lateinit var cartRepo: FakeCartItemRepository
    private lateinit var productRepo: FakeProductRepository
    private lateinit var promotionRepo: FakePromotionRepository

    @Before
    fun setUp() {
        clock = FakeSystemClock().apply { setTime(Instant.parse("2026-04-03T10:15:30.00Z")) }
        cartRepo = FakeCartItemRepository()
        productRepo = FakeProductRepository()
        promotionRepo = FakePromotionRepository()
    }

    private fun useCase() =
        GetCartSummaryUseCase(
            cartRepository = cartRepo,
            productRepository = productRepo,
            promotionRepository = promotionRepo,
            getPromotionForProductUseCase = GetPromotionForProduct(),
            clock = clock,
        ).invoke()

    @Test
    fun `given percent promotion when invoke then calculate correctly`() =
        runTest {
            val product =
                product {
                    withId("product-id")
                    withPrice(100.0)
                }
            val promotion =
                promotion {
                    withProductIds(listOf(product.id))
                    withType(PromotionType.PERCENT)
                    withValue(10.0)
                    withStartTime(clock.now().minusSeconds(10))
                    withEndTime(clock.now().plusSeconds(10))
                }
            val cart =
                cartItem {
                    withProductId(product.id)
                    withQuantity(2)
                }

            productRepo.setProducts(listOf(product))
            promotionRepo.setPromotions(listOf(promotion))
            cartRepo.setCartItems(listOf(cart))

            val result = useCase().first()

            assertEquals(180.0, result.finalTotal, 0.001)
            assertEquals(20.0, result.discountsTotal, 0.001)
            assertEquals(200.0, result.subtotal, 0.001)
        }

    @Test
    fun `given 3 items in 2x1 promotion whe invoke then only discount 1 unit`() =
        runTest {
            val product =
                product {
                    withId("product-id")
                    withPrice(100.0)
                }
            val promotion =
                promotion {
                    withProductIds(listOf(product.id))
                    withType(PromotionType.BUY_X_PAY_Y)
                    withBuyQuantity(2)
                    withValue(1.0)
                    withStartTime(clock.now().minusSeconds(10))
                    withEndTime(clock.now().plusSeconds(10))
                }
            val cart =
                cartItem {
                    withProductId(product.id)
                    withQuantity(3)
                }

            productRepo.setProducts(listOf(product))
            promotionRepo.setPromotions(listOf(promotion))
            cartRepo.setCartItems(listOf(cart))

            val result = useCase().first()

            assertEquals(300.0, result.subtotal, 0.001)
            assertEquals(200.0, result.finalTotal, 0.001)
            assertEquals(100.0, result.discountsTotal, 0.001)
        }

    @Test
    fun `given multiple products with different promotions when invoke then calculate correctly`() =
        runTest {
            val p1 =
                product {
                    withId("p1")
                    withPrice(100.0)
                } // con promo
            val p2 =
                product {
                    withId("p2")
                    withPrice(50.0)
                } // sin promo

            val promotion =
                promotion {
                    withProductIds(listOf(p1.id))
                    withType(PromotionType.PERCENT)
                    withValue(10.0)
                    withStartTime(clock.now().minusSeconds(10))
                    withEndTime(clock.now().plusSeconds(10))
                }
            val carts =
                listOf(
                    cartItem {
                        withProductId(p1.id)
                        withQuantity(1)
                    },
                    cartItem {
                        withProductId(p2.id)
                        withQuantity(1)
                    },
                )

            productRepo.setProducts(listOf(p1, p2))
            promotionRepo.setPromotions(listOf(promotion))
            cartRepo.setCartItems(carts)

            val result = useCase().first()

            assertEquals(150.0, result.subtotal, 0.001)
            assertEquals(140.0, result.finalTotal, 0.001)
            assertEquals(10.0, result.discountsTotal, 0.001)
        }

    @Test
    fun `given expired promotion when invoke then discount not applied`() =
        runTest {
            val p1 =
                product {
                    withId("p1")
                    withPrice(100.0)
                }

            val promotion =
                promotion {
                    withProductIds(listOf(p1.id))
                    withType(PromotionType.PERCENT)
                    withValue(10.0)
                    withStartTime(clock.now().minusSeconds(10))
                    withEndTime(clock.now().minusSeconds(1))
                }
            val carts =
                listOf(
                    cartItem {
                        withProductId(p1.id)
                        withQuantity(1)
                    },
                )

            productRepo.setProducts(listOf(p1))
            promotionRepo.setPromotions(listOf(promotion))
            cartRepo.setCartItems(carts)

            val result = useCase().first()

            assertEquals(100.0, result.subtotal, 0.001)
            assertEquals(100.0, result.finalTotal, 0.001)
            assertEquals(0.0, result.discountsTotal, 0.001)
        }

    @Test
    fun `given active promotion when time advances then summary update automatically`() =
        runTest {
            val p1 =
                product {
                    withId("p1")
                    withPrice(100.0)
                }

            val promotion =
                promotion {
                    withProductIds(listOf(p1.id))
                    withType(PromotionType.PERCENT)
                    withValue(10.0)
                    withStartTime(clock.now().minusSeconds(5))
                    withEndTime(clock.now().plusSeconds(5))
                }
            val carts =
                listOf(
                    cartItem {
                        withProductId(p1.id)
                        withQuantity(1)
                    },
                )

            productRepo.setProducts(listOf(p1))
            promotionRepo.setPromotions(listOf(promotion))
            cartRepo.setCartItems(carts)

            val myUseCase = useCase()

            val firstRsult = myUseCase.first()
            assertEquals(100.0, firstRsult.subtotal, 0.001)
            assertEquals(90.0, firstRsult.finalTotal, 0.001)
            assertEquals(10.0, firstRsult.discountsTotal, 0.001)

            clock.advanceTime(6)

            val secondRsult = myUseCase.first()
            assertEquals(100.0, secondRsult.subtotal, 0.001)
            assertEquals(100.0, secondRsult.finalTotal, 0.001)
            assertEquals(0.0, secondRsult.discountsTotal, 0.001)
        }
}
