package com.erichschnell.testingapp.cart.presentation.model

sealed interface CartEvent {
    data class ShowMessage(val message: String) : CartEvent
}