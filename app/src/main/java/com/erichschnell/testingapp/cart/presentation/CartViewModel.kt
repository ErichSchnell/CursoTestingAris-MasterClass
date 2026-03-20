package com.erichschnell.testingapp.cart.presentation

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erichschnell.testingapp.cart.domain.repository.CartItemRepository
import com.erichschnell.testingapp.cart.domain.usecase.GetCartItemsWithPromotionsUseCase
import com.erichschnell.testingapp.cart.domain.usecase.GetCartSummaryUseCase
import com.erichschnell.testingapp.cart.domain.usecase.UpdateCartItemUseCase
import com.erichschnell.testingapp.cart.presentation.model.CartEvent
import com.erichschnell.testingapp.cart.presentation.model.CartItemWithPromotion
import com.erichschnell.testingapp.cart.presentation.model.CartUiState
import com.erichschnell.testingapp.productList.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartItemRepository: CartItemRepository,
    private val getCartSummaryUseCase: GetCartSummaryUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val getCartItemsWithPromotionsUseCase: GetCartItemsWithPromotionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CartEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    var cartJob: Job? = null

    init {
        loadCart()
    }

    private fun loadCart() {
        _uiState.value = CartUiState.Loading
        cartJob?.cancel()
        cartJob = combine(
            getCartItemsWithPromotionsUseCase(),
            getCartSummaryUseCase()
        ) { cartItems, summary ->
            _uiState.value = CartUiState.Success(
                summary = summary,
                cartItems = cartItems,
                isLoading = false,
            )
        }.catch { e ->
            _uiState.value = CartUiState.Error(e.message.orEmpty())
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: CartEvent) {
        when(event){
            is CartEvent.Input.DecreaseQuantity -> decreaseQuantity(event.productId, event.quantity)
            is CartEvent.Input.IncreaseQuantity -> increaseQuantity(event.productId, event.quantity)
            is CartEvent.Action.RemoveCartItem -> removeFromCart(event.productId)
            is CartEvent.ShowMessage -> {}
        }
    }

    fun updateCartItem(productId: String, newQuantity: Int) {
        viewModelScope.launch {
            try {
                updateCartItemUseCase(productId, newQuantity)
            } catch (e: Exception){
                _events.emit(CartEvent.ShowMessage(e.message.orEmpty()))
            }
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            try {
                cartItemRepository.removeCartItem(productId)
            } catch (e: Exception){
                _events.emit(CartEvent.ShowMessage(e.message.orEmpty()))
            }
        }
    }

    fun increaseQuantity(productId: String, currentQuantity: Int) {
        updateCartItem(productId, currentQuantity + 1)
    }

    fun decreaseQuantity(productId: String, currentQuantity: Int) {
        if(currentQuantity > 1) {
            updateCartItem(productId, currentQuantity - 1)
            return
        } else {
            removeFromCart(productId)
        }
    }
}