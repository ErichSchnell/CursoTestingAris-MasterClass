package com.erichschnell.testingapp.presentation.productList.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erichschnell.testingapp.domain.models.ProductPromotion
import com.erichschnell.testingapp.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.domain.models.SortOption
import com.erichschnell.testingapp.domain.repository.SettingsRepository
import com.erichschnell.testingapp.domain.usecases.GetCartItemsQuantityUseCase
import com.erichschnell.testingapp.domain.usecases.GetProductsUseCase
import com.erichschnell.testingapp.presentation.productList.models.ProductListAction
import com.erichschnell.testingapp.presentation.productList.models.ProductListEvent
import com.erichschnell.testingapp.presentation.productList.models.ProductListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel
    @Inject
    constructor(
        getProductsUseCase: GetProductsUseCase,
        private val settingsRepository: SettingsRepository,
        getCartItemsQuantityUseCase: GetCartItemsQuantityUseCase,
    ) : ViewModel() {
        val uiState: StateFlow<ProductListUiState> =
            combine(
                getProductsUseCase(),
                settingsRepository.selectedCaregory,
                settingsRepository.sortOption,
            ) { products, category, sortOption ->
                var filteredProducts = products

                if (category != null) {
                    filteredProducts = filteredProducts.filter { it.product.category == category }
                }

                val sorted =
                    when (sortOption) {
                        SortOption.PRICE_ASC -> filteredProducts.sortedBy { effectivePrice(it) }
                        SortOption.PRICE_DESC -> filteredProducts.sortedByDescending { effectivePrice(it) }
                        SortOption.NONE -> filteredProducts
                        SortOption.DISCOUNT ->
                            filteredProducts.sortedWith(
                                compareByDescending<ProductWithPromotion> {
                                    effectiveDiscountPercent(it)
                                }.thenBy { it.promotion == null },
                            )
                    }

                val categories = products.map { it.product.category }.distinct().sorted()

                ProductListUiState.Success(
                    products = sorted,
                    categories = categories,
                    selectedCategory = category,
                    sortOption = sortOption,
                ) as ProductListUiState
            }.catch {
                emit(ProductListUiState.Error(it.message.orEmpty()))
            }.stateIn(
                scope = viewModelScope,
                initialValue = ProductListUiState.Loading,
                started = SharingStarted.WhileSubscribed(5000),
            )

        private val _events = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
        val events = _events.asSharedFlow()

        val showFilters: StateFlow<Boolean> =
            settingsRepository.filtersVisible.stateIn(
                scope = viewModelScope,
                initialValue = true,
                started = SharingStarted.WhileSubscribed(5000),
            )

        val cartItemCout =
            getCartItemsQuantityUseCase().stateIn(
                scope = viewModelScope,
                initialValue = 0,
                started = SharingStarted.WhileSubscribed(5000),
            )

        fun onAction(event: ProductListAction) {
            when (event) {
                is ProductListAction.FilterBy -> setCategory(event.value)
                is ProductListAction.SortedBy -> setSort(event.value)
                is ProductListAction.ShowFilters -> setShowFilters(event.value)
                is ProductListAction.ClickProdcut -> navigateToProductDetail(event.value.product.id)
            }
        }

        private fun navigateToProductDetail(id: String) {
            viewModelScope.launch {
                _events.emit(ProductListEvent.Navigate.ProductDetail(id))
            }
        }

        private fun setCategory(category: String?) {
            viewModelScope.launch { settingsRepository.setSelectedCaregory(category) }
        }

        private fun setSort(sort: SortOption) {
            viewModelScope.launch { settingsRepository.setSortOption(sort) }
        }

        private fun setShowFilters(value: Boolean) {
            viewModelScope.launch { settingsRepository.setFiltersVisible(value) }
        }

        private fun effectiveDiscountPercent(item: ProductWithPromotion): Double =
            when (val promo = item.promotion) {
                is ProductPromotion.Percent -> promo.percent
                else -> 0.0
            }

        private fun effectivePrice(item: ProductWithPromotion): Double =
            when (val promo = item.promotion) {
                is ProductPromotion.Percent -> promo.discountedPrice
                else -> item.product.price
            }
    }
