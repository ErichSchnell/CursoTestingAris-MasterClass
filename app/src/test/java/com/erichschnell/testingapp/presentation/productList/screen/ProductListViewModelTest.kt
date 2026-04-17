package com.erichschnell.testingapp.presentation.productList.screen

import app.cash.turbine.test
import com.erichschnell.testingapp.builders.product
import com.erichschnell.testingapp.core.MainDispatcherRule
import com.erichschnell.testingapp.domain.models.SortOption
import com.erichschnell.testingapp.domain.repository.ProductRepository
import com.erichschnell.testingapp.domain.usecases.GetCartItemsQuantityUseCase
import com.erichschnell.testingapp.domain.usecases.GetProductsUseCase
import com.erichschnell.testingapp.domain.usecases.GetPromotionForProduct
import com.erichschnell.testingapp.fakes.FakeCartRepository
import com.erichschnell.testingapp.fakes.FakeProductRepository
import com.erichschnell.testingapp.fakes.FakePromotionRepository
import com.erichschnell.testingapp.fakes.FakeSettingRepository
import com.erichschnell.testingapp.fakes.FakeSystemClock
import com.erichschnell.testingapp.presentation.productList.models.ProductListAction
import com.erichschnell.testingapp.presentation.productList.models.ProductListUiState
import com.erichschnell.testingapp.stubs.FailingProductRepositoryStub
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ProductListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(
        fakeProductRepo: ProductRepository = FakeProductRepository(),
        fakePromotionRepo: FakePromotionRepository = FakePromotionRepository(),
        fakeSettingRepo: FakeSettingRepository = FakeSettingRepository(),
        fakeClock: FakeSystemClock = FakeSystemClock(),
        fakeCartRepo: FakeCartRepository = FakeCartRepository()
    ): ProductListViewModel {
        val getProductsUseCase = GetProductsUseCase(
            productRepository = fakeProductRepo,
            promotionRepository = fakePromotionRepo,
            getPromotionForProduct = GetPromotionForProduct(),
            settingsRepository = fakeSettingRepo,
            clock = fakeClock,
        )
        val settingRepo = fakeSettingRepo
        val getCartItemsQuantityUseCase = GetCartItemsQuantityUseCase(
            cartRepository = fakeCartRepo
        )
        return ProductListViewModel(
            getProductsUseCase = getProductsUseCase,
            settingsRepository = settingRepo,
            getCartItemsQuantityUseCase = getCartItemsQuantityUseCase
        )
    }

    @Test
    fun `given products when initialized then emits success state`() =
        runTest(mainDispatcherRule.scheduler) {
            val product = product { withId("product-id") }
            val fakeRepo = FakeProductRepository().apply { setProducts(listOf(product)) }

            val viewModel = createViewModel(fakeProductRepo = fakeRepo)

            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue(state is ProductListUiState.Success)
                assertEquals(1, (state as ProductListUiState.Success).products.size)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given selected category when set category then filters products`() =
        runTest(mainDispatcherRule.scheduler) {
            val p1 = product { withId("p1"); withCategory("category1") }
            val p2 = product { withId("p2"); withCategory("category1") }
            val p3 = product { withId("p3"); withCategory("category2") }
            val fakeRepo = FakeProductRepository().apply { setProducts(listOf(p1, p2, p3)) }

            val viewModel = createViewModel(fakeProductRepo = fakeRepo)

            viewModel.uiState.test {
                val firstState = awaitItem()
                assertTrue(firstState is ProductListUiState.Success)
                assertEquals(3, (firstState as ProductListUiState.Success).products.size)

                viewModel.onAction(ProductListAction.FilterBy("category1"))

                val secondState = awaitItem()
                assertTrue(secondState is ProductListUiState.Success)
                assertEquals(2, (secondState as ProductListUiState.Success).products.size)
                assertEquals("category1", secondState.selectedCategory)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given price asc sort option when set sort option then sorts by price effective price`() =
        runTest(mainDispatcherRule.scheduler) {
            val p2 = product { withId("p2"); withPrice(150.0) }
            val p1 = product { withId("p1"); withPrice(100.0) }
            val p3 = product { withId("p3"); withPrice(200.0) }
            val fakeRepo = FakeProductRepository().apply { setProducts(listOf(p1, p2, p3)) }

            val viewModel = createViewModel(fakeProductRepo = fakeRepo)

            viewModel.uiState.test {
                awaitItem()
                viewModel.onAction(ProductListAction.SortedBy(SortOption.PRICE_ASC))

                val state = awaitItem()
                assertTrue(state is ProductListUiState.Success)
                assertEquals(3, (state as ProductListUiState.Success).products.size)
                assertEquals(100.0, state.products[0].product.price, 0.001)
                assertEquals(150.0, state.products[1].product.price, 0.001)
                assertEquals(200.0, state.products[2].product.price, 0.001)
                assertEquals(SortOption.PRICE_ASC, state.sortOption)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given repository throws exception when initialized then emits error state`() =
        runTest(mainDispatcherRule.scheduler) {
            val fakeRepo = FailingProductRepositoryStub(Exception("error test"))

            val viewModel = createViewModel(fakeProductRepo = fakeRepo)

            viewModel.uiState.test {
                val state = awaitItem()

                assertTrue(state is ProductListUiState.Error)
                assertEquals("error test", (state as ProductListUiState.Error).message)

                cancelAndIgnoreRemainingEvents()
            }
        }





}