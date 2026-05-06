package com.erichschnell.testingapp.presentation.productList.screen

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.erichschnell.testingapp.core.MainDispatcherRule
import com.erichschnell.testingapp.core.mockwebserver.MiniMarketApiDispatcher
import com.erichschnell.testingapp.core.mockwebserver.MockWebServerUrlHolder
import com.erichschnell.testingapp.core.mockwebserver.rules.MockWebServerRule
import com.erichschnell.testingapp.core.utils.asAsset
import com.erichschnell.testingapp.domain.models.SortOption
import com.erichschnell.testingapp.domain.repository.CartRepository
import com.erichschnell.testingapp.domain.repository.ProductRepository
import com.erichschnell.testingapp.domain.repository.PromotionRepository
import com.erichschnell.testingapp.domain.repository.SettingsRepository
import com.erichschnell.testingapp.domain.usecases.GetCartItemsQuantityUseCase
import com.erichschnell.testingapp.domain.usecases.GetProductsUseCase
import com.erichschnell.testingapp.domain.usecases.GetPromotionForProduct
import com.erichschnell.testingapp.domain.util.Clock
import com.erichschnell.testingapp.presentation.productList.models.ProductListAction
import com.erichschnell.testingapp.presentation.productList.models.ProductListUiState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ProductListViewModelIntregrationTest {

    private companion object {
        const val EXPECTED_PRODUCT_SIZE = 40
        const val DAIRY_CATEGORY = "Dairy"
    }

    @get:Rule(order = 0)
    val mockWebServer = MockWebServerRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val mainDispatcherRule = MainDispatcherRule()

    @Inject
    lateinit var getProductsUseCase: GetProductsUseCase

    @Inject
    lateinit var productRepository: ProductRepository

    @Inject
    lateinit var promotionRepository: PromotionRepository

    @Inject
    lateinit var getPromotionForProduct: GetPromotionForProduct

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var clock: Clock

    @Inject
    lateinit var getCartItemsQuantityUseCase: GetCartItemsQuantityUseCase

    @Inject
    lateinit var cartRepository: CartRepository

    @Before
    fun setUp() = runTest {
        mockWebServer.server.dispatcher = MiniMarketApiDispatcher(
            productJson = "product_list_default.json".asAsset()
        )
        hiltRule.inject()

        settingsRepository.clear()
        cartRepository.clearCart()

        productRepository.refreshProduct()
        promotionRepository.refreshPromotions()
    }

    @After
    fun tearDown() {
        MockWebServerUrlHolder.baseUrl = "http://localhost:8080/"
    }

    @Test
    fun givenSuccessfullApi_whenViewModelLoads_thenShowsProducts() = runTest {
        val viewModel = ProductListViewModel(getProductsUseCase, settingsRepository, getCartItemsQuantityUseCase)

        viewModel.uiState.test {
            val result = awaitSuccessMatching { products.size == 40 }

            assertEquals(40, result.products.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenDairyCategorySelected_whenFiltering_thenOnlyDairyProductsAreShown() = runTest {
        val viewModel = ProductListViewModel(getProductsUseCase, settingsRepository, getCartItemsQuantityUseCase)

        viewModel.uiState.test {
            awaitSuccessMatching { products.size == EXPECTED_PRODUCT_SIZE }

            viewModel.onAction(ProductListAction.FilterBy(DAIRY_CATEGORY))

            val result = awaitSuccessMatching {
                categories.contains(DAIRY_CATEGORY) &&
                        products.isNotEmpty() &&
                        products.all { it.product.category == DAIRY_CATEGORY }
            }

            assertTrue(result.products.size == 5)
            assertTrue(result.categories.contains(DAIRY_CATEGORY))
            assertTrue(result.products.all { it.product.category == DAIRY_CATEGORY })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenProductsLoaded_whenSortingByPriceAsc_thenListIsCorrectlySorted() = runTest {
        val viewModel = ProductListViewModel(getProductsUseCase, settingsRepository, getCartItemsQuantityUseCase)

        viewModel.uiState.test {
            val firstResult = awaitSuccessMatching { products.size == EXPECTED_PRODUCT_SIZE }
            val min = firstResult.products.minBy { it.product.price }.product.price
            val max = firstResult.products.maxBy { it.product.price }.product.price

            viewModel.onAction(ProductListAction.SortedBy(SortOption.PRICE_ASC))

            val result = awaitSuccessMatching { sortOption == SortOption.PRICE_ASC }

            assertTrue(result.sortOption == SortOption.PRICE_ASC)
            assertTrue(result.products.first().product.price == min)
            assertTrue(result.products.last().product.price == max)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<ProductListUiState>.awaitSuccessMatching(
        predicate: ProductListUiState.Success.() -> Boolean
    ): ProductListUiState.Success {
        while ( true ) {
            when(val item = awaitItem()) {
                is ProductListUiState.Success -> { if (predicate(item)) { return item } }
                is ProductListUiState.Error -> error("Unexpected error: ${item.message}")
                is ProductListUiState.Loading -> Unit
            }
        }
    }
}