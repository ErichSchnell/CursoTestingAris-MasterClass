package com.erichschnell.testingapp.detail.presentation

sealed interface ProductDetailEvent {

    data object Toast {
        data object NotFoundError : ProductDetailEvent
        data object NetworkError : ProductDetailEvent
        data object InsufficientStock : ProductDetailEvent
        data object AddProductSuccess : ProductDetailEvent
    }
}