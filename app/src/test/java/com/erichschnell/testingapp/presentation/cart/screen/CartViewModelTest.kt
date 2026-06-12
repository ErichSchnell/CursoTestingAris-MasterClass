package com.erichschnell.testingapp.presentation.cart.screen

import app.cash.turbine.test
import com.erichschnell.testingapp.core.MainDispatcherRule
import com.erichschnell.testingapp.core.builders.cartItem
import com.erichschnell.testingapp.core.builders.product
import com.erichschnell.testingapp.domain.repository.ProductRepository
import com.erichschnell.testingapp.domain.usecases.GetCartItemsWithPromotionsUseCase
import com.erichschnell.testingapp.domain.usecases.GetCartSummaryUseCase
import com.erichschnell.testingapp.domain.usecases.GetPromotionForProduct
import com.erichschnell.testingapp.domain.usecases.UpdateCartItemUseCase
import com.erichschnell.testingapp.fakes.FakeCartItemRepository
import com.erichschnell.testingapp.fakes.FakeProductRepository
import com.erichschnell.testingapp.fakes.FakePromotionRepository
import com.erichschnell.testingapp.fakes.FakeSystemClock
import com.erichschnell.testingapp.presentation.cart.model.CartAction
import com.erichschnell.testingapp.presentation.cart.model.CartEvent
import com.erichschnell.testingapp.presentation.cart.model.CartUiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CartViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(
        fakeProductRepo: ProductRepository = FakeProductRepository(),
        fakePromotionRepo: FakePromotionRepository = FakePromotionRepository(),
        fakeClock: FakeSystemClock = FakeSystemClock(),
        fakeCartRepo: FakeCartItemRepository = FakeCartItemRepository(),
    ): CartViewModel {
        val getCartSummaryUseCase =
            GetCartSummaryUseCase(
                cartRepository = fakeCartRepo,
                productRepository = fakeProductRepo,
                promotionRepository = fakePromotionRepo,
                getPromotionForProductUseCase = GetPromotionForProduct(),
                clock = fakeClock,
            )
        val updateCartItemUseCase =
            UpdateCartItemUseCase(
                cartRepository = fakeCartRepo,
                productRepository = fakeProductRepo,
            )
        val getCartItemsWithPromotionsUseCase =
            GetCartItemsWithPromotionsUseCase(
                cartRepository = fakeCartRepo,
                productRepository = fakeProductRepo,
                promotionRepository = fakePromotionRepo,
                getPromotionForProduct = GetPromotionForProduct(),
                clock = fakeClock,
            )
        return CartViewModel(
            cartRepository = fakeCartRepo,
            getCartSummaryUseCase = getCartSummaryUseCase,
            updateCartItemUseCase = updateCartItemUseCase,
            getCartItemsWithPromotionsUseCase = getCartItemsWithPromotionsUseCase,
        )
    }

