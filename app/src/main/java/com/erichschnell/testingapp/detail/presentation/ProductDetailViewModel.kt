package com.erichschnell.testingapp.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erichschnell.testingapp.cart.domain.usecase.AddToCartUseCase
import com.erichschnell.testingapp.core.domain.model.AppError
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailWithPromotionUseCase: GetProductDetailWithPromotionUseCase,
    private val addToCartUseCase: AddToCartUseCase
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
                if (e is AppError){
                    handleError(e)
                } else {
                    handleError(AppError.UnknownError(null))
                }
            }
            .launchIn(viewModelScope)
    }

    fun addToCart() {
        val productId = _uiState.value.item?.product?.id ?: return
        viewModelScope.launch {
            try {
                addToCartUseCase(productId)
                _events.emit(ProductDetailEvent.Toast.AddProductSuccess)
            } catch (e: AppError) {
                handleError(e)
            } catch (e: Exception) {

            }
        }
    }

    private suspend fun handleError(e: AppError) {
        val event = when(e){
            is AppError.UnknownError,
            AppError.DatabaseError,
            AppError.Validation.QuantityMustBePositive,
            AppError.NotFoundError -> ProductDetailEvent.Toast.NotFoundError

            AppError.NetworkError -> ProductDetailEvent.Toast.NetworkError

            is AppError.Validation.InsufficientStock -> ProductDetailEvent.Toast.InsufficientStock
        }
        _events.emit(event)
    }
}
