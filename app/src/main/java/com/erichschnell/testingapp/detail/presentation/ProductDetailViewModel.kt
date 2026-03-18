package com.erichschnell.testingapp.detail.presentation

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erichschnell.testingapp.detail.domain.usecase.GetProductDetailWithPromotionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailWithPromotionUseCase: GetProductDetailWithPromotionUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProductDetailEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private var productJob: Job? = null

    fun loadProduct(productId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        productJob?.cancel()
        productJob = getProductDetailWithPromotionUseCase(productId)
            .onEach {
                _uiState.value = _uiState.value.copy(isLoading = false, item = it)
            }
            .catch { e ->
                _uiState.value = _uiState.value.copy(isLoading = false)
                _events.emit(ProductDetailEvent.ShowError(e.message.orEmpty()))
            }
            .launchIn(viewModelScope)


    }

    fun addToCart() {

    }
}