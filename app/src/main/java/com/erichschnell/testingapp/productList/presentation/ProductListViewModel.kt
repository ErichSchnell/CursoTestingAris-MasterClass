package com.erichschnell.testingapp.productList.presentation

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    init {
        loadProducts()
    }

    fun loadProducts(){
        _uiState.value = ProductListUiState.Loading
        getProductsUseCase()
            .onEach { products ->
                _uiState.value = ProductListUiState.Success(products)
            }
            .catch {
                _uiState.value = ProductListUiState.Error(it.message ?: "Unknown error")
            }
            .launchIn(viewModelScope)
    }
}