/*
    ---------------------------- HECHO ------------------------------
    given cart items when initialized then emits success state
    given cart item with stock when decrease quantity permitted then update quantity in repo and refresh ui state
    given cart item with stock when increase quantity permitted then update quantity in repo and refresh ui state
    given cart item with quantity 1 when try decrease quantity then remove item from repo and refresh ui state

    --------------------------- POR HACER ---------------------------
    given cart item with quantity same its stock when try increase quantity over stock then show error message
    given exception thrown when getting cart items then emits error state
*/

    @Test
    fun `given cart items when initialized then emits success state`() =
        runTest(mainDispatcherRule.scheduler) {
            val product =
                product {
                    withId("product-id")
                    withStock(5)
                    withPrice(2.0)
                }
            val cartItem =
                cartItem {
                    withProductId(product.id)
                    withQuantity(2)
                }

            val productRepo = FakeProductRepository().apply { setProducts(listOf(product)) }
            val cartRepo = FakeCartItemRepository().apply { setCartItems(listOf(cartItem)) }

            val viewModel = createViewModel(fakeProductRepo = productRepo, fakeCartRepo = cartRepo)

            viewModel.uiState.test {
                val state = awaitItem()

                assertTrue(state is CartUiState.Success)
                assertEquals(1, (state as CartUiState.Success).cartItems.size)
                assertEquals(4.0, state.summary.subtotal, 0.0)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given cart item with stock when decrease quantity permitted then update quantity in repo and refresh ui state`() =
        runTest(mainDispatcherRule.scheduler) {
            val product =
                product {
                    withId("product-id")
                    withStock(5)
                }
            val cartItem =
                cartItem {
                    withProductId(product.id)
                    withQuantity(3)
                }

            val productRepo = FakeProductRepository().apply { setProducts(listOf(product)) }
            val cartRepo = FakeCartItemRepository().apply { setCartItems(listOf(cartItem)) }

            val viewModel = createViewModel(fakeProductRepo = productRepo, fakeCartRepo = cartRepo)

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(CartAction.DecreaseQuantity(cartItem.productId, cartItem.quantity))

                val secondState = awaitItem()
                assertTrue(secondState is CartUiState.Success)
                assertEquals(cartItem.quantity - 1, (secondState as CartUiState.Success).cartItems[0].cartItem.quantity)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given cart item with stock when increase quantity permitted then update quantity in repo and refresh ui state`() =
        runTest(mainDispatcherRule.scheduler) {
            val product =
                product {
                    withId("product-id")
                    withStock(5)
                }
            val cartItem =
                cartItem {
                    withProductId(product.id)
                    withQuantity(3)
                }

            val productRepo = FakeProductRepository().apply { setProducts(listOf(product)) }
            val cartRepo = FakeCartItemRepository().apply { setCartItems(listOf(cartItem)) }

            val viewModel = createViewModel(fakeProductRepo = productRepo, fakeCartRepo = cartRepo)

            viewModel.uiState.test {
                awaitItem()

                viewModel.onAction(CartAction.IncreaseQuantity(cartItem.productId, cartItem.quantity))

                val secondState = awaitItem()
                assertTrue(secondState is CartUiState.Success)
                assertEquals(cartItem.quantity + 1, (secondState as CartUiState.Success).cartItems[0].cartItem.quantity)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given cart item with quantity 1 when try decrease quantity then remove item from repo and refresh ui state`() =
        runTest(mainDispatcherRule.scheduler) {
            val product =
                product {
                    withId("product-id")
                    withStock(5)
                }
            val cartItem =
                cartItem {
                    withProductId(product.id)
                    withQuantity(1)
                }

            val productRepo = FakeProductRepository().apply { setProducts(listOf(product)) }
            val cartRepo = FakeCartItemRepository().apply { setCartItems(listOf(cartItem)) }

            val viewModel = createViewModel(fakeProductRepo = productRepo, fakeCartRepo = cartRepo)

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(CartAction.DecreaseQuantity(cartItem.productId, cartItem.quantity))

                val secondState = awaitItem()
                assertTrue(secondState is CartUiState.Success)
                assertEquals(0, (secondState as CartUiState.Success).cartItems.size)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given cart item with quantity same its stock when try increase quantity over stock then show error message`() =
        runTest(mainDispatcherRule.scheduler) {
            val product =
                product {
                    withId("product-id")
                    withStock(5)
                }
            val cartItem =
                cartItem {
                    withProductId(product.id)
                    withQuantity(5)
                }

            val productRepo = FakeProductRepository().apply { setProducts(listOf(product)) }
            val cartRepo = FakeCartItemRepository().apply { setCartItems(listOf(cartItem)) }

            val viewModel = createViewModel(fakeProductRepo = productRepo, fakeCartRepo = cartRepo)

            viewModel.events.test {
                viewModel.onAction(CartAction.IncreaseQuantity(cartItem.productId, cartItem.quantity))

                val event = awaitItem()
                assertTrue(event is CartEvent.ShowMessage)

                cancelAndIgnoreRemainingEvents()
            }
        }
}
