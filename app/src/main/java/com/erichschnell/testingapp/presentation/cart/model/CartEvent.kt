package com.erichschnell.testingapp.presentation.cart.model

sealed interface CartEvent {
    data class ShowMessage(val message: String) : CartEvent
}