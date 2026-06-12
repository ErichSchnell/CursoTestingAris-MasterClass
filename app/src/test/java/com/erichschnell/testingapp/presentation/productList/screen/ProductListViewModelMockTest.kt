package com.erichschnell.testingapp.presentation.productList.screen

import com.erichschnell.testingapp.core.MainDispatcherRule
import com.erichschnell.testingapp.domain.core.model.ThemeMode
import com.erichschnell.testingapp.domain.models.SortOption
import com.erichschnell.testingapp.domain.repository.ProductRepository
import com.erichschnell.testingapp.domain.repository.SettingsRepository
import com.erichschnell.testingapp.domain.usecases.GetCartItemsQuantityUseCase
import com.erichschnell.testingapp.domain.usecases.GetProductsUseCase
import com.erichschnell.testingapp.domain.usecases.GetPromotionForProduct
import com.erichschnell.testingapp.fakes.FakeCartItemRepository
import com.erichschnell.testingapp.fakes.FakeProductRepository
import com.erichschnell.testingapp.fakes.FakePromotionRepository
import com.erichschnell.testingapp.fakes.FakeSystemClock
import com.erichschnell.testingapp.presentation.productList.models.ProductListAction
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ProductListViewModelMockTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val settingsRepository: SettingsRepository =
        mockk(relaxed = true) {
            every { selectedCaregory } returns flowOf(null)
            every { sortOption } returns flowOf(SortOption.NONE)
            every { inStockOnly } returns flowOf(false)
            every { filtersVisible } returns flowOf(false)
            every { showTaxes } returns flowOf(false)
            every { themeMode } returns flowOf(ThemeMode.SYSTEM)
        }

    private fun createViewModel(
        fakeProductRepo: ProductRepository = FakeProductRepository(),
        fakePromotionRepo: FakePromotionRepository = FakePromotionRepository(),
        fakeClock: FakeSystemClock = FakeSystemClock(),
        fakeCartRepo: FakeCartItemRepository = FakeCartItemRepository(),
    ): ProductListViewModel {
        val getProductsUseCase =
            GetProductsUseCase(
                productRepository = fakeProductRepo,
                promotionRepository = fakePromotionRepo,
                getPromotionForProduct = GetPromotionForProduct(),
                settingsRepository = settingsRepository,
                clock = fakeClock,
            )
        val getCartItemsQuantityUseCase =
            GetCartItemsQuantityUseCase(
                cartRepository = fakeCartRepo,
            )
        return ProductListViewModel(
            getProductsUseCase = getProductsUseCase,
            settingsRepository = settingsRepository,
            getCartItemsQuantityUseCase = getCartItemsQuantityUseCase,
        )
    }

    @Test
    fun `given category when set category then delegate to setting repository`() =
        runTest(mainDispatcherRule.scheduler) {
            val viewModel = createViewModel()
            val category = "pasta"

            viewModel.onAction(ProductListAction.FilterBy(category))

            coVerify(exactly = 1) { settingsRepository.setSelectedCaregory(category) }
        }

    @Test
    fun `given sort option when set sort option then delegate to setting repository`() =
        runTest(mainDispatcherRule.scheduler) {
            val viewModel = createViewModel()
            val sortOption = SortOption.PRICE_ASC

            viewModel.onAction(ProductListAction.SortedBy(sortOption))

            coVerify(exactly = 1) { settingsRepository.setSortOption(sortOption) }
        }

    @Test
    fun `given show filters when set show filters then delegate to setting repository`() =
        runTest(mainDispatcherRule.scheduler) {
            val viewModel = createViewModel()
            val showFilters = true

            viewModel.onAction(ProductListAction.ShowFilters(showFilters))

            coVerify(exactly = 1) { settingsRepository.setFiltersVisible(showFilters) }
        }
}
