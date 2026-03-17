package com.erichschnell.testingapp.productList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erichschnell.testingapp.productList.domain.models.ProductPromotion
import com.erichschnell.testingapp.productList.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.productList.domain.models.SortOption
import com.erichschnell.testingapp.productList.domain.repository.SettingsRepository
import com.erichschnell.testingapp.productList.domain.usecases.GetProductsUseCase
import com.erichschnell.testingapp.productList.presentation.models.ProductListEvent
import com.erichschnell.testingapp.productList.presentation.models.ProductListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    val showFilters: StateFlow<Boolean> = settingsRepository.filtersVisible.stateIn(
        scope = viewModelScope,
        initialValue = true,
        started = SharingStarted.WhileSubscribed(5000)
    )



    private var productsJob: Job? = null

    init {
        loadProducts()
    }

    fun loadProducts(){
        _uiState.value = ProductListUiState.Loading

        productsJob?.cancel()
        productsJob = combine(
            getProductsUseCase(),
            settingsRepository.selectedCaregory,
            settingsRepository.sortOption
        ) { products, category, sortOption ->
            var filteredProducts = products

            if (category != null) {
                filteredProducts = filteredProducts.filter { it.product.category == category }
            }

            val sorted = when(sortOption) {
                SortOption.PRICE_ASC -> filteredProducts.sortedBy { effectivePrice(it) }
                SortOption.PRICE_DESC -> filteredProducts.sortedByDescending { effectivePrice(it) }
                SortOption.NONE -> filteredProducts
                SortOption.DISCOUNT -> filteredProducts.sortedWith (
                    compareByDescending<ProductWithPromotion> {
                        effectiveDiscountPercent(it)
                    }.thenBy { it.promotion == null }
                )
            }

            val categories = products.map { it.product.category }.distinct().sorted()

            ProductListUiState.Success(
                products = sorted,
                categories = categories,
                selectedCategory = category,
                sortOption = sortOption
            )
        }.onEach { state ->
            _uiState.value = state
        }
        .catch {
            _uiState.value = ProductListUiState.Error(it.message ?: "Unknown error")
        }
        .launchIn(viewModelScope)
    }

    fun onEvent(event: ProductListEvent){
        val state = uiState.value as? ProductListUiState.Success ?: return

        when(event){
            is ProductListEvent.ButtonClick.FilterBy -> setCategory(event.value)
            is ProductListEvent.ButtonClick.SortedBy -> setSort(event.value)
            is ProductListEvent.ButtonClick.ShowFilters -> setShowFilters(event.value)
            is ProductListEvent.ButtonClick.ClickProdcut -> {}
            else -> {}
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

    private fun effectiveDiscountPercent(item: ProductWithPromotion): Double {
        return when(val promo = item.promotion){
            is ProductPromotion.Percent -> promo.percent
            else -> 0.0
        }
    }

    private fun effectivePrice(item: ProductWithPromotion): Double {
        return when(val promo = item.promotion){
            is ProductPromotion.Percent -> promo.discountedPrice
            else -> item.product.price
        }
    }

}