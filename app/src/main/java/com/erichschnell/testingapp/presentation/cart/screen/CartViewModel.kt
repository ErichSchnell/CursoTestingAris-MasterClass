package com.erichschnell.testingapp.presentation.cart.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erichschnell.testingapp.domain.repository.CartRepository
import com.erichschnell.testingapp.domain.usecases.GetCartItemsWithPromotionsUseCase
import com.erichschnell.testingapp.domain.usecases.GetCartSummaryUseCase
import com.erichschnell.testingapp.domain.usecases.UpdateCartItemUseCase
import com.erichschnell.testingapp.presentation.cart.model.CartAction
import com.erichschnell.testingapp.presentation.cart.model.CartEvent
import com.erichschnell.testingapp.presentation.cart.model.CartUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    getCartSummaryUseCase: GetCartSummaryUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    getCartItemsWithPromotionsUseCase: GetCartItemsWithPromotionsUseCase,
) : ViewModel() {

    val uiState: StateFlow<CartUiState> = combine(
        getCartItemsWithPromotionsUseCase(),
        getCartSummaryUseCase()
    ) { cartItems, summary ->
        CartUiState.Success(
            summary = summary,
            cartItems = cartItems,
            isLoading = false,
        )
    }.catch { e ->
        CartUiState.Error(e.message.orEmpty())
    }.stateIn(
        scope = viewModelScope,
        initialValue = CartUiState.Loading,
        started = SharingStarted.WhileSubscribed(5000)
    )

    private val _events = MutableSharedFlow<CartEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()


    fun onAction(action: CartAction) {
        when(action){
            is CartAction.DecreaseQuantity -> decreaseQuantity(action.productId, action.quantity)
            is CartAction.IncreaseQuantity -> increaseQuantity(action.productId, action.quantity)
            is CartAction.RemoveCartItem -> removeFromCart(action.productId)
        }
    }

    private fun updateCartItem(productId: String, newQuantity: Int) {
        viewModelScope.launch {
            try {
                updateCartItemUseCase(productId, newQuantity)
            } catch (e: Exception){
                _events.emit(CartEvent.ShowMessage(e.message.orEmpty()))
            }
        }
    }

    private fun removeFromCart(productId: String) {
        viewModelScope.launch {
            try {
                cartRepository.removeCartItem(productId)
            } catch (e: Exception){
                _events.emit(CartEvent.ShowMessage(e.message.orEmpty()))
            }
        }
    }

    private fun increaseQuantity(productId: String, currentQuantity: Int) {
        updateCartItem(productId, currentQuantity + 1)
    }

    private fun decreaseQuantity(productId: String, currentQuantity: Int) {
        if(currentQuantity > 1) {
            updateCartItem(productId, currentQuantity - 1)
            return
        } else {
            removeFromCart(productId)
        }
    }
}