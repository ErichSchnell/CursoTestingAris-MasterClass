package com.erichschnell.testingapp.detail.presentation

sealed interface ProductDetailEvent {
    data class ShowMessage(val message: String) : ProductDetailEvent
    data class ShowError(val message: String) : ProductDetailEvent
}