package com.erichschnell.testingapp.presentation.productDetail.screen

import app.cash.turbine.test
import com.erichschnell.testingapp.builders.product
import com.erichschnell.testingapp.core.MainDispatcherRule
import com.erichschnell.testingapp.domain.core.model.AppError
import com.erichschnell.testingapp.domain.repository.CartRepository
import com.erichschnell.testingapp.domain.repository.ProductRepository
import com.erichschnell.testingapp.domain.repository.PromotionRepository
import com.erichschnell.testingapp.domain.usecases.AddToCartUseCase
import com.erichschnell.testingapp.domain.usecases.GetProductDetailWithPromotionUseCase
import com.erichschnell.testingapp.domain.usecases.GetPromotionForProduct
import com.erichschnell.testingapp.domain.util.Clock
import com.erichschnell.testingapp.fakes.FakeCartRepository
import com.erichschnell.testingapp.fakes.FakeProductRepository
import com.erichschnell.testingapp.fakes.FakePromotionRepository
import com.erichschnell.testingapp.fakes.FakeSystemClock
import com.erichschnell.testingapp.presentation.productDetail.models.ProductDetailEvent
import com.erichschnell.testingapp.presentation.productDetail.models.ProductDetailUiAction
import com.erichschnell.testingapp.stubs.FailingProductRepositoryStub
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ProductDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(
        cartRepository: CartRepository = FakeCartRepository(),
        productRepository: ProductRepository = FakeProductRepository(),
        promotionRepository: PromotionRepository = FakePromotionRepository(),
        clock: Clock = FakeSystemClock()
    ): ProductDetailViewModel {
        val getProductDetailWithPromotionUseCase =  GetProductDetailWithPromotionUseCase(
            productRepository = productRepository,
            promotionRepository = promotionRepository,
            getPromotionForProduct = GetPromotionForProduct(),
            clock = clock
        )
        val addToCartUseCase = AddToCartUseCase(
            cartRepository = cartRepository,
            productRepository = productRepository
        )
        return ProductDetailViewModel(
            getProductDetailWithPromotionUseCase = getProductDetailWithPromotionUseCase,
            addToCartUseCase = addToCartUseCase
        )
    }
/*
    ------------------------------------- HECHO --------------------------------
    given viewmodel when initialized then emits ui state with loading true
    given product when loadProduct then emits ui state with product and loading false
    given a non-existent product when loadProduct then update ui state with loading false and null product
    given an UnknownError exception when loadProduct then emits event with UnknownError
    given product when addToCart then add product en repository and emits event with AddProductSuccess

    ------------------------------------- POR HACER  --------------------------------

    given an DatabaseError exception when loadProduct then emits event with DatabaseError
    given an QuantityMustBePositive exception when loadProduct then emits event with QuantityMustBePositive
    given an NotFoundError exception when loadProduct then emits event with NotFoundError
    given an NetworkError exception when loadProduct then emits event with NetworkError
    given an InsufficientStock exception when loadProduct then emits event with InsufficientStock
 */

    @Test
    fun `given viewmodel when initialized then emits ui state with loading true`() =
        runTest (mainDispatcherRule.scheduler) {
            val product = product { withId("p1")}
            val productRepo = FakeProductRepository().apply { setProducts(listOf(product)) }
            val viewModel = createViewModel(productRepository = productRepo)

            viewModel.uiState.test {
                assertTrue(awaitItem().isLoading)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given product when loadProduct then emits ui state with product and loading false`() =
        runTest (mainDispatcherRule.scheduler) {
            val product = product { withId("p1")}
            val productRepo = FakeProductRepository().apply { setProducts(listOf(product)) }
            val viewModel = createViewModel(productRepository = productRepo)

            viewModel.uiState.test {
                assertTrue(awaitItem().isLoading)

                viewModel.loadProduct(product.id)

                val state = awaitItem()
                assertFalse(state.isLoading)
                assertEquals(product, state.item?.product)
                assertNull(state.item?.promotion)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given a non-existent product when loadProduct then update ui state with loading false and null product`() =
        runTest (mainDispatcherRule.scheduler) {
            val product = product { withId("p1")}
            val productRepo = FakeProductRepository().apply { setProducts(listOf(product)) }
            val viewModel = createViewModel(productRepository = productRepo)

            viewModel.uiState.test {
                assertTrue(awaitItem().isLoading)

                viewModel.loadProduct("other-id")

                val state = awaitItem()
                assertFalse(state.isLoading)
                assertNull(state.item)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given an UnknownError exception when loadProduct then emits event with UnknownError`() =
        runTest (mainDispatcherRule.scheduler) {
            val productRepo = FailingProductRepositoryStub(AppError.UnknownError(null))
            val viewModel = createViewModel(productRepository = productRepo)

            viewModel.uiState.test {
                awaitItem()

                viewModel.events.test {
                    awaitItem()
                    viewModel.loadProduct("other-id")

                    val event = awaitItem()
                    assertTrue(event is ProductDetailEvent.Toast.NotFoundError)
                    cancelAndIgnoreRemainingEvents()
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given product when addToCart then add product en repository and emits event with AddProductSuccess`() =
        runTest (mainDispatcherRule.scheduler) {
            val product = product { withId("p1"); withStock(10)}
            val productRepo = FakeProductRepository().apply { setProducts(listOf(product)) }
            val viewModel = createViewModel(productRepository = productRepo)

            viewModel.loadProduct(product.id)

            viewModel.events.test {

                viewModel.onAction(ProductDetailUiAction.AddToCart)

                val state = awaitItem()
                assertTrue(state is ProductDetailEvent.Toast.AddProductSuccess)

                cancelAndIgnoreRemainingEvents()
            }
        }

}