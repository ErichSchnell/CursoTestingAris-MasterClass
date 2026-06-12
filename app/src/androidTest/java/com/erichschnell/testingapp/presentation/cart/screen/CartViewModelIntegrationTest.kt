package com.erichschnell.testingapp.presentation.cart.screen

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.erichschnell.testingapp.core.MainDispatcherRule
import com.erichschnell.testingapp.core.mockwebserver.MiniMarketApiDispatcher
import com.erichschnell.testingapp.core.mockwebserver.MockWebServerUrlHolder
import com.erichschnell.testingapp.core.mockwebserver.rules.MockWebServerRule
import com.erichschnell.testingapp.core.utils.asAsset
import com.erichschnell.testingapp.domain.repository.CartRepository
import com.erichschnell.testingapp.domain.repository.ProductRepository
import com.erichschnell.testingapp.domain.repository.PromotionRepository
import com.erichschnell.testingapp.domain.usecases.GetCartItemsWithPromotionsUseCase
import com.erichschnell.testingapp.domain.usecases.GetCartSummaryUseCase
import com.erichschnell.testingapp.domain.usecases.GetPromotionForProduct
import com.erichschnell.testingapp.domain.usecases.UpdateCartItemUseCase
import com.erichschnell.testingapp.domain.util.Clock
import com.erichschnell.testingapp.presentation.cart.model.CartAction
import com.erichschnell.testingapp.presentation.cart.model.CartUiState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CartViewModelIntegrationTest {
    private companion object {
        const val PRODUCT_ID = "p2"
        const val INITIAL_QUANTITY = 1
        const val UPDATE_QUANTITY = 2
    }

    @get:Rule(order = 0)
    val mockWebServer = MockWebServerRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val mainDispatcherRule = MainDispatcherRule()

    @Inject
    lateinit var cartRepository: CartRepository

    @Inject
    lateinit var getCartSummaryUseCase: GetCartSummaryUseCase

    @Inject
    lateinit var productRepository: ProductRepository

    @Inject
    lateinit var promotionRepository: PromotionRepository

    @Inject
    lateinit var getPromotionForProductUseCase: GetPromotionForProduct

    @Inject
    lateinit var clock: Clock

    @Inject
    lateinit var updateCartItemUseCase: UpdateCartItemUseCase

    @Inject
    lateinit var getCartItemsWithPromotionsUseCase: GetCartItemsWithPromotionsUseCase

    @Before
    fun setUp() =
        runTest {
            mockWebServer.server.dispatcher =
                MiniMarketApiDispatcher(
                    productJson = "product_list_default.json".asAsset(),
                )
            hiltRule.inject()

            cartRepository.clearCart()

            productRepository.refreshProduct()
            promotionRepository.refreshPromotions()
        }

    @After
    fun tearDown() {
        MockWebServerUrlHolder.baseUrl = "http://localhost:8080/"
    }

    @Test
    fun givenCartWithItems_whenViewModelCollectsUiState_thenSuccessWithSummary() =
        runTest {
            cartRepository.addToCart(PRODUCT_ID, UPDATE_QUANTITY)

            val viewModel = createViewModel()

            viewModel.uiState.test {
                val result =
                    awaitSuccessMatching {
                        summary.finalTotal > 0 &&
                            cartItems.isNotEmpty()
                    }

                assertTrue(result.cartItems.size == 1)
                assertTrue(result.summary.finalTotal > 0)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun givenSingleProduct_whenIncreaseQuantity_thenCartHasIncreasedQuantity() =
        runTest {
            cartRepository.addToCart(PRODUCT_ID, INITIAL_QUANTITY)

            val viewModel = createViewModel()

            viewModel.uiState.test {
                val result =
                    awaitSuccessMatching {
                        cartItems.any {
                            it.cartItem.productId == PRODUCT_ID &&
                                it.cartItem.quantity == INITIAL_QUANTITY
                        }
                    }
                assertTrue(result.cartItems.size == 1)
                assertTrue(
                    result.cartItems
                        .first()
                        .cartItem.quantity == INITIAL_QUANTITY,
                )

                viewModel.onAction(CartAction.IncreaseQuantity(PRODUCT_ID, INITIAL_QUANTITY))

                val newResult =
                    awaitSuccessMatching {
                        cartItems.any {
                            it.cartItem.productId == PRODUCT_ID &&
                                it.cartItem.quantity == INITIAL_QUANTITY + INITIAL_QUANTITY
                        }
                    }

                assertTrue(newResult.cartItems.size == 1)
                assertTrue(
                    newResult.cartItems
                        .first()
                        .cartItem.quantity == INITIAL_QUANTITY + 1,
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun givenSingleProduct_whenDecreaseQuantity_thenCartHasDecreasedQuantity() =
        runTest {
            cartRepository.addToCart(PRODUCT_ID, UPDATE_QUANTITY)

            val viewModel = createViewModel()

            viewModel.uiState.test {
                val result =
                    awaitSuccessMatching {
                        cartItems.any {
                            it.cartItem.productId == PRODUCT_ID &&
                                it.cartItem.quantity == UPDATE_QUANTITY
                        }
                    }
                assertTrue(result.cartItems.size == 1)
                assertTrue(
                    result.cartItems
                        .first()
                        .cartItem.quantity == UPDATE_QUANTITY,
                )

                viewModel.onAction(CartAction.DecreaseQuantity(PRODUCT_ID, UPDATE_QUANTITY))

                val newResult =
                    awaitSuccessMatching {
                        cartItems.any {
                            it.cartItem.productId == PRODUCT_ID &&
                                it.cartItem.quantity == INITIAL_QUANTITY
                        }
                    }

                assertTrue(newResult.cartItems.size == 1)
                assertTrue(
                    newResult.cartItems
                        .first()
                        .cartItem.quantity == INITIAL_QUANTITY,
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun givenSingleProduct_whenDecreaseQuantityAZero_thenProductIsRemovedFromCart() =
        runTest {
            cartRepository.addToCart(PRODUCT_ID, INITIAL_QUANTITY)

            val viewModel = createViewModel()

            viewModel.uiState.test {
                val result =
                    awaitSuccessMatching {
                        cartItems.any {
                            it.cartItem.productId == PRODUCT_ID &&
                                it.cartItem.quantity == INITIAL_QUANTITY
                        }
                    }
                assertTrue(result.cartItems.size == 1)
                assertTrue(
                    result.cartItems
                        .first()
                        .cartItem.quantity == INITIAL_QUANTITY,
                )

                viewModel.onAction(CartAction.DecreaseQuantity(PRODUCT_ID, INITIAL_QUANTITY))

                val newResult =
                    awaitSuccessMatching {
                        cartItems.isEmpty() && summary.finalTotal == 0.0
                    }

                assertTrue(newResult.cartItems.isEmpty())

                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun createViewModel(): CartViewModel =
        CartViewModel(
            cartRepository = cartRepository,
            getCartSummaryUseCase = getCartSummaryUseCase,
            updateCartItemUseCase = updateCartItemUseCase,
            getCartItemsWithPromotionsUseCase = getCartItemsWithPromotionsUseCase,
        )

    private suspend fun ReceiveTurbine<CartUiState>.awaitSuccessMatching(
        predicate: CartUiState.Success.() -> Boolean,
    ): CartUiState.Success {
        while (true) {
            when (val item = awaitItem()) {
                is CartUiState.Success -> {
                    if (predicate(item)) {
                        return item
                    }
                }
                is CartUiState.Error -> error("Unexpected error: ${item.message}")
                is CartUiState.Loading -> Unit
            }
        }
    }
}
