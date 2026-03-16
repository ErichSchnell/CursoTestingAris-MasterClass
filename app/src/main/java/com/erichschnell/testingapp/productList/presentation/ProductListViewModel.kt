package com.erichschnell.testingapp.productList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erichschnell.testingapp.productList.domain.models.SortOption
import com.erichschnell.testingapp.productList.domain.usecases.GetProductsUseCase
import com.erichschnell.testingapp.productList.presentation.models.ProductListEvent
import com.erichschnell.testingapp.productList.presentation.models.ProductListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _showFilters = MutableStateFlow(false)
    val showFilters = _showFilters.asStateFlow()


    private val _events = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    init {
        loadProducts()
    }

    fun loadProducts(){
        _uiState.value = ProductListUiState.Loading
        getProductsUseCase()
            .onEach { products ->
                val categories = products.map { it.category }.distinct().sorted()
                _uiState.value = ProductListUiState.Success(
                    products = products,
                    categories = categories,
                    selectedCategory = null,
                    sortOption = SortOption.NONE
                )
            }
            .catch {
                _uiState.value = ProductListUiState.Error(it.message ?: "Unknown error")
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: ProductListEvent){
        val state = uiState.value as? ProductListUiState.Success ?: return

        when(event){
            is ProductListEvent.ButtonClick.FilterBy -> setFilter(event.value, state)
            is ProductListEvent.ButtonClick.SortedBy -> setSort(event.value, state)
            is ProductListEvent.ButtonClick.ShowFilters -> _showFilters.value = event.value
            else -> {}
        }
    }

    private fun setFilter(category: String?, state: ProductListUiState.Success) {
        _uiState.value = state.copy(selectedCategory = category)
    }

    private fun setSort(sort: SortOption, state: ProductListUiState.Success) {
        _uiState.value = state.copy(sortOption = sort)
    }

}